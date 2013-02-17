/* $Id: FTGLBufferFont.java,v 1.1 2004/11/12 19:44:23 funsheep Exp $ */
package org.lwjgl.font.glfont;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;

import org.lwjgl.font.glyph.FTBufferGlyph;
import org.lwjgl.font.glyph.FTGlyph;


/**
 * FTGLBufferFont is a specialisation of the FTFont class for handling Pixmap
 * (Grey Scale) fonts
 * 
 * @see FTFont
 */
class FTGLBufferFont extends FTFont
{

	private byte[] buffer = new byte[0];


	/**
	 * Open and read a font file. Sets Error flag.
	 * @param fontname font file name.
	 */
	public FTGLBufferFont(String fontname)
	{
		this(Font.decode(fontname));
	}

	/**
	 * Creates a new font from the given font with the standard FontRenderContext.
	 * @param font The font from which to construct this FTFont.
	 */
	public FTGLBufferFont(Font font)
	{
		this(font, FTFont.STANDARDCONTEXT);
	}

	/**
	 * Reads the font from the given font name and renders the font to the given fontrendercontext.
	 * @param fontname The fontname.
	 * @param context The FontRenderContext to render with.
	 */
	public FTGLBufferFont(final String fontname, final FontRenderContext context)
	{
		this(Font.decode(fontname), context);
	}

	/**
	 * Creates the FTFont from the given {@link Font}.
	 * @param font
	 * @param context
	 */
	public FTGLBufferFont(final Font font, final FontRenderContext context)
	{
		super(font, context);
	}


	public void setClientBuffer(byte[] b)
	{
		this.buffer = b;
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
	public void render(String string)
	{
		if (this.buffer != null)
		{
			super.render(string);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected FTGlyph makeGlyph(Shape ftGlyph, float advance)
	{
		if (ftGlyph != null)
		{
			FTBufferGlyph tempGlyph = new FTBufferGlyph(ftGlyph, buffer, advance);
			return tempGlyph;
		}

		return null;
	}

}