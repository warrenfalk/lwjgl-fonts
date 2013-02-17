/* $Id */
package org.lwjgl.font.glfont;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;

import org.lwjgl.font.glyph.FTBitmapGlyph;
import org.lwjgl.font.glyph.FTGlyph;
import org.lwjgl.opengl.GL11;

/**
 * FTGLBitmapFont is a specialisation of the FTFont class for handling Bitmap
 * fonts
 * 
 * @see FTFont
 */
public class FTGLBitmapFont extends FTFont {

	/**
	 * Open and read a font file. Sets Error flag.
	 * 
	 * @param fontname
	 *            font file name.
	 */
	public FTGLBitmapFont(String fontname) {
		this(Font.decode(fontname));
	}

	/**
	 * Creates a new font from the given font with the standard
	 * FontRenderContext.
	 * 
	 * @param font
	 *            The font from which to construct this FTFont.
	 */
	public FTGLBitmapFont(Font font) {
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
	public FTGLBitmapFont(final String fontname, final FontRenderContext context) {
		this(Font.decode(fontname), context);
	}

	/**
	 * Creates the FTFont from the given {@link Font} and renders it to the
	 * given fontrendercontext.
	 * 
	 * @param font
	 *            The font to create this FTFont from.
	 * @param context
	 *            The rendercontext to render the glyphs with.
	 */
	public FTGLBitmapFont(final Font font, final FontRenderContext context) {
		super(font, context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(String string) {
		GL11.glPushClientAttrib(GL11.GL_CLIENT_PIXEL_STORE_BIT);
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);

		GL11.glPixelStorei(GL11.GL_UNPACK_LSB_FIRST, GL11.GL_FALSE);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		GL11.glDisable(GL11.GL_BLEND);

		super.render(string);

		GL11.glPopAttrib();
		GL11.glPopClientAttrib();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FTGlyph makeGlyph(Shape ftGlyph, float advance) {

		if (ftGlyph != null) {
			FTBitmapGlyph tempGlyph = new FTBitmapGlyph(ftGlyph, advance);
			return tempGlyph;
		}

		return null;
	}

}