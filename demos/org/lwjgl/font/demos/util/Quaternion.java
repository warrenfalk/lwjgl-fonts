/* $ Id$
 * Created on 09.11.2004
 */
package org.lwjgl.font.demos.util;

import java.nio.FloatBuffer;

import org.lwjgl.font.util.Vector3f;




/*
 * (c) Copyright 1993, 1994, Silicon Graphics, Inc.
 * ALL RIGHTS RESERVED
 * Permission to use, copy, modify, and distribute this software for
 * any purpose and without fee is hereby granted, provided that the above
 * copyright notice appear in all copies and that both the copyright notice
 * and this permission notice appear in supporting documentation, and that
 * the name of Silicon Graphics, Inc. not be used in advertising
 * or publicity pertaining to distribution of the software without specific,
 * written prior permission.
 * 
 * THE MATERIAL EMBODIED ON THIS SOFTWARE IS PROVIDED TO YOU "AS-IS"
 * AND WITHOUT WARRANTY OF ANY KIND, EXPRESS, IMPLIED OR OTHERWISE,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE.  IN NO EVENT SHALL SILICON
 * GRAPHICS, INC.  BE LIABLE TO YOU OR ANYONE ELSE FOR ANY DIRECT,
 * SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY
 * KIND, OR ANY DAMAGES WHATSOEVER, INCLUDING WITHOUT LIMITATION,
 * LOSS OF PROFIT, LOSS OF USE, SAVINGS OR REVENUE, OR THE CLAIMS OF
 * THIRD PARTIES, WHETHER OR NOT SILICON GRAPHICS, INC.  HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS, HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE
 * POSSESSION, USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 * US Government Users Restricted Rights
 * Use, duplication, or disclosure by the Government is subject to
 * restrictions set forth in FAR 52.227.19(c)(2) or subparagraph
 * (c)(1)(ii) of the Rights in Technical Data and Computer Software
 * clause at DFARS 252.227-7013 and/or in similar or successor
 * clauses in the FAR or the DOD or NASA FAR Supplement.
 * Unpublished-- rights reserved under the copyright laws of the
 * United States.  Contractor/manufacturer is Silicon Graphics,
 * Inc., 2011 N.  Shoreline Blvd., Mountain View, CA 94039-7311.
 * 
 * OpenGL(TM) is a trademark of Silicon Graphics, Inc.
 */

/**
 * <p>trackball.c
 * Trackball code:
 * 
 * Implementation of a virtual trackball.
 * Implemented by Gavin Bell, lots of ideas from Thant Tessman and
 *   the August '88 issue of Siggraph's "Computer Graphics," pp. 121-129.
 * 
 * Vector manip code:
 * 
 * Original code from:
 * David M. Ciemiewicz, Mark Grossman, Henry Moreton, and Paul Haeberli
 * 
 * Much mucking with by:
 * Gavin Bell
 * <p>trackball.h
 * A virtual trackball implementation
 * Written by Gavin Bell for Silicon Graphics, November 1988.
 * @author Gavin Bell, java port by joda
 */
public class Quaternion
{
	/**
	 * Number of times to a quaternion's {@link #add(Quaternion)}
	 * method can be called until the quaternion is automatically normalized.
	 */
	public static final int RENORMCOUNT = 97;

	/**
	 * the first 3 components of this quaternion.
	 */
	public final Vector3f v = new Vector3f();

	/**
	 * the last component
	 */
	public float w = 0 ;

	/**
	 * Number of times the quaternion has been changed.
	 * @see #RENORMCOUNT
	 */
	private int count = 0;

	public Quaternion()
	{
		this.v.setZero();
		this.w = 1;
	}

	/**
	 * Returns a rotation matrix representation of this quaternion.
	 * <p>Note: The first elements of matrix are equal to the first row.
	 * @param matrix array of 16 values to be filled (<code>null</code> is not allowed)
	 * @return a rotation matrix representation of this quaternion.
	 */
	public FloatBuffer toRowArray(FloatBuffer matrix)
	{
		matrix.put(0, 1.0f - 2.0f * (this.v.y * this.v.y + this.v.z * this.v.z));
		matrix.put(4, 2.0f * (this.v.x * this.v.y - this.v.z * this.w));
		matrix.put(8, 2.0f * (this.v.z * this.v.x + this.v.y * this.w));
		matrix.put(12, 0.0f);

		matrix.put(1, 2.0f * (this.v.x * this.v.y + this.v.z * this.w));
		matrix.put(5, 1.0f - 2.0f * (this.v.z * this.v.z + this.v.x * this.v.x));
		matrix.put(9, 2.0f * (this.v.y * this.v.z - this.v.x * this.w));
		matrix.put(13, 0.0f);

		matrix.put(0, 2.0f * (this.v.z * this.v.x - this.v.y * this.w));
		matrix.put(6, 2.0f * (this.v.y * this.v.z + this.v.x * this.w));
		matrix.put(10, 1.0f - 2.0f * (this.v.y * this.v.y + this.v.x * this.v.x));
		matrix.put(14, 0.0f);

		matrix.put(0, 0.0f);
		matrix.put(7, 0.0f);
		matrix.put(11, 0.0f);
		matrix.put(15, 1.0f);
		return matrix;
	}

