/* $Id: VectorUtils.java,v 1.1 2005/08/20 10:15:55 joda Exp $
 * Created on Feb 12, 2005
 */
package org.lwjgl.font.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.logging.Level;

import org.lwjgl.font.demos.util.Logger;

/**
 * This class holds a bunch of utilities to make the life with the Vector
 * package easier.
 * 
 * @author funsheep
 */
public final class VectorUtils {
	private static Logger LOGGER = Logger.getLogger();

	/**
	 * Constructor.
	 */
	private VectorUtils() {
		super();
	}

	/**
	 * Interpolates between two Vectors. Substitute the Vector, indicated, by
	 * the two given endpoints to teh alias C. Then this method returns a point
	 * lying on the straight line, indicated by C. Is t = 0, the vector vec1 is
	 * returned, is t = 1 vector vec2 is returned. Is t between 0 and 1, a point
	 * on the line between vec1 and vec2 is returned.
	 * 
	 * @param vec1
	 *            The vector indicating the start point of the line.
	 * @param vec2
	 *            The vector indicating the end point of the line.
	 * @param t
	 *            A float value in the intervall [0..1] indicating the
	 *            interpolation degree.
	 * @return An interpolated point between the two given points.
	 */
	public static final Vector3f interpolate(Vector3f vec1, Vector3f vec2,
			float t) {
		assert t >= 0 && t <= 1;
		return new Vector3f(vec2).sub(vec1).scale(t).add(vec1);
	}

	/**
	 * Interpolates between two Vectors. Substitute the Vector, indicated, by
	 * the two given endpoints to teh alias C. Then this method returns a point
	 * lying on the straight line, indicated by C. Is t = 0, the vector vec1 is
	 * returned, is t = 1 vector vec2 is returned. Is t between 0 and 1, a point
	 * on the line between vec1 and vec2 is returned.
	 * 
	 * @param vec1
	 *            The vector indicating the start point of the line.
	 * @param vec2
	 *            The vector indicating the end point of the line.
	 * @param t
	 *            A float value in the intervall [0..1] indicating the
	 *            interpolation degree.
	 * @return An interpolated point between the two given points.
	 */
	public static final Point2D.Float interpolate(Point2D.Float vec1,
			Point2D.Float vec2, float t) {
		assert t >= 0 && t <= 1;
		// vec2).sub(vec1).scale(t).add(vec1
		return new Point2D.Float(t * (vec2.x - vec1.x) + vec1.x, t * vec2.y
				+ (1 - t) * vec1.y);
	}

	// /** HENDRIK: can this method be removed ?
	// * TO DO_RE javadoc
	// *
	// * @param v1
	// * @param v2
	// * @return
	// */
	// public static float projCosangle(Vector3f v1, Vector3f v2)
	// {
	// Vector3f projv1 = new Vector3f(v1);
	// Vector3f projv2 = new Vector3f(v2);
	// projv1.z = 0;
	// projv2.z = 0;
	// projv1.normalize();
	// projv2.normalize();
	// return projv1.dot(projv2);
	// }

	// /**
	// * HENDRIK javadoc
	// *
	// * @param frontvector
	// * @param direction
	// * @return javadoc!
	// */
	// public static final Matrix3f cylindricalBillboard(Vector3f frontvector,
	// Vector3f direction)
	// {
	// Vector3f projfront = new Vector3f(frontvector);
	// Vector3f projdirection = new Vector3f(direction);
	// projfront.z = 0;
	// projdirection.z = 0;
	// projfront.normalize();
	// projdirection.normalize();
	// float cosangle = projfront.dot(projdirection);
	// if (cosangle > 1 - Vector3f.TOLERANCE)
	// {
	// // direction and frontvector are more, or less the same, return
	// // identity matrix
	// return new Matrix3f();
	// }
	// if (-cosangle > 1 - Vector3f.TOLERANCE)
	// {
	// // direction and frontvector are more or less the opposite of each
	// // other
	// return new Matrix3f();
	// }
	// Vector3f rotup = Vector3f.cross(projfront, projdirection).normalize();
	// Matrix3f rot = new Matrix3f();
	// rot.rotate(rotup, (float)Math.acos(cosangle));
	// return rot;
	// }

	private static float manhattenLength(Vector3f v) {
		float x = Math.abs(v.x);
		float y = Math.abs(v.y);
		float z = Math.abs(v.z);
		return Math.max(Math.max(x, y), z);
	}

