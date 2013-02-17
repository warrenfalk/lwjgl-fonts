/* $Id: FTTesselatorCallback.java,v 1.2 2005/07/27 23:14:31 joda Exp $
 * Created on 05.11.2004
 */
package org.lwjgl.font;

import org.lwjgl.util.glu.GLUtessellatorCallbackAdapter;

/**
 * This is class contains all previously separate tesselation callbacks. JOGL
 * requires these callbacks to be combined in a class.
 * 
 * @author joda
 */
public class FTTesselatorCallback extends GLUtessellatorCallbackAdapter {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beginData(int type, Object polygonData) {
		FTMesh mesh = (FTMesh) polygonData;
		mesh.begin(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void vertexData(Object vertexData, Object polygonData) {
		FTMesh mesh = (FTMesh) polygonData;
		// TODO:
		assert (vertexData instanceof double[]) : "vertex data is not a double array";
		mesh.addPoint((double[]) vertexData);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endData(Object polygonData) {
		FTMesh mesh = (FTMesh) polygonData;
		mesh.end();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void combineData(double[] coords, Object[] data, float[] weight,
			Object[] outData, Object polygonData) {
		// usually only called for the first vertex of the polygon (as this is
		// repeated)
		// and has to be combined everytime.
		FTMesh mesh = (FTMesh) polygonData;
		outData[0] = mesh.combine(coords);// ( coords[0], coords[1], coords[2]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void errorData(int errnum, Object polygonData) {
		FTMesh mesh = (FTMesh) polygonData;
		mesh.setError(errnum);
	}
}