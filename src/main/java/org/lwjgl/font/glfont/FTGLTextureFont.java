/* $Id: FTGLTextureFont.java,v 1.3 2004/11/18 17:55:46 joda Exp $ */
package org.lwjgl.font.glfont;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.font.FTBBox;
import org.lwjgl.font.glyph.FTGlyph;
import org.lwjgl.font.glyph.FTTextureGlyph;
import org.lwjgl.font.util.FTMath;
import org.lwjgl.opengl.GL11;

/**
 * FTGLTextureFont is a specialisation of the FTFont class for handling Texture
 * mapped fonts
 * 
 * @see FTFont
 */
public class FTGLTextureFont extends FTFont {

	/**
	 * The maximum texture dimension on this OpenGL implemetation
	 */
	private int[] maxTextSize = { 0 }; // TODO was GLsizei

	/**
	 * The minimum texture width required to hold the glyphs
	 */
	private int textureWidth = 0; // TODO was GLsizei

	/**
	 * The minimum texture height required to hold the glyphs
	 */
	private int textureHeight = 0; // TODO was GLsizei

	/**
	 * An array of texture ids
	 */
	private List<IntBuffer> textureIDList = new LinkedList<IntBuffer>();

	/**
	 * The max height for glyphs in the current font
	 */
	private int glyphHeight = 0;

	/**
	 * The max width for glyphs in the current font
	 */
	private int glyphWidth = 0;

	/**
	 * A value to be added to the height and width to ensure that glyphs don't
	 * overlap in the texture
	 */
	private int padding = 3;

	/**
	 * 
	 */
	private int numGlyphs;

	/**
	 */
	private int remGlyphs;

	/**
	 */
	private int xOffset = 0;

	/**
	 */
	private int yOffset = 0;

	/**
	 * Open and read a font file. Uses the standard FontRenderContext.
	 * 
	 * @param fontname
	 *            font file name.
	 */
	public FTGLTextureFont(String fontname) {
		this(Font.decode(fontname));
	}

	/**
	 * Creates a new FTGLTextureFont with the given {@link Font}. Uses the
	 * standard FontRenderContext.
	 * 
	 * @param font
	 *            The font to create from this FTFont.
	 */
	public FTGLTextureFont(Font font) {
		this(font, FTFont.STANDARDCONTEXT);
	}

	/**
	 * Reads the font from the given font name and renders the font to the given
	 * fontrendercontext.
	 * 
	 * @param fontname
	 *            The fontname.
	 * @param context
	 *            The FontRenderContext to render with.
	 */
	public FTGLTextureFont(final String fontname,
			final FontRenderContext context) {
		this(Font.decode(fontname), context);
	}