	/**
	 * Calculate the normal of a triangle.
	 * 
	 * @param v1
	 *            first vertex/coordinate of the triangle.
	 * @param v2
	 *            second vertex/coordinate of the triangle.
	 * @param v3
	 *            thrid vertex/coordinate of the triangle.
	 * @return the orthogonal normal
	 */
	public static Vector3f calculateNormal(Vector3f v1, Vector3f v2, Vector3f v3) {
		// if this method causes trouble, ask Joda or Sven

		// calculate facenormal from vertices
		Vector3f plA = new Vector3f(v1).sub(v2);
		Vector3f plB = new Vector3f(v2).sub(v3);
		Vector3f plC = new Vector3f(v3).sub(v1);

		// use some kind of "manhatten length"
		float lenA = manhattenLength(plA);
		float lenB = manhattenLength(plB);
		float lenC = manhattenLength(plC);

		Vector3f r1, r2;

		// chose the two best vectors.
		// the best vectors are the largest and the smallest.
		// they typically have the greatest angle between them.
		if (lenA < lenB) {
			// we know now: A B
			if (lenB < lenC) {
				// we know now: A B C
				r1 = plC;
				r2 = plA;
			} else if (lenC < lenA) {
				// we know now: C A B
				r1 = plB;
				r2 = plC;
			} else {
				// we know now: A C B
				r1 = plA;
				r2 = plB;
			}
		} else // we know now: B A
		if (lenA < lenC) {
			// we know now: B A C
			r1 = plB;
			r2 = plC;
		} else if (lenC < lenB) {
			// we know now: C B A
			r1 = plC;
			r2 = plA;
		} else {
			// we know now: B C A
			r1 = plA;
			r2 = plB;
		}

		if (r1.isZero() || r2.isZero()) {
			assert LOGGER.log(Level.FINE,
					"degenerated triangle: {0}, {1}, {2}", new Object[] { v1,
							v2, v3 });
			return null;
		}

		r1.normalize2();
		r2.normalize2();

		Vector3f normal = r1.cross(r2);

		if (normal.isZero()) {
			LOGGER.log(Level.WARNING,
					"unexpected zero normal, triangle was: {0}, {1}, {2}",
					new Object[] { v1, v2, v3 });
			return null;
		}

		if (normal.isInvalid()) {
			LOGGER.log(Level.WARNING,
					"normal is invalid, triangle was: {0}, {1}, {2}",
					new Object[] { v1, v2, v3 });
			return null;
		}

		// here we shouldn't hava a zero-vector or something like that
		normal.normalize2();

		// this should be quite sure now
		assert normal.isNormalized(Vector3f.TOLERANCE);

		return normal;
	}

	/**
	 * Calculates the area covered by the triangle spanned by v1 and v2.
	 * 
	 * @param v1
	 * @param v2
	 * @return the area
	 */
	public static final float triangleArea(Vector3f v1, Vector3f v2) {
		// fl�cheninhalt vom dreieck berechnet mit der formel:
		// A = | v1 x v2 | / 2 (gilt in R3)
		return Vector3f.cross(v1, v2).length() / 2.0f;
	}

	/**
	 * Calculates the area covered by the triangle spanned by v1 and v2.
	 * 
	 * @param v1
	 * @param v2
	 * @return the area
	 */
	public static final float triangleArea(Point2D.Float v1, Point2D.Float v2) {
		// fl�cheninhalt vom dreieck berechnet mit der formel:
		// A = | v1 x v2 | / 2 (gilt in R3)
		return Math.abs(v1.x * v2.y - v1.y * v2.x) / 2.0f;
	}

	/**
	 * Calculates the area covered by the triangle give by 3 points.
	 * 
	 * @param v1
	 * @param v2
	 * @param v3
	 * @return the area
	 */
	public static final float triangleArea(Point2D.Float v1, Point2D.Float v2,
			Point2D.Float v3) {
		return Math.abs((v1.x - v3.x) * (v2.y - v3.y) - (v1.y - v3.y)
				* (v2.x - v3.x)) / 2.0f;
	}

	/**
	 * @return The AffineTransform, which can be used to rotate with the angle
	 *         an direction <code>Vector2f</code> provides.
	 * @param v
	 *            the <code>Vector2f</code> interpreted as a direction vector
	 */
	public static AffineTransform getRotationTransform(Point2D.Float v) {
		float l = (float) v.distance(0, 0);
		return new AffineTransform(v.x / l, v.y / l, -v.y / l, v.x / l, 0f, 0f);
	}

	/**
	 * @return The AffineTransform, which can be used to rotate with the inverse
	 *         angle an direction <code>Vector2f</code> provides.
	 * @param v
	 *            the <code>Vector2f</code> interpreted as a direction vector
	 */
	public static AffineTransform getInverseRotationTransform(Point2D.Float v) {
		float l = (float) v.distance(0, 0);
		return new AffineTransform(v.x / l, -v.y / l, v.y / l, v.x / l, 0f, 0f);
	}
}