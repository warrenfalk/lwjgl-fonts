/* $Id: FTVectoriser.java,v 1.3 2005/07/27 23:14:31 joda Exp $ */
package org.lwjgl.font;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.font.glyph.FTExtrdGlyph;
import org.lwjgl.font.glyph.FTOutlineGlyph;
import org.lwjgl.font.glyph.FTPolyGlyph;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.GLUtessellator;


/**
 * FTVectoriser class is a helper class that converts font outlines into point data.
 * TODO: DOC
 * @see FTExtrdGlyph
 * @see FTOutlineGlyph
 * @see FTPolyGlyph
 * @see FTContour
 * @see FTPoint
 */
public class FTVectoriser
{

	public static final double FTGL_FRONT_FACING = 1.0;
	public static final double FTGL_BACK_FACING = -1.0;

	/**
	 * A flag indicating the tesselation rule for the glyph
	 */
	private int contourFlag = 0;

	/**
	 * A Freetype outline (Shape representing a single or more characters)
	 */
	private PathIterator outline; //FT_LIB

	/**
	 * The list of contours in the glyph
	 */
	private List<FTContour> contourList = new ArrayList<FTContour>(2);

	/**
	 * A Mesh for tesselations
	 */
	private FTMesh mesh = null;

	private float stepSize;

	/**
	 * Constructor
	 * 
	 * @param glyph The freetype glyph to be processed
	 */
	public FTVectoriser(final Shape glyph)
	{
		this(glyph, FTContour.BEZIER_STEP_SIZE);
	}

	public FTVectoriser(final Shape glyph, float flatness)
	{
//		if (glyph == null)
//		{
//			throw new NullPointerException("Glyph shape can not be null.");
//		}

		assert glyph != null : "Glyph shape cannot be null!";

		this.outline = glyph.getPathIterator(null);
		this.stepSize = flatness;

		//this.ftContourCount = outline.n_contours;
		//this.contourList = 0;
		//this.contourFlag = outline.flags;
		//TODO: Fix this is pretty evil :D (correct incorrect winding of shape
		// this fix is required get correct normals for inverted shapes
//		if (this.outline.getWindingRule()==PathIterator.WIND_NON_ZERO)
//			this.contourFlag = PathIterator.WIND_EVEN_ODD;
//		else
//			this.contourFlag = PathIterator.WIND_NON_ZERO;
		this.contourFlag = this.outline.getWindingRule();
		this.processContours();
	}

	/**
	 * Build an FTMesh from the vector outline data.
	 */
	public void makeMesh()
	{
		this.makeMesh(FTGL_FRONT_FACING);
	}

	/**
	 * Build an FTMesh from the vector outline data.
	 * 
	 * @param zNormal The direction of the z axis of the normal for this mesh
	 */
	public void makeMesh(double zNormal)
	{
		if (this.mesh != null)
		{
			this.mesh.dispose();
		}

		this.mesh = new FTMesh();

		GLUtessellator tobj = GLU.gluNewTess();
		FTTesselatorCallback callback = new FTTesselatorCallback();

		tobj.gluTessCallback(GLU.GLU_TESS_BEGIN_DATA, callback);
		tobj.gluTessCallback(GLU.GLU_TESS_VERTEX_DATA, callback);
		tobj.gluTessCallback(GLU.GLU_TESS_COMBINE_DATA, callback);
		tobj.gluTessCallback(GLU.GLU_TESS_END_DATA, callback);
		tobj.gluTessCallback(GLU.GLU_TESS_ERROR_DATA, callback);

		//if( contourFlag && ft_outline_even_odd_fill) // ft_outline_reverse_fill
		if (this.contourFlag == PathIterator.WIND_EVEN_ODD)
		{
			tobj.gluTessProperty(GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_NONZERO);
		}
		else
		{
			assert this.contourFlag == PathIterator.WIND_NON_ZERO;
			tobj.gluTessProperty(GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_ODD);
		}

		tobj.gluTessProperty(GLU.GLU_TESS_TOLERANCE, 0);
		tobj.gluTessNormal(0.0f, 0.0f, zNormal);
		tobj.gluTessBeginPolygon(this.mesh);

		for (int c = 0; c < this.contourCount(); ++c)
		{
			final FTContour contour = contourList.get(c);

			tobj.gluTessBeginContour();

			for (int p = 0; p < contour.pointCount(); ++p)
			{
				double[] v3d = contour.getPoint(p);
				// point must be copied into a at least 3 component array
				double[] d = new double[] { v3d[FTContour.X], v3d[FTContour.Y], 0};
				tobj.gluTessVertex(d, 0, v3d);
			}

			tobj.gluTessEndContour();
		}

		tobj.gluTessEndPolygon();

		tobj.gluDeleteTess();
	}

	/**
	 * Get the current mesh.
	 */
	public final FTMesh getMesh()
	{
		return this.mesh;
	}

	/**
	 * Get the total count of points in this outline
	 * 
	 * @return the number of points
	 */
	public int pointCount()
	{
		int s = 0;
		for (int c = 0; c < this.contourCount(); ++c)
		{
			s += this.contour(c).pointCount();
		}
		return s;
	}

	/**
	 * Get the count of contours in this outline
	 * 
	 * @return the number of contours
	 */
	public final int contourCount()
	{
		return this.contourList.size();
	}

	/**
	 * Return a contour at index
	 * 
	 * @return the number of contours
	 */
	public final FTContour contour(int index)
	{
		//return (index < this.contourCount()) ? this.contourList[index] : null;
		return this.contourList.get(index);
	}

	/**
	 * Get the number of points in a specific contour in this outline
	 * 
	 * @param c The contour index
	 * @return the number of points in contour[c]
	 */
	public final int contourSize(int c)
	{
		return this.contour(c).pointCount();
	}

	/**
	 * Get the flag for the tesselation rule for this outline
	 * 
	 * @return The contour flag
	 */
	public final int contourFlag()
	{
		return this.contourFlag;
	}

	/**
	 * Process the freetype outline data into contours of points
	 */
	private void processContours()
	{
		while (!this.outline.isDone())
		{
			// prevent creation of emtpy segments
			assert (FTVectoriser.checkSegment(this.outline)):"Let's hope this won't happen.";
			this.contourList.add(new FTContour(this.outline, this.stepSize));
		}
	}

	private static boolean checkSegment(PathIterator path)
	{
		int type = path.currentSegment(new double[6]);
		switch (type)
		{
			case PathIterator.SEG_CLOSE:
				return false;
			case PathIterator.SEG_CUBICTO:
			case PathIterator.SEG_LINETO:
			case PathIterator.SEG_QUADTO:
				System.err.println("FTVectoriser: FTContour will not read segment SEG_MOVETO.");
			default:
				return true;
		}
	}

}