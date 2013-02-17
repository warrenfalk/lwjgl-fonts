/* $Id: FTExtrdGlyph.java,v 1.8 2005/07/27 23:14:31 joda Exp $ */
package org.lwjgl.font.glyph;

import java.awt.Shape;
import java.awt.geom.PathIterator;

import org.lwjgl.font.FTContour;
import org.lwjgl.font.FTGlyphContainer;
import org.lwjgl.font.FTMesh;
import org.lwjgl.font.FTTesselation;
import org.lwjgl.font.FTVectoriser;
import org.lwjgl.font.util.Vector3f;
import org.lwjgl.opengl.GL11;


/**
 * FTExtrdGlyph is a specialisation of FTGlyph for creating tessellated extruded polygon glyphs.
 * @see FTGlyphContainer
 * @see FTVectoriser
 */
public class FTExtrdGlyph extends FTGlyph
{

	/**
	 * Distance to extrude the glyph
	 */
	private float depth;


	/**
	 * Constructor. Sets the Error to Invalid_Outline if the glyphs isn't an outline.
	 * @param glyph The Freetype glyph to be processed
	 * @param depth The distance along the z axis to extrude the glyph
	 */
	public FTExtrdGlyph(final Shape glyph, float depth)
	{
		super(glyph);
		this.depth = depth;
		this.bBox.setDepth(-depth);
	}

