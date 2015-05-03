/* $Id: FTGLExtrdFont.java,v 1.2 2004/11/16 21:25:13 joda Exp $ */
package org.lwjgl.font.glfont;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;

import org.lwjgl.font.FTBBox;
import org.lwjgl.font.glyph.FTExtrdGlyph;
import org.lwjgl.font.glyph.FTGlyph;

/**
 * FTGLExtrdFont is a specialisation of the FTFont class for handling extruded
 * Polygon fonts
 * 
 * @see FTFont
 * @see FTGLPolygonFont
 */
public class FTGLExtrdFont extends FTFont {

	private float depth = 1.0f;

	/**
	 * Open and read a font file. Uses the standard FontRenderContext.
	 * 
	 * @param fontname
	 *            font file name.
	 */
	public FTGLExtrdFont(final String fontname) {
		this(Font.decode(fontname));
	}

	/**
	 * Creates a new FTGLExtrdFont with the specified {@link Font}. Uses the
	 * standard FontRenderContext.
	 * 
	 * @param font
	 *            The font from which to create this ExtrdFont.
	 */
	public FTGLExtrdFont(final Font font) {
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
	public FTGLExtrdFont(final String fontname, final FontRenderContext context) {
		this(Font.decode(fontname), context);
	}

	/**
	 * Creates a new FTGLExtrdFont with the specified {@link Font}. Uses the
	 * given FontRenderContext.
	 * 
	 * @param font
	 *            The font from which to create this ExtrdFont.
	 * @param context
	 *            The rendercontext.
	 */
	public FTGLExtrdFont(final Font font, final FontRenderContext context) {
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
	 * Sets the depth of this font.
	 * 
	 * @param depth
	 *            The new depth.
	 */
	public void setDepth(float depth) {
		this.depth = depth;
		glyphCache.clear();
	}

	/**
	 * Returns the depth of this font.
	 * 
	 * @return the depth of this font
	 */
	public float getDepth() {
		return depth;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FTBBox getBBox(String string) {
		FTBBox box = super.getBBox(string);
		box.setDepth(-depth);
		return box;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FTGlyph makeGlyph(Shape ftGlyph, float advance) {
		if (ftGlyph != null) {
			FTExtrdGlyph tempGlyph = new FTExtrdGlyph(ftGlyph, depth, advance);
			return tempGlyph;
		}
		return null;
	}

}