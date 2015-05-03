/* $Id: Vector4f.java,v 1.1 2005/08/20 10:15:55 joda Exp $
 * Created on 22.07.2004
 */
package org.lwjgl.font.util;

/**
 * Implementation of the 4 dimensional vector.
 * 
 * @author Ralf Petring
 * @author funsheep
 */
public class Vector4f implements Cloneable {
	/** x component */
	public float x;
	/** y component */
	public float y;
	/** z component */
	public float z;
	/** w component */
	public float w;

	/**
	 * Constructs a new vector whith (0,0,0,1) as coordinates.
	 */
	public Vector4f() {
		this(0, 0, 0, 1);
	}

	/**
	 * Creates a new vector.
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @param z
	 *            the z coordinate
	 * @param w
	 *            the w coordinate
	 */
	public Vector4f(final float x, final float y, final float z, final float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	/**
	 * Copyconstructor.
	 * 
	 * @param v
	 *            the vector to copy
	 */
	public Vector4f(final Vector4f v) {
		this(v.x, v.y, v.z, v.w);
	}

	/**
	 * Adds the given values to this vector.
	 * 
	 * @param dx
	 *            the x value to add
	 * @param dy
	 *            the y value to add
	 * @param dz
	 *            the z value to add
	 * @return this vector
	 */
	public Vector4f add(final float dx, final float dy, final float dz) {
		x += dx;
		y += dy;
		z += dz;
		return this;
	}

	/**
	 * Adds a vector to this vector.
	 * 
	 * @param v
	 *            the vector to add
	 * @return this vector
	 */
	public Vector4f add(final Vector4f v) {
		return this.add(v.x, v.y, v.z);
	}

	/**
	 * Substracts the given values from this vector.
	 * 
	 * @param dx
	 *            the x value to substract
	 * @param dy
	 *            the y value to substract
	 * @param dz
	 *            the z value to substract
	 * @return this vector
	 */
	public Vector4f sub(final float dx, final float dy, final float dz) {
		return this.add(-dx, -dy, -dz);
	}

	/**
	 * Substracts a given vector from this one.
	 * 
	 * @param v
	 *            the vector to substract
	 * @return this vector
	 */
	public Vector4f sub(final Vector4f v) {
		return this.add(-v.x, -v.y, -v.z);
	}

	/**
	 * Multiplies the coordinates of this vector with the given float values.
	 * 
	 * @param dx
	 *            the value to be multiplied the x coordinate
	 * @param dy
	 *            the value to be multiplied the y coordinate
	 * @param dz
	 *            the value to be multiplied the z coordinate
	 * @return this vector
	 */
	public Vector4f scale(final float dx, final float dy, final float dz) {
		x *= dx;
		y *= dy;
		z *= dz;
		return this;
	}

	/**
	 * Multiplies the coordinates of this vector with the given float value.
	 * 
	 * @param d
	 *            the value to be multiplied with all coordinates
	 * @return this vector
	 */
	public Vector4f scale(final float d) {
		return this.scale(d, d, d);
	}

	/**
	 * Returns the squared length of this vector.
	 * 
	 * @return the squared length of this vector
	 */
	public final float lengthSquared() {
		return (x * x + y * y + z * z); // TODO needs w?
	}

	/**
	 * Returns the length of this vector.
	 * 
	 * @return the length of this vector
	 */
	public final float length() {
		// TODO needs w?
		return (float) Math.sqrt(lengthSquared());
	}

	/**
	 * Normalizes this vector.
	 * 
	 * @return this vector
	 */
	public final Vector4f normalize() {
		// TODO needs w?
		float norm = 1f / length();

		x *= norm;
		y *= norm;
		z *= norm;

		return this;
	}

	/**
	 * Calculates the dotprodukt of this vector and the given vector.
	 * 
	 * @param v
	 *            the second vector
	 * @return the scalarproduct between this and the second vector
	 */
	public final float dot(final Vector4f v) {
		return x * v.x + y * v.y + z * v.z + w * v.w; // TODO w component also?
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("the roof is on fire", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "[" + x + "," + y + "," + z + "," + w + "]";
	}

}