	/**
	 * Returns a rotation matrix representation of this quaternion.
	 * <p>Note: The first elements of matrix are equal to the first column.
	 *    This reprensentation is the default for OpenGL.
	 * @param matrix array to be filled (<code>null</code> is not allowed)
	 * @return a rotation matrix representation of this quaternion.
	 */
	public FloatBuffer toColumArray(FloatBuffer matrix)
	{
		matrix.put(0, 1.0f - 2.0f * (this.v.y * this.v.y + this.v.z * this.v.z));
		matrix.put(1, 2.0f * (this.v.x * this.v.y - this.v.z * this.w));
		matrix.put(2, 2.0f * (this.v.z * this.v.x + this.v.y * this.w));
		matrix.put(3, 0.0f);

		matrix.put(4, 2.0f * (this.v.x * this.v.y + this.v.z * this.w));
		matrix.put(5, 1.0f - 2.0f * (this.v.z * this.v.z + this.v.x * this.v.x));
		matrix.put(6, 2.0f * (this.v.y * this.v.z - this.v.x * this.w));
		matrix.put(7, 0.0f);

		matrix.put(8, 2.0f * (this.v.z * this.v.x - this.v.y * this.w));
		matrix.put(9, 2.0f * (this.v.y * this.v.z + this.v.x * this.w));
		matrix.put(10, 1.0f - 2.0f * (this.v.y * this.v.y + this.v.x * this.v.x));
		matrix.put(11, 0.0f);

		matrix.put(12, 0.0f);
		matrix.put(13, 0.0f);
		matrix.put(14, 0.0f);
		matrix.put(15, 1.0f);
		return matrix;
	}

	/**
	 * Sets this quaternion to a rotation about an axis (defined by
	 * the given vector <code>axis</code>) and
	 * an angle <code>phi</code> about which to rotate.
	 * The angle is expressed in radians.
	 * @param axis axis to rotate about
	 * @param phi degree in radians
	 * @return this instance
	 */
	public Quaternion setToAxisRotation(Vector3f axis, float phi)
	{
		this.v.set(axis);
		this.v.normalize();
		this.v.scale((float)Math.sin(phi/2.0f));
		this.w = (float)Math.cos(phi/2.0f);
		return this;
	}

	/**
	 * Add another quaternion rotation to this instance.
	 * Modifies this quaternion to represent quaternion rotation,
	 * figure out the equivalent single rotation and stuff it into dest.
	 * 
	 * This routine also normalizes the result every RENORMCOUNT times it is
	 * called, to keep error from creeping in.
	 * 
	 * <p>Note: This method is written so that q may be the same as this.
	 * @param q another quaternion or the same
	 * @return this instance
	 */
	public Quaternion add(Quaternion q)
	{
		Vector3f t1, t2, t3;
		float dot = this.v.dot(q.v);

		t1 = new Vector3f(this.v).scale(q.w);
		t2 = new Vector3f(q.v).scale(this.w);
		t3 = Vector3f.cross(q.v, this.v);

		this.v.set(t1).add(t2);
		this.v.add(t3);
		this.w = this.w * q.w - dot;

		if (++this.count > RENORMCOUNT)
		{
			this.count = 0;
			this.normalize();
		}
		return this;
	}

	/**
	 * Set this quaternion to the values of the other quaternion.
	 * <p>Note: renormalization count is also copied
	 * @param toCopy instance to copy values from
	 * @return this instance
	 */
	public Quaternion set(Quaternion toCopy)
	{
		this.v.set(toCopy.v);
		this.w = toCopy.w;
		this.count = toCopy.count;
		return this;
	}

	/**
	 * Normalizes this quaternion.
	 * Quaternions always obey:  a^2 + b^2 + c^2 + d^2 = 1.0
	 * If they don't add up to 1.0, dividing by their magnitued will
	 * renormalize them.
	 * 
	 * <p>Note: See the following for more information on quaternions:
	 * <ul>
	 * 	 <li>Shoemake, K., Animating rotation with quaternion curves, Computer
	 *       Graphics 19, No 3 (Proc. SIGGRAPH'85), 245-254, 1985.
	 *   <li>Pletinckx, D., Quaternion calculus as a basic tool in computer
	 *       graphics, The Visual Computer 5, 2-13, 1989.
	 * </ul>
	 * @return this instance
	 */
	public Quaternion normalize()
	{
		float mag = 1/(this.v.lengthSquared() + this.w*this.w);
		// orig: v.div(mag)
		this.v.scale(mag);
		// orig: w /= mag
		this.w *= mag;
		return this;
	}
}