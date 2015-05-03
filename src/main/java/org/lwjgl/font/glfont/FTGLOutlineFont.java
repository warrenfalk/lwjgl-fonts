/* $Id: FTGLOutlineFont.java,v 1.1 2004/11/12 19:44:23 funsheep Exp $ */
package org.lwjgl.font.glfont;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;

import org.lwjgl.font.glyph.FTGlyph;
import org.lwjgl.font.glyph.FTOutlineGlyph;
import org.lwjgl.opengl.GL11;

/**
 * FTGLOutlineFont is a specialisation of the FTFont class for handling Vector
 * Outline fonts
 * 
 * @see FTFont
 */
public class FTGLOutlineFont extends FTFont {

	/**
	 * Open and read a font file. Sets Error flag.
	 * 
	 * @param fontname
	 *            font file name.
	 */
	public FTGLOutlineFont(final String fontname) {
		this(Font.decode(fontname));
	}

	/**
	 * Creates a new FTGLOutlineFont from the given {@link Font}.
	 * 
	 * @param font
	 *            The font from to create a new OutlineFont.
	 */
	public FTGLOutlineFont(final Font font) {
		this(font, FTFont.STANDARDCONTEXT);
	}

	/**
	 * Open and reads a font file. Renders the font with the given
	 * FontRenderContext.
	 * 
	 * @param fontname
	 *            The name of the font.
	 * @param context
	 *            The rendercontext.
	 */
	public FTGLOutlineFont(final String fontname,
			final FontRenderContext context) {
		this(Font.decode(fontname), context);
	}

	/**
	 * Creates a new FTGLOutlineFont with the specified {@link Font}. Uses the
	 * given FontRenderContext.
	 * 
	 * @param font
	 *            The font from which to create this FTFont.
	 * @param context
	 *            The rendercontext.
	 */
	public FTGLOutlineFont(final Font font, final FontRenderContext context) {
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
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_HINT_BIT
				| GL11.GL_LINE_BIT | GL11.GL_COLOR_BUFFER_BIT);

		GL11.glDisable(GL11.GL_TEXTURE_2D);

		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // GL_ONE

		super.render(string);

		GL11.glPopAttrib();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final FTGlyph makeGlyph(Shape ftGlyph, float advance) {
		if (ftGlyph != null) {
			FTOutlineGlyph tempGlyph = new FTOutlineGlyph(ftGlyph, advance);
			return tempGlyph;
		}
		return null;
	}

}