	public FTExtrdGlyph(final Shape glyph, float depth, float advance)
	{
		super(glyph, advance);
		this.depth = depth;
		this.bBox.setDepth(-depth);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createDisplayList()
	{
		FTVectoriser vectoriser = new FTVectoriser(this.glyph);

		if ((vectoriser.contourCount() < 1) || (vectoriser.pointCount() < 3))
		{
			return;
		}

		int tesselationIndex;
		this.glList = GL11.glGenLists(1);
		GL11.glNewList(this.glList, GL11.GL_COMPILE);
		if (!GL11.glIsList(glList))
			System.err.println("WARNING: something went wrong creating display list");

		vectoriser.makeMesh(1.0);
		GL11.glNormal3d(0.0, 0.0, 1.0);

		FTMesh mesh = vectoriser.getMesh();
		for (tesselationIndex = 0; tesselationIndex < mesh.tesselationCount(); ++tesselationIndex)
		{
			FTTesselation subMesh = mesh.getTesselation(tesselationIndex);
			int polygonType = subMesh.getPolygonType();

			GL11.glBegin(polygonType);
			for (int pointIndex = 0; pointIndex < subMesh.pointCount(); ++pointIndex)
			{
				double[] point = subMesh.getPoint(pointIndex);
				GL11.glVertex3f((float)point[FTContour.X] /*/ 64.0f*/, // TODO 64?
					(float)point[FTContour.Y] /*/ 64.0f*/, 0.0f);
			}
			GL11.glEnd();
		}

		vectoriser.makeMesh(-1.0);
		GL11.glNormal3d(0.0, 0.0, -1.0);

		mesh = vectoriser.getMesh();
		for (tesselationIndex = 0; tesselationIndex < mesh.tesselationCount(); ++tesselationIndex)
		{
			FTTesselation subMesh = mesh.getTesselation(tesselationIndex);
			int polygonType = subMesh.getPolygonType();

			GL11.glBegin(polygonType);
			for (int pointIndex = 0; pointIndex < subMesh.pointCount(); ++pointIndex)
			{
				GL11.glVertex3f((float)subMesh.getPoint(pointIndex)[FTContour.X] /*/ 64.0f*/, // TODO 64?
					(float)subMesh.getPoint(pointIndex)[FTContour.Y] /*/ 64.0f*/, -this.depth);
			}
			GL11.glEnd();
		}

		int contourFlag = vectoriser.contourFlag();
		boolean nonzero = (contourFlag != PathIterator.WIND_NON_ZERO);

		for (int c = 0; c < vectoriser.contourCount(); ++c)
		{
			FTContour contour = vectoriser.contour(c);
			int numberOfPoints = contour.pointCount();

			Vector3f oldNormal = FTExtrdGlyph.getNormal(contour.getPoint(numberOfPoints-1), contour.getPoint(0));
			GL11.glBegin(GL11.GL_QUAD_STRIP);
			for (int j = 0; j <= numberOfPoints; ++j)
			{
				int index = (j == numberOfPoints) ? 0 : j;
				int nextIndex = (index == numberOfPoints - 1) ? 0 : index + 1;

				Vector3f flatNormal = FTExtrdGlyph.getNormal(contour.getPoint(index), contour.getPoint(nextIndex));
				boolean smoothEdge = Math.abs(flatNormal.angle(oldNormal))<Math.PI/4;
				Vector3f normal;
				if (smoothEdge)
				{
					normal = new Vector3f(flatNormal).add(oldNormal).scale(1/2f);
				}
				else
				{
					normal = oldNormal;
					GL11.glNormal3f(normal.x, normal.y, 0.0f);
					if (nonzero)// & ft_outline_reverse_fill) //FT_LIB
					{
						GL11.glVertex3f((float)contour.getPoint(index)[FTContour.X] /*/ 64.0f*/,
							(float)contour.getPoint(index)[FTContour.Y] /*/ 64.0f*/, 0.0f); // TODO 64?
						GL11.glVertex3f((float)contour.getPoint(index)[FTContour.X] /*/ 64.0f*/,
							(float)contour.getPoint(index)[FTContour.Y] /*/ 64.0f*/, -this.depth);
					}
					else
					{
						GL11.glVertex3f((float)contour.getPoint(index)[FTContour.X] /*/ 64.0f*/,
							(float)contour.getPoint(index)[FTContour.Y] /*/ 64.0f*/, -this.depth); // TODO 64?
						GL11.glVertex3f((float)contour.getPoint(index)[FTContour.X] /*/ 64.0f*/,
							(float)contour.getPoint(index)[FTContour.Y] /*/ 64.0f*/, 0.0f);
					}
					normal = flatNormal;
					GL11.glEnd();
					GL11.glBegin(GL11.GL_QUAD_STRIP);
				}

				GL11.glNormal3f(normal.x, normal.y, 0.0f);
				if (nonzero)// & ft_outline_reverse_fill) //FT_LIB
				{
					GL11.glVertex3f((float)contour.getPoint(index)[FTContour.X] /*/ 64.0f*/,
						(float)contour.getPoint(index)[FTContour.Y] /*/ 64.0f*/, 0.0f); // TODO 64?
					GL11.glVertex3f((float)contour.getPoint(index)[FTContour.X] /*/ 64.0f*/,
						(float)contour.getPoint(index)[FTContour.Y] /*/ 64.0f*/, -this.depth);
				}
				else
				{
					GL11.glVertex3f((float)contour.getPoint(index)[FTContour.X] /*/ 64.0f*/,
						(float)contour.getPoint(index)[FTContour.Y] /*/ 64.0f*/, -this.depth); // TODO 64?
					GL11.glVertex3f((float)contour.getPoint(index)[FTContour.X] /*/ 64.0f*/,
						(float)contour.getPoint(index)[FTContour.Y] /*/ 64.0f*/, 0.0f);
				}
				oldNormal = flatNormal;
			}
			GL11.glEnd();
			assert displayNormals(vectoriser);
		}
		GL11.glEndList();
	}

	private boolean displayNormals(FTVectoriser vectoriser)
	{
		GL11.glPushAttrib(GL11.GL_CURRENT_BIT | GL11.GL_LIGHTING_BIT);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor3f(1.0f, 0.0f, 0.0f);
		GL11.glBegin(GL11.GL_LINES);
		for (int c = 0; c < vectoriser.contourCount(); ++c)
		{
			FTContour contour = vectoriser.contour(c);
			int numberOfPoints = contour.pointCount();
			Vector3f oldNormal = FTExtrdGlyph.getNormal(contour.getPoint(0), contour.getPoint(1));
			for (int j = 0; j <= numberOfPoints; ++j)
			{
				int index = (j == numberOfPoints) ? 0 : j;
				int nextIndex = (index == numberOfPoints - 1) ? 0 : index + 1;

				Vector3f flatNormal = FTExtrdGlyph.getNormal(contour.getPoint(index), contour.getPoint(nextIndex));
				boolean smoothEdge = Math.abs(oldNormal.angle(flatNormal))<Math.PI/4;
				Vector3f normal;
				if (smoothEdge)
				{
					normal = new Vector3f(flatNormal).add(oldNormal).scale(1/2f);
				}
				else
				{
					normal = new Vector3f(oldNormal).scale(4f, 4f, 0f);
					GL11.glNormal3f(normal.x, normal.y, 0.0f);
					GL11.glVertex3f((float)contour.getPoint(index)[FTContour.X] /*/ 64.0f*/,
						(float)contour.getPoint(index)[FTContour.Y] /*/ 64.0f*/, 0.0f); // TODO 64?
					GL11.glVertex3f((float)contour.getPoint(index)[FTContour.X]+normal.x /*/ 64.0f*/,
						(float)contour.getPoint(index)[FTContour.Y]+normal.y /*/ 64.0f*/, 0.0f); // TODO 64?
					GL11.glVertex3f((float)contour.getPoint(index)[FTContour.X] /*/ 64.0f*/,
						(float)contour.getPoint(index)[FTContour.Y] /*/ 64.0f*/, -depth);
					GL11.glVertex3f((float)contour.getPoint(index)[FTContour.X]+normal.x /*/ 64.0f*/,
						(float)contour.getPoint(index)[FTContour.Y]+normal.y /*/ 64.0f*/, -depth);
				}
//				System.out.print("Contourflag:");
//				switch (vectoriser.contourFlag())
//				{
//					case PathIterator.WIND_EVEN_ODD:
//						System.out.println("even odd");
//						break;
//					case PathIterator.WIND_NON_ZERO:
//						System.out.println("non zero");
//						break;
//					default:
//						System.out.println("unknown");
//						break;
//				}
				normal = new Vector3f(flatNormal).scale(4f, 4f, 0f);
				GL11.glNormal3f(normal.x, normal.y, 0.0f);
				GL11.glVertex3f((float)contour.getPoint(index)[FTContour.X] /*/ 64.0f*/,
					(float)contour.getPoint(index)[FTContour.Y] /*/ 64.0f*/, 0.0f); // TODO 64?
				GL11.glVertex3f((float)contour.getPoint(index)[FTContour.X]+normal.x /*/ 64.0f*/,
					(float)contour.getPoint(index)[FTContour.Y]+normal.y /*/ 64.0f*/, 0.0f); // TODO 64?
				GL11.glVertex3f((float)contour.getPoint(index)[FTContour.X] /*/ 64.0f*/,
					(float)contour.getPoint(index)[FTContour.Y] /*/ 64.0f*/, -depth);
				GL11.glVertex3f((float)contour.getPoint(index)[FTContour.X]+normal.x /*/ 64.0f*/,
					(float)contour.getPoint(index)[FTContour.Y]+normal.y /*/ 64.0f*/, -depth);
				oldNormal = flatNormal;
			}
		}
		GL11.glEnd();
		GL11.glPopAttrib();
		return true;
	}

	/**
	 * Calculate the normal vector to 2 points. This is 2D and ignores the z component. The normal
	 * will be normalised
	 * @param a
	 * @param b
	 * @return
	 */
	private static Vector3f getNormal(double[] a, double[] b)
	{
		float vectorX = (float)(a[FTContour.X] - b[FTContour.X]);
		float vectorY = (float)(a[FTContour.Y] - b[FTContour.Y]);

		float length = (float)Math.sqrt(vectorX * vectorX + vectorY * vectorY);

		if (length > 0.0f)
		{
			length = 1 / length;
		}
		else
		{
			length = 0.0f;
		}

		return new Vector3f((float)vectorY * length, (float)-vectorX * length, 0.0f);
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
	public float render(final float x, final float y, final float z)
	{
		if (GL11.glIsList(this.glList))
		{
			GL11.glTranslatef((float)x, (float)y, 0);
			GL11.glCallList(this.glList);
			GL11.glTranslatef((float)-x, (float)-y, 0);
		}
		return advance;
	}

}