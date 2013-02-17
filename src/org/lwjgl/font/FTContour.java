/* $Id: FTContour.java,v 1.2 2005/07/27 23:14:31 joda Exp $ */

package org.lwjgl.font;

import java.awt.geom.PathIterator;
import java.util.List;
import java.util.Vector;

import org.lwjgl.font.glyph.FTOutlineGlyph;
import org.lwjgl.font.glyph.FTPolyGlyph;


/**
 * FTContour class is a container of points that describe a vector font outline. It is used as a
 * container for the output of the bezier curve evaluator in FTVectoriser.
 * 
 * @see FTOutlineGlyph
 * @see FTPolyGlyph
 * @see FTPoint
 */
public class FTContour
{
	/**
	 * 
	 */
	public static final int X = 0;

	/**
	 * 
	 */
	public static final int Y = 1;

	/** step size used to transform curves to line segments */
	public static final float BEZIER_STEP_SIZE = 0.2f;

	/** 2D array storing values of de Casteljau algorithm. */
	private double[][] controlPoints = new double[4][2];
	private List pointlist = new Vector();
	private float stepSize = BEZIER_STEP_SIZE;
	private float lastStep = 0;

	/**
	 * Constructor
	 * 
	 * @param contour
	 * @param pointTags
	 * @param numberOfPoints
	 */
	public FTContour(PathIterator contour)
	{
		this(contour, BEZIER_STEP_SIZE);
	}

	/**
	 * 
	 * @param contour
	 * @param bezierStepSize
	 */
	public FTContour(PathIterator contour, float bezierStepSize)
	{
		this.stepSize = bezierStepSize;
		int pointTag;
		double[] pointData = new double[6];
		double[] lastPoint = new double[2];
		double[] lastMoveTo = new double[2];
		boolean moved = false;
		//TODO: Verify correct handling of PathIterator.SEG_CLOSE
		pointTag = contour.currentSegment(pointData);
		while (pointTag==PathIterator.SEG_MOVETO)
		{
				System.arraycopy(pointData, 0, lastPoint, 0, 2);
				System.arraycopy(pointData, 0, lastMoveTo, 0, 2);
				moved = true;
				contour.next();
				pointTag = contour.currentSegment(pointData);
		}

		this.addPoint(lastPoint[0], lastPoint[1]);
		assert moved:"FTContour: PathIterator did not start with SEG_MOVETO. (type="+pointTag+")";
	loop:
		do
		{
			pointTag = contour.currentSegment(pointData);
			switch (pointTag)
			{
				case PathIterator.SEG_LINETO:
					this.addPoint(pointData[0], pointData[1]);
					System.arraycopy(pointData, 0, lastPoint, 0, 2);
					break;
				case PathIterator.SEG_MOVETO:
					System.err.println("FTContour: segment not closed - aborted by SEG_MOVETO");
					break loop;
				case PathIterator.SEG_QUADTO:
					this.controlPoints[0] = lastPoint;
					this.controlPoints[1][0] = pointData[0];
					this.controlPoints[1][1] = pointData[1];
					this.controlPoints[2][0] = pointData[2];
					this.controlPoints[2][1] = pointData[3];

					evaluateQuadraticCurve();
					System.arraycopy(pointData, 2, lastPoint, 0, 2);
					break;
				case PathIterator.SEG_CUBICTO:
					this.controlPoints[0] = lastPoint;
					this.controlPoints[1][0] = pointData[0];
					this.controlPoints[1][1] = pointData[1];
					this.controlPoints[2][0] = pointData[2];
					this.controlPoints[2][1] = pointData[3];
					this.controlPoints[3][0] = pointData[4];
					this.controlPoints[3][1] = pointData[5];

					this.evaluateCubicCurve();
					System.arraycopy(pointData, 4, lastPoint, 0, 2);
					break;
				case PathIterator.SEG_CLOSE:
					if (moved)
						this.addPoint(lastMoveTo[0], lastMoveTo[1]);
					break;
				default:
					throw new IllegalStateException("Unknown type of path segment.");
			}
			contour.next();
		}
		while (
			// contour is has more segments
			!contour.isDone() &&
			// segment was not explicitly closed
			pointTag != PathIterator.SEG_CLOSE &&
			// segment was not implicitly closed
			(lastPoint[0]!=lastMoveTo[0] || lastPoint[1]!=lastMoveTo[1]));

		if (!contour.isDone())
		{
			// an additional segment close
			pointTag = contour.currentSegment(pointData);
			if (pointTag==PathIterator.SEG_CLOSE)
			{
				if (moved)
					this.addPoint(lastMoveTo[0], lastMoveTo[1]);
				contour.next();
			}
		}
	}

