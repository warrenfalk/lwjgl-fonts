/* $Id: Vector3f.java,v 1.3 2005/08/20 10:15:55 joda Exp $
 * Created on 22.07.2004
 */
package org.lwjgl.font.util;

/**
 * Implements an Vector in 3D space.
 * 
 * @author Ralf Petring
 * @author funsheep
 */
public class Vector3f implements Cloneable
{

	/** The tolerance we accept when prooving length against a value (e.g. 1.0f). */
	public final static float TOLERANCE = 1E-6f;

	/** x component*/
	public float x;
	/** y component*/
	public float y;
	/** z component*/
	public float z;
	//public float dx, dy;		// tkoch: only for test purposes regarding inclusion of .3DS loader


	/**
	 * Constructs a new vector.
	 * 
	 * @param x the first coordinate
	 * @param y the second coordinate
	 * @param z the third coordinate
	 */
	public Vector3f(final float x, final float y, final float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Constructs a new vector.
	 * @param vector new vector's coordinates
	 */
	public Vector3f(final float[] vector)
	{
		this(vector[0], vector[1], vector[2]);
	}

	/**
	 * Constructs a new vector whith (0,0,0) as coordinates.
	 */
	public Vector3f()
	{
		this(0, 0, 0);
	}

	/**
	 * Copyconstructor.
	 * 
	 * @param v the vector to be copied
	 */
	public Vector3f(final Vector3f v)
	{
		this(v.x, v.y, v.z);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object o)
	{
		if (o instanceof Vector3f)
			return this.equals((Vector3f)o);

		return false;
	}

	/**
	 * Compares this vector with the given vector.
	 * 
	 * @param v the vector to be used for compare
	 * @return <code>true</code> if this vector has the same components as the argument vector
	 * <code>v</code>, false otherwise
	 */
	public boolean equals(Vector3f v)
	{
		if (v == null)
			return false;

		return (this.x == v.x && this.y == v.y && this.z == v.z);
	}

	/**
	 * Compares this vector with the given vector.
	 * <p>Note: tolerance is applied per component, thus all vectors
	 * which equal this vector will form a box.</p>
	 * @param v the vector to be used for compare
	 * @param tolerance accepted tolerance
	 * @return <code>true</code> if this the components of this vector are equal to the argument vector
	 * <code>v</code> with a tolerance of argument <code>tolerance</code>, false otherwise
	 */
	public boolean equals(Vector3f v, float tolerance)
	{
		if (v == null)
			return false;

		return (Math.abs(this.x-v.x)<tolerance &&
				Math.abs(this.y-v.y)<tolerance &&
				Math.abs(this.z-v.z)<tolerance);
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode()
	{
		int i1 = Float.floatToIntBits(this.x);
		int i2 = Float.floatToIntBits(this.y);
		int i3 = Float.floatToIntBits(this.z);

		return i1 ^ ((i2 << 8) | (i2 >>> 24)) ^ ((i3 << 16) | (i3 >>> 16));
	}

	/**
	 * Calculates the (squared) distance between two Vectors.
	 * 
	 * @param v1
	 * @param v2
	 * @return the distance (squared)
	 */
	public static float distanceSquared(Vector3f v1, Vector3f v2)
	{
		float x = v1.x - v2.x;
		float y = v1.y - v2.y;
		float z = v1.z - v2.z;
		return x*x + y*y + z*z;
	}

	/**
	 * Calculates the distance between two Vectors.
	 * 
	 * @param v1
	 * @param v2
	 * @return the distance
	 */
	public static float distance(Vector3f v1, Vector3f v2)
	{
		return (float)Math.sqrt(distanceSquared(v1, v2));
	}

	/**
	 * Adds the given float values to the coordinates of this vector.
	 * 
	 * @param dx the value to be added to the x coordinate
	 * @param dy the value to be added to the y coordinate
	 * @param dz the value to be added to the z coordinate
	 * @return this
	 */
	public Vector3f add(final float dx, final float dy, final float dz)
	{
		this.x += dx;
		this.y += dy;
		this.z += dz;
		return this;
	}

	/**
	 * Adds a given vector to this vector.
	 * 
	 * @param v the vector to be added
	 * @return this
	 */
	public Vector3f add(final Vector3f v)
	{
		return this.add(v.x, v.y, v.z);
	}

	/**
	 * Adds to this vector the given one with the given scale.
	 * @param v A vector to add.
	 * @param d The length of the vector, to be added.
	 * @return this
	 */
	public Vector3f addScaled(final Vector3f v, final float d)
	{
		return this.add(v.x * d, v.y * d, v.z * d);
	}

	/**
	 * Substracts a given vector from this vector.
	 * 
	 * @param v the vector to be substracted
	 * @return this
	 */
	public Vector3f sub(final Vector3f v)
	{
		return this.sub(v.x, v.y, v.z);
	}

	/**
	 * Substracts the given float values from the coordinates of this vector.
	 * 
	 * @param dx the value to be substracted from the x coordinate
	 * @param dy the value to be substracted from the y coordinate
	 * @param dz the value to be substracted from the z coordinate
	 * @return this
	 */
	public Vector3f sub(final float dx, final float dy, final float dz)
	{
		this.x -= dx;
		this.y -= dy;
		this.z -= dz;
		return this;
	}

	/**
	 * Multiplies the coordinates of this vector with the given float values.
	 * 
	 * @param dx the value to be multiplied the x coordinate
	 * @param dy the value to be multiplied the y coordinate
	 * @param dz the value to be multiplied the z coordinate
	 * @return this
	 */
	public Vector3f scale(final float dx, final float dy, final float dz)
	{
		this.x *= dx;
		this.y *= dy;
		this.z *= dz;
		return this;
	}

	/**
	 * Multiplies all coordinates of this vector with the given float value.
	 * 
	 * @param d the value to be multiplied with all coordinates
	 * @return this
	 */
	public Vector3f scale(final float d)
	{
		return this.scale(d, d, d);
	}

	/**
	 * Calculates the dotprodukt of this vector and the given vector.
	 * 
	 * @param v the vector to be used for calculation
	 * @return the dotproduct
	 */
	public float dot(final Vector3f v)
	{
		return this.x * v.x + this.y * v.y + this.z * v.z;
	}

	/**
	 * Normalizes this vector.
	 * 
	 * @return this
	 */
	public Vector3f normalize()
	{
		float l = this.length();
		if (l != 0)
		{
			this.x /= l;
			this.y /= l;
			this.z /= l;
		}
		return this;
	}

	//Daniel K. wanted this name
	/**
	 * Improved version of {@link #normalize()} which also works for VERY short vectors,
	 * or very large ones. This implementation is slighty slower than {@link #normalize()}.
	 * @return this
	 */
	public Vector3f normalize2()
	{
		if (this.isZero())
			throw new IllegalStateException("this is the zero vector");
		if (this.isInvalid())
			throw new IllegalStateException("invalid vector");

		while (x>-0.5f && x<0.5f && y>-0.5f && y<0.5f && z>-0.5f && z<0.5f)
		{
			x *= 2;
			y *= 2;
			z *= 2;
		}

		while (x<=-1.0f || x>=1.0f || y<=-1.0f || y>=1.0f || z<=-1.0f || z>=1.0f)
		{
			x /= 2;
			y /= 2;
			z /= 2;
		}

		float l = this.length();
		this.x /= l;
		this.y /= l;
		this.z /= l;

		return this;
	}

	/**
	 * Returns <code>true</code> if vector is {@link Float#NEGATIVE_INFINITY},
	 * {@link Float#POSITIVE_INFINITY} or {@link Float#NaN}
	 * @return  <code>true</code> if vector is {@link Float#NEGATIVE_INFINITY},
	 * {@link Float#POSITIVE_INFINITY} or {@link Float#NaN}.
	 */
	public boolean isInvalid()
	{
		return Float.isInfinite(x) || Float.isInfinite(y) || Float.isInfinite(z)
			|| Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z);
	}

	/**
	 * Returns <code>true</code> iff x exactly zero (0,0,0).
	 * @return <code>true</code> iff x exactly zero (0,0,0).
	 */
	public boolean isZero()
	{
		return x==0f && y==0f && z==0f;
	}

	/**
	 * Returns <code>true</code> if vector is nearly zero (0,0,0).
	 * @param tolerance accepted tolerance
	 * @return <code>true</code> if vector is nearly zero (0,0,0).
	 */
	public boolean isZero(float tolerance)
	{
		return Math.abs(this.x)<tolerance &&
			Math.abs(this.y)<tolerance &&
			Math.abs(this.z)<tolerance;
	}

	/**
	 * Calculates the angle between this vector and the given vector.
	 * 
	 * @param v the vector to be used for calculation
	 * @return the angle between this and v
	 */
	public float angle(final Vector3f v)
	{
		float dls = dot(v) / (this.length() * v.length());
		if (dls < -1.0f)
			dls = -1.0f;
		else if (dls > 1.0f)
			dls = 1.0f;
		return (float)Math.acos(dls);
	}

	/**
	 * Sets all coordinates of this vector to zero.
	 * 
	 * @return this
	 */
	public Vector3f setZero()
	{
		this.x = 0;
		this.y = 0;
		this.z = 0;
		return this;
	}

	/**
	 * Copies all values from the given vector to this vector.
	 * 
	 * @param v the vector to be copied
	 * @return this
	 */
	public Vector3f set(final Vector3f v)
	{
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		return this;
	}

	public Vector3f set(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	/**
	 * Sets the vector's components to the first 3 values
	 * in argument <code>array</code>.
	 * @param array array which contains the new values
	 * @return this
	 */
	public Vector3f set(final float[] array)
	{
		this.x = array[0];
		this.y = array[1];
		this.z = array[2];
		return this;
	}

	/**
	 * Negates all coordinates of this vector.
	 * 
	 * @return this
	 */
	public Vector3f negate()
	{
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		return this;
	}


	/**
	 * Returns the squared length of this vector.
	 * 
	 * @return the squared length of this vector
	 */
	public float lengthSquared()
	{
		return (this.x * this.x + this.y * this.y + this.z * this.z);
	}

	/**
	 * Returns the length of this vector.
	 * 
	 * @return the length of this vector
	 */
	public float length()
	{
		return (float)Math.sqrt(this.lengthSquared());
	}

	/**
	 * Calculates the vector cross product of this vector and the given vector.
	 * 
	 * @param v the second vector
	 * @return this
	 */
	public Vector3f cross(final Vector3f v)
	{
		final float a = this.y * v.z - this.z * v.y;
		final float b = this.z * v.x - this.x * v.z;
		final float c = this.x * v.y - this.y * v.x;

		this.x = a;
		this.y = b;
		this.z = c;

		return this;
	}

	/**
	 * Calculates the vector cross product of the two given vectors.
	 * <br><code>a x b = c</code> c is returned.
	 * @param a The first vector.
	 * @param b The second vector.
	 * @return A new vector, holding the cross product of a x b.
	 */
	public static Vector3f cross(final Vector3f a, final Vector3f b)
	{
		return new Vector3f(
				a.y * b.z - a.z * b.y,
				a.z * b.x - a.x * b.z,
				a.x * b.y - a.y * b.x);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new RuntimeException("the roof is on fire", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString()
	{
		return "[" + this.x + "," + this.y + "," + this.z + "]";
	}

	/**
	 * Copies all components of this vector into the first 3 array components.
	 * @param dest array to be filled - may be <code>null</code>
	 * @return argument <code>dest</code> or a new array filled with this
	 * instances components.
	 */
	public float[] toArray(float[] dest)
	{
		if (dest==null)
			dest = new float[3];
		dest[0] = this.x;
		dest[1] = this.y;
		dest[2] = this.z;
		return dest;
	}

	/**
	 * Returns <code>true</code> if this vector is normalized. If it's length is <code>1</code>.
	 * @return <code>true</code> if this vector is normalized.
	 */
	public boolean isNormalized()
	{
		return this.lengthSquared()==1;
	}

	/**
	 * Returns <code>true</code> if this vector is normalized.
	 * If it's length is <code>1</code> within a tolerance of argument <code>tolerance</code>.
	 * @param tolerance amount of error to be ignores - must be positive!
	 * <p>Note: the normal is computed by using {@link #lengthSquared()}, thus
	 * the following equation approximated.
	 * For a Vector3f <code>v</code>:
	 * Correct:<pre>return {@link Math#sqrt(double) sqrt}({@link #lengthSquared()
	 * v.lengthSquared()})>1f-tolerance;</pre>
	 * 
	 * Steps to a more numerical stable and faster test:
	 * <pre>
	 * v.lengthSquared()>(1f-tolerance)^2
	 *  &lt;=&gt; v.lengthSquared()>1f-2f*tolerance+tolerance^2;</pre>
	 * Optimized and approximated result:
	 * <pre>return v.lengthSquared()>1f-2f*tolerance;</pre>
	 * This test is infact less precise than the original test,
	 * but seems to be reasonable, as a tolerance is assumed to be pretty small!
	 * @return <code>true</code> if this vector is normalized.
	 */
	public boolean isNormalized(float tolerance)
	{
		assert tolerance>=0:"tolerance must be positive or 0";
		float tmp1 = this.lengthSquared() - 1f;
		float tmp2 = 2f * tolerance + tolerance * tolerance;
		return tmp1 < tmp2 && -tmp1 < tmp2;
	}

	/**
	 * Returns <code>true</code> if this vector is orthogonal to argument <code>other</code>.
	 * @param other other vector
	 * @return <code>true</code> if this vector is orthogonal to argument <code>other</code>.
	 */
	public boolean isOrthogonal(Vector3f other)
	{
		return this.dot(other)==0;
	}

	/**
	 * Returns <code>true</code> if this vector is orthogonal to argument <code>other</code>.
	 * @param other other vector
	 * @param tolerance amount of error to be ignores - must be positive!
	 * @return <code>true</code> if this vector is orthogonal to argument <code>other</code>.
	 */
	public boolean isOrthogonal(Vector3f other, float tolerance)
	{
		assert tolerance>=0:"tolerance must be positive or 0";
		return this.dot(other)>-tolerance;
	}
}