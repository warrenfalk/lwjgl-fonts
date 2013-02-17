/* $Id: FTPolyGlyph.java,v 1.2 2005/07/27 23:14:31 joda Exp $ */
package org.lwjgl.font.glyph;

import java.awt.Shape;

import org.lwjgl.font.FTContour;
import org.lwjgl.font.FTGlyphContainer;
import org.lwjgl.font.FTMesh;
import org.lwjgl.font.FTTesselation;
import org.lwjgl.font.FTVectoriser;
import org.lwjgl.opengl.GL11;


//#include "FTPolyGlyph.h"
//#include "FTVectoriser.h"

/**
 * FTPolyGlyph is a specialisation of FTGlyph for creating tessellated
 * polygon glyphs.
 * 
 * @see FTGlyphContainer
 * @see FTVectoriser
 * 
 */
public class FTPolyGlyph extends FTGlyph
{

	/**
	 * Creates a new FTPolyGlyph
	 * @param glyph The Shape the glyph represents.
	 */
	public FTPolyGlyph(Shape glyph)
	{
		super(glyph);
	}

	/**
	 * Creates a new FTPolyGlyph.
	 * @param glyph The shape th eglyph represents.
	 * @param advance The advance factor of this glyph.
	 */
	public FTPolyGlyph(Shape glyph, float advance)
	{
		super(glyph, advance);
	}


	/**
	 * {@inheritDoc}
	 */
	protected void createDisplayList()
	{
		FTVectoriser vectoriser = new FTVectoriser(this.glyph);

		if ((vectoriser.contourCount() < 1) || (vectoriser.pointCount() < 3))
		{
			return;
		}

		vectoriser.makeMesh(1.0);

		this.glList = GL11.glGenLists(1);
		GL11.glNewList(this.glList, GL11.GL_COMPILE);

		GL11.glNormal3d(0.0, 0.0, 1.0);

		final FTMesh mesh = vectoriser.getMesh();
		for (int index = 0; index < mesh.tesselationCount(); ++index)
		{
			final FTTesselation subMesh = mesh.getTesselation(index);
			int polyonType = subMesh.getPolygonType();

			GL11.glBegin(polyonType);
			for (int x = 0; x < subMesh.pointCount(); ++x)
			{
				GL11.glVertex3f((float) subMesh.getPoint(x)[FTContour.X] /*/ 64.0f */, (float) subMesh.getPoint(x)[FTContour.Y] /*/ 64.0f */, 0.0f);
			}
			GL11.glEnd();
		}
		GL11.glEndList();
	}

	/**
	 * {@inheritDoc}
	 */
	public float render(final float x, final float y, final float z)
	{
		if (GL11.glIsList(glList))
		{
			GL11.glTranslatef((float) x, (float) y, 0.0f);
			GL11.glCallList(glList);
			GL11.glTranslatef((float) -x, (float) -y, 0.0f);
		}

		return advance;
	}
}