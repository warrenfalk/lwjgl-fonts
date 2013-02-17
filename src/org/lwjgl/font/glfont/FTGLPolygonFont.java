/* $Id: FTGLPolygonFont.java,v 1.1 2004/11/12 19:44:23 funsheep Exp $ */
package org.lwjgl.font.glfont;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;

import org.lwjgl.font.glyph.FTGlyph;
import org.lwjgl.font.glyph.FTPolyGlyph;


/**
 * FTGLPolygonFont is a specialisation of the FTFont class for handling tesselated Polygon Mesh
 * fonts.
 * @see FTFont
 */
public class FTGLPolygonFont extends FTFont
{


	/**
	 * Open and read a font file. Uses the standard FontRenderContext.
	 * @param fontname font file name.
	 */
	public FTGLPolygonFont(final String fontname)
	{
		this(Font.decode(fontname));
	}

	/**
	 * Creates a new FTGLPolygonFont with the specified {@link Font}. Uses the standard FontRenderContext.
	 * @param font The font from which to create this ExtrdFont.
	 */
	public FTGLPolygonFont(final Font font)
	{
		this(font, FTFont.STANDARDCONTEXT);
	}

	/**
	 * Open and reads a font file. Renders the font with the given FontRenderContext.
	 * @param fontname The name of the font.
	 * @param context The rendercontext.
	 */
	public FTGLPolygonFont(final String fontname, final FontRenderContext context)
	{
		this(Font.decode(fontname), context);
	}

	/**
	 * Creates a new FTGLPolygonFont with the specified {@link Font}. Uses the given FontRenderContext.
	 * @param font The font from which to create this FTFont.
	 * @param context The rendercontext.
	 */
	public FTGLPolygonFont(final Font font, final FontRenderContext context)
	{
		super(font, context);
	}


	/**
	 * {@inheritDoc}
	 */
	public void dispose()
	{
		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	public FTGlyph makeGlyph(Shape ftGlyph, float advance)
	{
		if (ftGlyph != null)
		{
			FTPolyGlyph tempGlyph = new FTPolyGlyph(ftGlyph, advance);
			return tempGlyph;
		}

		return null;
	}

}