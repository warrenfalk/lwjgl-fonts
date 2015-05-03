/* $ Id$
 * Created on 11.11.2004
 */
package org.lwjgl.font.glfont;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;

import org.lwjgl.font.glyph.FTGlyph;
import org.lwjgl.font.glyph.FTPixmapGlyph;
import org.lwjgl.opengl.GL11;

/**
 * FTGLPixmapFont is a specialisation of the FTFont class for handling Pixmap
 * (Grey Scale) fonts
 * 
 * @see FTFont
 * @author joda
 */
public class FTGLPixmapFont extends FTFont {

	/**
	 * @param font
	 */
	public FTGLPixmapFont(Font font) {
		this(font, FTFont.STANDARDCONTEXT);
	}

	/**
	 * @param font
	 * @param context
	 */
	public FTGLPixmapFont(Font font, FontRenderContext context) {
		super(font, context);
	}

	/**
	 * @param fontname
	 */
	public FTGLPixmapFont(String fontname) {
		this(Font.decode(fontname));
	}

	/**
	 * @param fontname
	 * @param context
	 */
	public FTGLPixmapFont(String fontname, FontRenderContext context) {
		this(Font.decode(fontname), context);
	}

	/**
	 * Construct a FTPixmapGlyph.
	 * 
	 * @param g
	 *            The glyph index NOT the char code.
	 * @return An FTPixmapGlyph or <code>null</code> on failure.
	 */
	@Override
	protected FTGlyph makeGlyph(Shape ftGlyph, float advance) {
		if (ftGlyph != null) {
			FTPixmapGlyph tempGlyph = new FTPixmapGlyph(ftGlyph);
			return tempGlyph;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void createDisplayList() {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc} Renders a string of characters
	 * 
	 * @param string
	 *            'C' style string to be output.
	 */
	@Override
	public void render(String string) {
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_PIXEL_MODE_BIT
				| GL11.GL_COLOR_BUFFER_BIT);
		GL11.glPushClientAttrib(GL11.GL_CLIENT_PIXEL_STORE_BIT);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glDisable(GL11.GL_TEXTURE_2D);

		super.render(string);

		GL11.glPopClientAttrib();
		GL11.glPopAttrib();
	}

}