	/**
	 * Creates the FTFont from the given {@link Font}.
	 * 
	 * @param font
	 * @param context
	 */
	public FTGLTextureFont(final Font font, final FontRenderContext context) {
		super(font, context);
		remGlyphs = this.font.getNumGlyphs();
		numGlyphs = this.font.getNumGlyphs();
		// this.precache();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		super.dispose();
		deleteTextures();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean faceSize(final float size) {
		return this.faceSize(size, new FontRenderContext(null, true, true));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean faceSize(final float size, FontRenderContext context) {
		if (!textureIDList.isEmpty()) {
			deleteTextures();
			remGlyphs = font.getNumGlyphs();
			numGlyphs = font.getNumGlyphs();
		}

		return super.faceSize(size, context);
	}

	private void deleteTextures() {
		int size = textureIDList.size();
		IntBuffer textureIDs = ByteBuffer.allocateDirect(size * 4)
				.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
		for (int i = 0; i < size; i++)
			textureIDs.put(textureIDList.get(i).get(0));
		textureIDs.flip();
		GL11.glDeleteTextures(textureIDs);
		textureIDList.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FTGlyph makeGlyph(Shape ftGlyph, float advance) {

		if (ftGlyph != null) {
			// prevents overwriting previous characters if the last character
			// was very small
			glyphHeight = Math.max(glyphHeight, ftGlyph.getBounds().height);
			glyphWidth = ftGlyph.getBounds().width;

			// TODO maybe workaround.
			if (glyphHeight == 0 || glyphWidth == 0)
				return new FTTextureGlyph(ftGlyph, 0, 0, 0, 0, 0);

			if (textureIDList.isEmpty()) {
				textureIDList.add(createTexture());
				xOffset = padding;
				yOffset = padding;
			}

			if (xOffset > (textureWidth - padding - glyphWidth)) {
				xOffset = padding;
				yOffset += glyphHeight;

				if (yOffset > (textureHeight - padding - glyphHeight)) {
					textureIDList.add(createTexture());
					yOffset = padding;
				}
			}

			Object textureID = textureIDList.get(textureIDList.size() - 1);
			FTTextureGlyph tempGlyph = new FTTextureGlyph(ftGlyph,
					((IntBuffer) textureID).get(0), xOffset, yOffset,
					textureWidth, textureHeight);
			FTBBox box = tempGlyph.getBBox();
			assert box.upperX - box.lowerX <= glyphWidth : "Character width is out of bounds."
					+ (box.upperX - box.lowerX) + " max:" + glyphWidth;
			assert box.upperY - box.lowerY <= glyphHeight : "Character height is out of bounds."
					+ (box.upperY - box.lowerY) + " max:" + glyphWidth;
			xOffset += glyphWidth + padding;

			--remGlyphs;
			return tempGlyph;
		}

		return null;
	}

	/**
	 * Get the size of a block of memory required to layout the glyphs
	 * Calculates a width and height based on the glyph sizes and the number of
	 * glyphs. It over estimates.
	 */
	private final void calculateTextureSize() {
		if (maxTextSize[0] == 0) {
			maxTextSize[0] = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
			System.err.println("MaxTexture:" + maxTextSize[0]);
		}

		textureWidth = FTMath.nextPowerOf2((remGlyphs * glyphWidth)
				+ (padding * 2));
		if (textureWidth > maxTextSize[0])
			textureWidth = maxTextSize[0];

		// TODO: Validate these calculations
		int h = (textureWidth - padding * 2) / glyphWidth;

		textureHeight = FTMath
				.nextPowerOf2(((numGlyphs / h) + 1) * glyphHeight);
		textureHeight = textureHeight > maxTextSize[0] ? maxTextSize[0]
				: textureHeight;
	}

	/**
	 * Creates a 'blank' OpenGL texture object. The format is GL_ALPHA and the
	 * params are GL_TEXTURE_WRAP_S = GL_CLAMP GL_TEXTURE_WRAP_T = GL_CLAMP
	 * GL_TEXTURE_MAG_FILTER = GL_LINEAR GL_TEXTURE_MIN_FILTER = GL_LINEAR Note
	 * that mipmapping is NOT used
	 */
	private final IntBuffer createTexture() // TODO was GLuint
	{
		calculateTextureSize();

		int totalMemory = textureWidth * textureHeight;
		System.err.println("Real width/height:" + textureWidth + "x"
				+ textureHeight);
		ByteBuffer textureMemory = ByteBuffer.allocateDirect(totalMemory)
				.order(ByteOrder.LITTLE_ENDIAN);
		// memset( textureMemory, 0, totalMemory);

		IntBuffer textID = ByteBuffer.allocateDirect(4)
				.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
		GL11.glGenTextures(textID);
		textID.rewind();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textID.get(0));
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
				GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
				GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				GL11.GL_LINEAR);

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_ALPHA, textureWidth,
				textureHeight, 0, GL11.GL_ALPHA, GL11.GL_UNSIGNED_BYTE,
				textureMemory);

		// delete [] textureMemory;

		return textID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(final String string) {
		assert renderTexture(0);
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // GL_ONE

		super.render(string);

		GL11.glPopAttrib();
	}

	/**
	 * Render to texture.
	 * 
	 * @param index
	 *            index of the texture
	 * @return always <code>true</code>
	 */
	public boolean renderTexture(int index) {
		if (index >= 0 && index < textureIDList.size()) {
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT
					| GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_CURRENT_BIT);

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // GL_ONE
			GL11.glDepthFunc(GL11.GL_ALWAYS);
			int textureID = textureIDList.get(index).get(0);
			int[] activeTextureID = new int[1];
			activeTextureID[0] = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
			if (activeTextureID[0] != textureID)
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
			float sizeX = Math.min(256f, textureWidth);
			float sizeY = (sizeX / textureWidth) * textureHeight;
			sizeX /= 2f;
			sizeY /= 2f;
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor4f(1f, 0f, 0f, 1f);
			GL11.glVertex2f(-sizeX, -sizeY);
			GL11.glVertex2f(-sizeX, sizeY);
			GL11.glVertex2f(sizeX, sizeY);
			GL11.glVertex2f(sizeX, -sizeY);
			GL11.glEnd();
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0f, 0f);
			GL11.glVertex2f(-sizeX, -sizeY);

			GL11.glTexCoord2f(0f, 1f);
			GL11.glVertex2f(-sizeX, sizeY);

			GL11.glTexCoord2f(1f, 1f);
			GL11.glVertex2f(sizeX, sizeY);

			GL11.glTexCoord2f(1f, 0f);
			GL11.glVertex2f(sizeX, -sizeY);
			GL11.glEnd();
			GL11.glPopAttrib();
		}
		return true;
	}
}