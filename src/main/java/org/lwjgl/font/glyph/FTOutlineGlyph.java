/* $Id: FTOutlineGlyph.java,v 1.2 2005/07/27 23:14:31 joda Exp $ */
package org.lwjgl.font.glyph;

import java.awt.Shape;

import org.lwjgl.font.FTContour;
import org.lwjgl.font.FTGlyphContainer;
import org.lwjgl.font.FTVectoriser;
import org.lwjgl.opengl.GL11;

/**
 * FTOutlineGlyph is a specialisation of FTGlyph for creating outlines.
 * 
 * @see FTGlyphContainer
 * @see FTVectoriser
 */
public class FTOutlineGlyph extends FTGlyph {

	/**
	 * Constructor.
	 * 
	 * @param glyph
	 *            The glyph to be processed
	 */
	public FTOutlineGlyph(Shape glyph) {
		super(glyph);
	}

	/**
	 * Constructor.
	 * 
	 * @param glyph
	 *            The glyph to be processed.
	 * @param advance
	 *            The advance of the glyph.
	 */
	public FTOutlineGlyph(Shape glyph, float advance) {
		super(glyph, advance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createDisplayList() {
		FTVectoriser vectoriser = new FTVectoriser(glyph);

		int numContours = vectoriser.contourCount();
		if ((numContours < 1) || (vectoriser.pointCount() < 3))
			return;

		glList = GL11.glGenLists(1);
		GL11.glNewList(glList, GL11.GL_COMPILE);
		for (int c = 0; c < numContours; ++c) {
			final FTContour contour = vectoriser.contour(c);

			GL11.glBegin(GL11.GL_LINE_LOOP);
			for (int p = 0; p < contour.pointCount(); ++p)
				GL11.glVertex2f((float) contour.getPoint(p)[FTContour.X] /*
																		 * /
																		 * 64.0f
																		 */,
						(float) contour.getPoint(p)[FTContour.Y] /* /64.0f */);
			GL11.glEnd();
		}
		GL11.glEndList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float render(final float x, final float y, final float z) {
		if (GL11.glIsList(glList)) {
			GL11.glTranslatef(x, y, 0.0f);
			GL11.glCallList(glList);
			GL11.glTranslatef(-x, -y, 0.0f);
		}

		return advance;
	}

}