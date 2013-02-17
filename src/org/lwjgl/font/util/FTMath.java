/* $Id: FTMath.java,v 1.1 2004/11/05 13:54:54 funsheep Exp $
 * Created on 05.11.2004
 */
package org.lwjgl.font.util;

/**
 * Some methods, to make the life easier.
 * @author funsheep
 */
public class FTMath
{

	/**
	 * Generates the next power of two.
	 * @param in An int.
	 * @return The next power of two.
	 */
	public static final int nextPowerOf2(int in)
	{
		 in -= 1;

		 in |= in >> 16;
		 in |= in >> 8;
		 in |= in >> 4;
		 in |= in >> 2;
		 in |= in >> 1;

		 return in + 1;
	}

}