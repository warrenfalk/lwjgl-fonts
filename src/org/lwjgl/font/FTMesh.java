/* $ Id$
 * Created on 04.11.2004
 */
package org.lwjgl.font;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// typedef FTVector<FTTesselation*> TesselationVector;
// typedef FTList<FTPoint> PointList;

/**
 * FTMesh is a container of FTTesselation's that make up a polygon glyph
 */
public class FTMesh {

	/**
	 * The current sub mesh that we are constructing.
	 */
	private FTTesselation currentTesselation;

	/**
	 * Holds each sub mesh that comprises this glyph.
	 */
	private List<FTTesselation> tesselationList;

	/**
	 * Holds extra points created by gluTesselator. See ftglCombine.
	 */
	private List<double[]> tempPointList;

	/**
	 * GL ERROR returned by the glu tesselator
	 */
	private int err;

	/**
	 * Default constructor
	 */
	public FTMesh() {
		currentTesselation = null;
		err = 0;
		ArrayList<FTTesselation> help = new ArrayList<FTTesselation>();
		help.ensureCapacity(16);
		tesselationList = help;
		tempPointList = new LinkedList<double[]>();
	}

	/**
	 * Destructor
	 */
	public void dispose() {
		for (int t = 0; t < tesselationList.size(); ++t)
			tesselationList.get(t).dispose();
		tesselationList.clear();
		tempPointList.clear();
	}

	/**
	 * 
	 */
	public void addPoint(double[] point) {
		currentTesselation.addPoint(point);
	}

	/**
	 * <p>
	 * Note: returns Vector reference instead of component x pointer
	 */
	// public Vector3d combine( final double x, final double y, final double z)
	// {
	// Vector3d v = new Vector3d( x, y, z);
	// tempPointList.add(v);
	// //return &tempPointList.back().x;
	// return v;
	// }
	public double[] combine(final double[] help) {
		// double [] v = new double[] {x,y,z};
		tempPointList.add(help);
		// return &tempPointList.back().x;
		return help;
	}

	/**
	 * 
	 */
	public void begin(int meshType) {
		currentTesselation = new FTTesselation(meshType);
	}

	/**
	 * 
	 */
	public void end() {
		tesselationList.add(currentTesselation);
	}

	/**
	 * Used to set the GL ERROR returned by the glu tesselator.
	 * 
	 * @param e
	 *            new error code
	 */
	public void setError(int e) {
		err = e;
	}

	/**
	 * Returns the tesselation count.
	 * 
	 * @return the tesselation count.
	 */
	public int tesselationCount() // const
	{
		return tesselationList.size();
	}

	/**
	 * 
	 */
	public FTTesselation getTesselation(int index) // const;
	{
		return tesselationList.get(index);
	}

	/**
	 * 
	 */
	public List<double[]> getTempPointList() // const
	{
		return tempPointList;
	}

	/**
	 * Get the GL ERROR returned by the glu tesselator.
	 */
	public int getError() // const
	{
		return err;
	}
}