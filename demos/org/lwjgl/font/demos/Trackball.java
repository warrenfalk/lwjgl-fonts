/* $ Id$
 * Created on 09.11.2004
 */
package org.lwjgl.font.demos;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.nio.FloatBuffer;

import org.lwjgl.font.demos.util.Quaternion;
import org.lwjgl.font.util.Vector3f;


/**
 *  Simple trackball-like motion adapted (ripped off) from projtex.c
 *  (written by David Yu and David Blythe).  See the SIGGRAPH '96
 *  Advanced OpenGL course notes.
 * 
 * 
 *  Usage:
 *  <ul>
 *    <li>call tbInit() in before any other tb call
 *    <li>call tbReshape() from the reshape callback
 *    <li>call tbMatrix() to get the trackball matrix rotation
 *    <li>call tbStartMotion() to begin trackball movememt
 *    <li>call tbStopMotion() to stop trackball movememt
 *    <li>call tbMotion() from the motion callback
 *    <li>call tbAnimate(GL_TRUE) if you want the trackball to continue
 *    <li>spinning after the mouse button has been released
 *    <li>call tbAnimate(GL_FALSE) if you want the trackball to stop
 *       spinning after the mouse button has been released
 *  </ul>
 *  Typical setup:
 * 
 *  <code><pre>
	void
	init(void)
	{
	  tbInit(GLUT_MIDDLE_BUTTON);
	  tbAnimate(GL_TRUE);
	  . . .
	}

	void
	reshape(int width, int height)
	{
	  tbReshape(width, height);
	  . . .
	}

	void
	display(void)
	{
	  glPushMatrix();

	  tbMatrix();
	  . . . draw the scene . . .

	  glPopMatrix();
	}

	void
	mouse(int button, int state, int x, int y)
	{
	  tbMouse(button, state, x, y);
	  . . .
	}

	void
	motion(int x, int y)
	{
	  tbMotion(x, y);
	  . . .
	}

	int
	main(int argc, char** argv)
	{
	  . . .
	  init();
	  glutReshapeFunc(reshape);
	  glutDisplayFunc(display);
	  glutMouseFunc(mouse);
	  glutMotionFunc(motion);
	  . . .
	}
	</pre></code>
 * @author David Yu and David Blythe, java port by joda
 * */
public class Trackball extends MouseAdapter implements MouseMotionListener
{
	/*
	 * This size should really be based on the distance from the center of
	 * rotation to the point on the object underneath the mouse.  That
	 * point would then track the mouse as closely as possible.  This is a
	 * simple example, though, so that is left as an Exercise for the
	 * Programmer.
	 */
	public final static float TRACKBALLSIZE = 0.4f;

	/* globals */
	private long    tb_lasttime;

	Quaternion curquat = new Quaternion();
	Quaternion lastquat = new Quaternion();
	int beginx, beginy;

	int tb_width;
	int tb_height;
	int tb_button;

	boolean tb_tracking = false;

	public void tbInit(int button)
	{
		this.tb_button = button;
		trackball(this.curquat, 0.0f, 0.0f, 0.0f, 0.0f);
	}


	public FloatBuffer tbMatrix(FloatBuffer matrix)
	{
		return this.curquat.toColumArray(matrix);
	}

	public void	tbReshape(int width, int height)
	{
		tb_width  = width;
		tb_height = height;
	}

	/**
	 * {@inheritDoc}
	 */
	public void mouseMoved(int x, int y, int dx, int dy)
	{
		  if (tb_tracking)
		  {
			trackball(lastquat,
			  (2.0f * beginx - tb_width) / tb_width,
			  (tb_height - 2.0f * beginy) / tb_height,
			  (2.0f * x - tb_width) / tb_width,
			  (tb_height - 2.0f * y) / tb_height
			);
			beginx = x;
			beginy = y;
			this.curquat.add(this.lastquat);
		  }
	}

	void _tbStartMotion(int x, int y)
	{
	  tb_tracking = true;
	  beginx = x;
	  beginy = y;
	}

	void _tbStopMotion()
	{
	  tb_tracking = false;
	}

	/**
	 * {@inheritDoc}
	 * <p>Note: replacement for tbMouse(int button...)
	 */
	public void mousePressed(int button, int x, int y)
	{
		  if (button == this.tb_button)
			_tbStartMotion(x, y);
	}
	/**
	 * {@inheritDoc}
	 * <p>Note: replacement for tbMouse(int button...)
	 */
	public void mouseReleased(int button, int x, int y)
	{
		  //else if (state == GLUT_UP && button == tb_button)
		if (button == this.tb_button)
			_tbStopMotion();
	}

	/**
	 * Ok, simulate a track-ball.  Project the points onto the virtual
	 * trackball, then figure out the axis of rotation, which is the cross
	 * product of P1 P2 and O P1 (O is the center of the ball, 0,0,0)
	 * Note:  This is a deformed trackball-- is a trackball in the center,
	 * but is deformed into a hyperbolic sheet of rotation away from the
	 * center.  This particular function was chosen after trying out
	 * several variations.
	 * 
	 * It is assumed that the arguments to this routine are in the range
	 * (-1.0 ... 1.0)
	 * Pass the x and y coordinates of the last and current positions of
	 * the mouse, scaled so they are from (-1.0 ... 1.0).
	 * 
	 * The resulting rotation is stored in the quaternion rotation (first paramater).
	 * @param q quaternion to be set
	 * @param p1x start point of rotation
	 * @param p1y start point of rotation
	 * @param p2x end point of rotation
	 * @param p2y end point of rotation
	 */
	void trackball(Quaternion q, float p1x, float p1y, float p2x, float p2y)
	{
		Vector3f a; /* Axis of rotation */
		float phi;  /* how much to rotate about axis */
		Vector3f p1, p2, d;
		float t;

		if (p1x == p2x && p1y == p2y) {
			/* Zero rotation */
			q.v.setZero();
			q.w = 1.0f;
			return;
		}

		/*
		 * First, figure out z-coordinates for projection of P1 and P2 to
		 * deformed sphere
		 */
		p1 = new Vector3f(p1x,p1y,tb_project_to_sphere(TRACKBALLSIZE,p1x,p1y));
		p2 = new Vector3f(p2x,p2y,tb_project_to_sphere(TRACKBALLSIZE,p2x,p2y));

		/*
		 *  Now, we want the cross product of P1 and P2
		 */
		a = Vector3f.cross(p2,p1);

		/*
		 *  Figure out how much to rotate around that axis.
		 */
		d = new Vector3f(p1).sub(p2);
		t = (float)d.length() / (2.0f*TRACKBALLSIZE);

		/*
		 * Avoid problems with out-of-control values...
		 */
		if (t > 1.0) t = 1.0f;
		if (t < -1.0) t = -1.0f;
		phi = 2.0f * (float)Math.asin(t);

		q.setToAxisRotation(a,phi);
	}

	/**
	 * Project an x,y pair onto a sphere of radius r OR a hyperbolic sheet
	 * if we are away from the center of the sphere.
	 * @param r radius of the sphere
	 * @param x point on the sphere or inside it?
	 * @param y point on the sphere or inside it?
	 * @return ???
	 */
	private static float tb_project_to_sphere(float r, float x, float y)
	{
		float d, t, z;

		d = (float)Math.sqrt(x*x + y*y);
		if (d < r * 0.70710678118654752440) {    /* Inside sphere */
			z = (float)Math.sqrt(r*r - d*d);
		} else {           /* On hyperbola */
			t = r / 1.41421356237309504880f;
			z = t*t / d;
		}
		return z;
	}
}