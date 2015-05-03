/* $Id: FTTextureGlyph.java,v 1.3 2005/07/27 23:14:31 joda Exp $ */
package org.lwjgl.font.glyph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.font.FTGlyphContainer;
import org.lwjgl.font.util.Vector3f;
import org.lwjgl.opengl.GL11;

/**
 * FTTextureGlyph is a specialisation of FTGlyph for creating texture glyphs.
 * 
 * @see FTGlyphContainer
 */
public class FTTextureGlyph extends FTGlyph {

	/**
	 * The width of the glyph 'image'
	 */
	private int destWidth = 0;

	/**
	 * The height of the glyph 'image'
	 */
	private int destHeight = 0;

	/**
	 * Vector from the pen position to the topleft corner of the pixmap
	 */
	private Vector3f pos = new Vector3f();

	/**
	 * The texture co-ords of this glyph within the texture.
	 */
	private Vector3f[] uv = new Vector3f[2];

	/**
	 * The texture index that this glyph is contained in.
	 */
	private int glTextureID;

	/**
	 * The texture index of the currently active texture We call glGetIntegerv(
	 * GL_TEXTURE_2D_BINDING, activeTextureID); to get the currently active
	 * texture to try to reduce the number of texture bind operations
	 */
	private int[] activeTextureID = new int[1];

	private int xOffset = 0;
	private int yOffset = 0;
	private int width = 0;
	private int height = 0;

	/**
	 * Constructor
	 * 
	 * @param glyph
	 *            The Freetype glyph to be processed
	 * @param id
	 *            The id of the texture that this glyph will be drawn in
	 * @param xOffset
	 *            The x offset into the parent texture to draw this glyph
	 * @param yOffset
	 *            The y offset into the parent texture to draw this glyph
	 * @param width
	 *            The width of the parent texture
	 * @param height
	 *            The height (number of rows) of the parent texture
	 */
	public FTTextureGlyph(Shape glyph, int id, int xOffset, int yOffset,
			int width, int height) {
		super(glyph);
		glTextureID = id;
		activeTextureID[0] = -1;

		this.xOffset = xOffset;
		this.yOffset = yOffset;

		this.width = width;
		this.height = height;

		uv[0] = new Vector3f();
		uv[1] = new Vector3f();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createDisplayList() {
		Rectangle bounds = glyph.getBounds();

		destWidth = bounds.width;
		destHeight = bounds.height;

		if (destWidth == 0 || destHeight == 0)
			return;

		// int[] rgbarray = new int[this.destWidth*this.destHeight];

		BufferedImage image = new BufferedImage(bounds.width, bounds.height,
				BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.scale(1.0f, -1.0f);
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, bounds.width, bounds.height);
		g2d.setColor(Color.WHITE);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.translate(-bounds.getX(), -bounds.getMaxY());
		g2d.fill(glyph);

		// image.getRGB(0, 0, this.destWidth, this.destHeight, rgbarray, 0,
		// this.destWidth);

		byte[] array = new byte[destWidth * destHeight];

		for (int i = 0; i < destHeight; i++)
			for (int j = 0; j < destWidth; j++)
				array[i * destWidth + j] = (byte) image.getRGB(j, i);

		if (destWidth != 0 && destHeight != 0) {
			GL11.glPushClientAttrib(GL11.GL_CLIENT_PIXEL_STORE_BIT);
			GL11.glPixelStorei(GL11.GL_UNPACK_LSB_FIRST, GL11.GL_FALSE);
			GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTextureID);
			ByteBuffer bb = ByteBuffer.allocateDirect(array.length).order(
					ByteOrder.LITTLE_ENDIAN);
			bb.put(array).flip();
			GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, xOffset, yOffset,
					destWidth, destHeight, GL11.GL_ALPHA,
					GL11.GL_UNSIGNED_BYTE, bb);

			GL11.glPopClientAttrib();
			// 0
			// +----+
			// | |
			// | |
			// | |
			// +----+
			// 1

			uv[0].x = (float) xOffset / width;
			uv[0].y = (float) yOffset / height;
			uv[1].x = (float) (xOffset + destWidth) / width;
			uv[1].y = (float) (yOffset + destHeight) / height;
			assert xOffset == (uv[0].x * width) : "floating point conversion problem (xOffset)";
			assert yOffset == (uv[0].y * height) : "floating point conversion problem (yOffset)";
			assert xOffset + destWidth == (uv[1].x * width) : "floating point conversion problem (xOffset+width)";
			assert yOffset + destHeight == (uv[1].y * height) : "floating point conversion problem (yOffset+height)";
		}

		pos.x = (float) glyph.getBounds().getMinX();
		pos.y = (float) glyph.getBounds().getMaxY();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float render(final float x, final float y, final float z) {
		if (destWidth == 0 || destHeight == 0)
			return advance;

		activeTextureID[0] = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
		if (activeTextureID[0] != glTextureID)
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTextureID);

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(uv[0].x, uv[0].y);
		GL11.glVertex2f(x + pos.x, y + pos.y);

		GL11.glTexCoord2f(uv[0].x, uv[1].y);
		GL11.glVertex2f(x + pos.x, y + pos.y - destHeight);

		GL11.glTexCoord2f(uv[1].x, uv[1].y);
		GL11.glVertex2f(x + destWidth + pos.x, y + pos.y - destHeight);

		GL11.glTexCoord2f(uv[1].x, uv[0].y);
		GL11.glVertex2f(x + destWidth + pos.x, y + pos.y);
		GL11.glEnd();

		return advance;
	}

}