	/**
	 * Return a point at index.
	 * 
	 * @param index of the point in the curve.
	 * @return const point reference
	 */
	public final double[] getPoint(int index)
	{
		return (double[])this.pointlist.get(index);
	}

	/**
	 * How many points define this contour
	 * 
	 * @return the number of points in this contour
	 */
	public final int pointCount()
	{
		return this.pointlist.size();
	}

	/**
	 * Add a point to this contour. This function tests for duplicate points.
	 * 
	 * @param point The point to be added to the contour.
	 */
	private final void addPoint(final double[] point)
	{
		if (this.pointlist.isEmpty() || !equals(point, (double[])this.pointlist.get(this.pointlist.size() - 1)))
		{
			this.pointlist.add(point);
		}
	}

	private static boolean equals(double[] a, double[] b)
	{
		assert b.length==2 && a.length==2:"exspected array of length 2";
		return b[0]==a[0] && b[1]==a[1];
	}

	/**
	 * Add a point to this contour. This function tests for duplicate points.
	 * 
	 * @param x x component of the point
	 * @param y y component of the point
	 */
	private final void addPoint(final double x, final double y)
	{
		this.addPoint(new double[] {x, y});
	}

	/**
	 * De Casteljau (bezier) algorithm contributed by Jed Soane Evaluates a quadratic or conic
	 * (second degree) curve
	 */
	private final void evaluateQuadraticCurve()
	{
		int i = (int)(this.lastStep / this.stepSize);
		for (; i <= (1.0f / this.stepSize); i++)
		{
			final double[][] bezierValues = new double[2][2];

			final float t = i * this.stepSize;

			bezierValues[0][0] = (1.0f - t) * controlPoints[0][0] + t * controlPoints[1][0];
			bezierValues[0][1] = (1.0f - t) * controlPoints[0][1] + t * controlPoints[1][1];

			bezierValues[1][0] = (1.0f - t) * controlPoints[1][0] + t * controlPoints[2][0];
			bezierValues[1][1] = (1.0f - t) * controlPoints[1][1] + t * controlPoints[2][1];

			bezierValues[0][0] = (1.0f - t) * bezierValues[0][0] + t * bezierValues[1][0];
			bezierValues[0][1] = (1.0f - t) * bezierValues[0][1] + t * bezierValues[1][1];

			this.addPoint(bezierValues[0][0], bezierValues[0][1]);
		}
		this.lastStep = i*stepSize-1f;
	}

	/**
	 * De Casteljau (bezier) algorithm contributed by Jed Soane Evaluates a cubic (third degree)
	 * curve
	 */
	private final void evaluateCubicCurve()
	{
		int i = (int)(this.lastStep / this.stepSize);
		for (; i <= (1.0f / this.stepSize); i++)
		{
			final double[][] bezierValues = new double[3][2];

			final float t = i * this.stepSize;

			bezierValues[0][0] = (1.0f - t) * controlPoints[0][0] + t * controlPoints[1][0];
			bezierValues[0][1] = (1.0f - t) * controlPoints[0][1] + t * controlPoints[1][1];

			bezierValues[1][0] = (1.0f - t) * controlPoints[1][0] + t * controlPoints[2][0];
			bezierValues[1][1] = (1.0f - t) * controlPoints[1][1] + t * controlPoints[2][1];

			bezierValues[2][0] = (1.0f - t) * controlPoints[2][0] + t * controlPoints[3][0];
			bezierValues[2][1] = (1.0f - t) * controlPoints[2][1] + t * controlPoints[3][1];

			bezierValues[0][0] = (1.0f - t) * bezierValues[0][0] + t * bezierValues[1][0];
			bezierValues[0][1] = (1.0f - t) * bezierValues[0][1] + t * bezierValues[1][1];

			bezierValues[1][0] = (1.0f - t) * bezierValues[1][0] + t * bezierValues[2][0];
			bezierValues[1][1] = (1.0f - t) * bezierValues[1][1] + t * bezierValues[2][1];

			bezierValues[0][0] = (1.0f - t) * bezierValues[0][0] + t * bezierValues[1][0];
			bezierValues[0][1] = (1.0f - t) * bezierValues[0][1] + t * bezierValues[1][1];

			this.addPoint(bezierValues[0][0], bezierValues[0][1]);
		}
		this.lastStep = i*stepSize-1f;
	}

}