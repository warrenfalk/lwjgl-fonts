/* $ Id$
 * Created on 08.11.2004
 */
package org.lwjgl.font.demos;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.OpenType;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import org.lwjgl.font.FTBBox;
import org.lwjgl.font.glfont.FTFont;
import org.lwjgl.font.glfont.FTGLBitmapFont;
import org.lwjgl.font.glfont.FTGLExtrdFont;
import org.lwjgl.font.glfont.FTGLOutlineFont;
import org.lwjgl.font.glfont.FTGLPixmapFont;
import org.lwjgl.font.glfont.FTGLPolygonFont;
import org.lwjgl.font.glfont.FTGLTextureFont;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

/**
 * TODO javadoc
 * 
 * @author joda
 */
public class FTGLDemo implements ActionListener {

	public static final int EDITING = 1;
	public static final int INTERACTIVE = 2;

	public static final int FTGL_BITMAP = 0;
	public static final int FTGL_PIXMAP = 1;

	public static final int FTGL_OUTLINE = 2;
	public static final int FTGL_POLYGON = 3;
	public static final int FTGL_EXTRUDE = 4;
	public static final int FTGL_TEXTURE = 5;

	/** number of frames rendered */
	public static volatile int framesCounted = 0;

	/**
	 * The 6 different types of supported fonts.
	 * <p>
	 * Note: previously static FTFont* fonts[6];
	 */
	private FTFont[] fonts = new FTFont[6];

	/**
	 * default font used to display text
	 * <p>
	 * Note: previously static FTGLPixmapFont* infoFont;
	 */
	private FTFont infoFont = null;

	private FontRenderContext context = null;
	private Font font = null;
	private Font updateFontTo = null;

	private int current_font = FTGL_EXTRUDE;
	public volatile boolean running = false;

	private int w_win = 640, h_win = 480;
	private int mode = EDITING;
	private float zoom = 2.0f;

	private Trackball tb = new Trackball();

	private String myString = "ftgl";// /default empty String ?
	private int carat = myString.length();

	/**
	 * Set up lighting.
	 */
	private void setUpLighting() {
		// Set up lighting.
		FloatBuffer light1_ambient = makeFloatBuffer(new float[] { 1.0f, 1.0f,
				1.0f, 1.0f });
		FloatBuffer light1_diffuse = makeFloatBuffer(new float[] { 1.0f, 0.9f,
				0.9f, 1.0f });
		FloatBuffer light1_specular = makeFloatBuffer(new float[] { 1.0f, 0.7f,
				0.7f, 1.0f });
		FloatBuffer light1_position = makeFloatBuffer(new float[] { -1.0f,
				1.0f, 1.0f, 0.0f });
		GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, light1_ambient);
		GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, light1_diffuse);
		GL11.glLight(GL11.GL_LIGHT1, GL11.GL_SPECULAR, light1_specular);
		GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, light1_position);
		GL11.glEnable(GL11.GL_LIGHT1);

		FloatBuffer light2_ambient = makeFloatBuffer(new float[] { 0.2f, 0.2f,
				0.2f, 1.0f });
		FloatBuffer light2_diffuse = makeFloatBuffer(new float[] { 0.9f, 0.9f,
				0.9f, 1.0f });
		FloatBuffer light2_specular = makeFloatBuffer(new float[] { 0.7f, 0.7f,
				0.7f, 1.0f });
		FloatBuffer light2_position = makeFloatBuffer(new float[] { 1.0f,
				-1.0f, -1.0f, 0.0f });
		GL11.glLight(GL11.GL_LIGHT2, GL11.GL_AMBIENT, light2_ambient);
		GL11.glLight(GL11.GL_LIGHT2, GL11.GL_DIFFUSE, light2_diffuse);
		GL11.glLight(GL11.GL_LIGHT2, GL11.GL_SPECULAR, light2_specular);
		GL11.glLight(GL11.GL_LIGHT2, GL11.GL_POSITION, light2_position);
		// glEnableLIGHT2);

		FloatBuffer front_emission = makeFloatBuffer(new float[] { 0.3f, 0.2f,
				0.1f, 0.0f });
		FloatBuffer front_ambient = makeFloatBuffer(new float[] { 0.2f, 0.2f,
				0.2f, 0.0f });
		FloatBuffer front_diffuse = makeFloatBuffer(new float[] { 0.95f, 0.95f,
				0.8f, 0.0f });
		FloatBuffer front_specular = makeFloatBuffer(new float[] { 0.6f, 0.6f,
				0.6f, 0.0f });
		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_EMISSION, front_emission);
		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT, front_ambient);
		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, front_diffuse);
		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, front_specular);
		GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, 16.0f);
		GL11.glColor4f(front_diffuse.get(0), front_diffuse.get(1),
				front_diffuse.get(2), front_diffuse.get(3));

		GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_FALSE);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glShadeModel(GL11.GL_SMOOTH);
	}

	private FloatBuffer makeFloatBuffer(float[] fs) {
		FloatBuffer fb = ByteBuffer.allocateDirect(fs.length * 4)
				.order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
		fb.put(fs);
		fb.flip();
		return fb;
	}

	/**
	 * Create fonts.
	 */
	private void setUpFonts(FontRenderContext context) {
		try {
			fonts[FTGL_BITMAP] = new FTGLBitmapFont(font, context);
			fonts[FTGL_PIXMAP] = new FTGLPixmapFont(font, context);
			fonts[FTGL_OUTLINE] = new FTGLOutlineFont(font, context);
			fonts[FTGL_POLYGON] = new FTGLPolygonFont(font, context);
			fonts[FTGL_EXTRUDE] = new FTGLExtrdFont(font, context);
			fonts[FTGL_TEXTURE] = new FTGLTextureFont(font, context);
		} catch (IllegalStateException ex) {

			throw ex;
		}
		for (FTFont font2 : fonts) {
			// TODO: Fix implementation to support this :D
			// this.fonts[i].faceSize(144f);
		}
		((FTGLExtrdFont) fonts[FTGL_EXTRUDE]).setDepth(20f);

		infoFont = new FTGLPixmapFont(font);
		ascenderfont = new FTGLPolygonFont(font.deriveFont(20f));

		infoFont.faceSize(18);

		initFonts();
	}

	private final void hasFontChanged() {
		if (updateFontTo != null) {
			font = updateFontTo;
			setUpFonts(FTFont.STANDARDCONTEXT);
			updateFontTo = null;
		}
	}

	/**
	 * This method sets the new GL context, if it has changed. This method sets
	 * the new GLU context, if it has changed. All engines, deriving from this
	 * class have to overwrite this method, to actualise all variables pointing
	 * on the old context (in Object3Ds or Mesh3Ds, e.g.). When overriding this
	 * method, super.setGL(GL gl); must be called!
	 * 
	 * @param gl
	 *            The new GL context.
	 * @param glu
	 *            The new GLU context.
	 */
	protected void initFonts() // TODO: rename
	{
		for (FTFont font2 : fonts)
			if (font2 != null) {
				font2.init();
				font2.clearCache(true);
			}
		infoFont.init();
		ascenderfont.init();
	}

	private FTFont ascenderfont;

	/**
	 * Draws the bounding boxes around the text.
	 */
	void renderFontmetrics() {
		FTBBox box = fonts[current_font].getBBox(myString);

		// Draw the bounding box
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE); // ONE_MINUS_SRC_ALPHA

		GL11.glColor3f(0.0f, 1.0f, 0.0f);
		// Draw the front face
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(box.lowerX, box.lowerY, box.lowerZ);
		GL11.glVertex3f(box.lowerX, box.upperY, box.lowerZ);
		GL11.glVertex3f(box.upperX, box.upperY, box.lowerZ);
		GL11.glVertex3f(box.upperX, box.lowerY, box.lowerZ);
		GL11.glEnd();
		// Draw the back face
		if (current_font == FTGL_EXTRUDE && box.lowerZ != box.upperZ) {
			GL11.glBegin(GL11.GL_LINE_LOOP);
			GL11.glVertex3f(box.lowerX, box.lowerY, box.upperZ);
			GL11.glVertex3f(box.lowerX, box.upperY, box.upperZ);
			GL11.glVertex3f(box.upperX, box.upperY, box.upperZ);
			GL11.glVertex3f(box.upperX, box.lowerY, box.upperZ);
			GL11.glEnd();
			// Join the faces
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3f(box.lowerX, box.lowerY, box.lowerZ);
			GL11.glVertex3f(box.lowerX, box.lowerY, box.upperZ);

			GL11.glVertex3f(box.lowerX, box.upperY, box.lowerZ);
			GL11.glVertex3f(box.lowerX, box.upperY, box.upperZ);

			GL11.glVertex3f(box.upperX, box.upperY, box.lowerZ);
			GL11.glVertex3f(box.upperX, box.upperY, box.upperZ);

			GL11.glVertex3f(box.upperX, box.lowerY, box.lowerZ);
			GL11.glVertex3f(box.upperX, box.lowerY, box.upperZ);
			GL11.glEnd();
		}

		// Draw the baseline, Ascender and Descender
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3f(0.0f, 0.0f, 1.0f);
		GL11.glVertex3f(0.0f, 0.0f, 0.0f);
		GL11.glVertex3f(fonts[current_font].advance(myString), 0.0f, 0.0f);
		GL11.glVertex3f(0.0f, fonts[current_font].ascender(), 0.0f);
		assert (fonts[current_font].ascender() >= fonts[current_font]
				.descender()) : "Ascender is not greater or equal to descender.";
		GL11.glVertex3f(0.0f, fonts[current_font].descender(), 0.0f);

		GL11.glEnd();

		// Draw the origin
		GL11.glColor3f(1.0f, 0.0f, 0.0f);
		GL11.glPointSize(5.0f);
		GL11.glBegin(GL11.GL_POINTS);
		GL11.glVertex3f(0.0f, 0.0f, 0.0f);
		GL11.glEnd();

		// GL11.glLineWidth(3f);
		// GL11.glBegin(GL.GL_LINES);
		// GL11.glColor3f(1f,0f,0f);
		// final float LENGTH = 28f;
		// GL11.glVertex3f(LENGTH,0f,0f);
		// GL11.glVertex3f(0f,0f,0f);
		// GL11.glColor3f(0f,1f,0f);
		// GL11.glVertex3f(0f,LENGTH,0f);
		// GL11.glVertex3f(0f,0f,0f);
		// GL11.glColor3f(0f,0f,1f);
		// GL11.glVertex3f(0f,0f,LENGTH);
		// GL11.glVertex3f(0f,0f,0f);
		// GL11.glEnd();

		GL11.glLineWidth(1f);
	}

	/**
	 * 
	 */
	void renderFontInfo() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluOrtho2D(0, w_win, 0, h_win);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		// draw mode
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glRasterPos2f(20.0f, h_win - (20.0f + infoFont.ascender()));

		switch (mode) {
		case EDITING:
			infoFont.render("Edit Mode");
			break;
		case INTERACTIVE:
			break;
		}

		// draw font type
		GL11.glRasterPos2i(20, 20);
		switch (current_font) {
		case FTGL_BITMAP:
			infoFont.render("Bitmap Font");
			break;
		case FTGL_PIXMAP:
			infoFont.render("Pixmap Font");
			break;
		case FTGL_OUTLINE:
			infoFont.render("Outline Font");
			break;
		case FTGL_POLYGON:
			infoFont.render("Polygon Font");
			break;
		case FTGL_EXTRUDE:
			infoFont.render("Extruded Font");
			break;
		case FTGL_TEXTURE:
			infoFont.render("Texture Font");
			break;
		}
		GL11.glRasterPos2f(20.0f,
				20.0f + infoFont.ascender() - infoFont.descender());
		infoFont.render(font.getFontName());
	}

	/**
	 * 
	 */
	public void do_display() {
		switch (current_font) {
		case FTGL_BITMAP:
		case FTGL_PIXMAP:
		case FTGL_OUTLINE:
			break;
		case FTGL_POLYGON:
			GL11.glDisable(GL11.GL_BLEND);
			setUpLighting();
			break;
		case FTGL_EXTRUDE:
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_BLEND);
			setUpLighting();
			break;
		case FTGL_TEXTURE:
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			setUpLighting();
			GL11.glNormal3f(0.0f, 0.0f, 1.0f);
			break;

		}

		if (fonts[current_font] != null) {
			FTBBox box = fonts[current_font].getBBox(myString);
			// FTBBox.renderBBox(new Vector3f(0, 0, 0), box);
			GL11.glPushMatrix();
			GL11.glTranslatef(-box.getWidth() / 2.0f, -box.getHeight() / 2.0f,
					0f);

			renderCarat();

			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			// If you do want to switch the color of bitmaps rendered with
			// glBitmap,
			// you will need to explicitly call glRasterPos3f (or its ilk) to
			// lock
			// in a changed current color.
			GL11.glRasterPos3f(0f, 0f, 0f);

			fonts[current_font].render(myString);

			// this.renderFontmetrics();
			GL11.glPopMatrix();
		}
		renderFontInfo();
	}

	private void renderCarat() {
		if (carat >= 0) {
			float caratPos = fonts[current_font].advance(myString.substring(0,
					carat));
			GL11.glPushAttrib(GL11.GL_LIGHTING_BIT | GL11.GL_LINE_BIT
					| GL11.GL_TEXTURE_BIT);
			GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
			GL11.glLineWidth(12f);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3f(caratPos, fonts[current_font].ascender(), 0.0f);
			GL11.glVertex3f(caratPos, fonts[current_font].descender(), 0.0f);
			GL11.glEnd();
			GL11.glPopAttrib();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void display() {
		hasFontChanged();

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		SetCamera();

		FloatBuffer fb = ByteBuffer.allocateDirect(16 * 4)
				.order(ByteOrder.LITTLE_ENDIAN).order(ByteOrder.LITTLE_ENDIAN)
				.asFloatBuffer();

		switch (current_font) {
		case FTGL_BITMAP:
		case FTGL_PIXMAP:
			GL11.glRasterPos2i(w_win / 2, h_win / 2);
			GL11.glTranslatef(w_win / 2f, h_win / 2f, 0.0f);
			break;
		case FTGL_OUTLINE:
		case FTGL_POLYGON:
		case FTGL_EXTRUDE:
		case FTGL_TEXTURE:
			tb.tbMatrix(fb);
			GL11.glMultMatrix(tb.tbMatrix(fb));
			break;
		}

		GL11.glPushMatrix();

		do_display();

		GL11.glPopMatrix();

		FTGLDemo.framesCounted++;
		// glutSwapBuffers();
	}

	void printMatrix(FloatBuffer fb) {
		for (int r = 0; r < 4; r++) {
			System.out.print("[");
			for (int c = 0; c < 4; c++) {
				System.out.print(" ");
				System.out.print(fb.get(c * 4 + r));
			}
			System.out.println("]");
		}
		System.out.println("");
	}

	/**
	 * {@inheritDoc}
	 */
	public void init() {
		setUpFonts(context);
		hasFontChanged();

		System.err.println("GL_VENDOR: " + GL11.glGetString(GL11.GL_VENDOR));
		System.err
				.println("GL_RENDERER: " + GL11.glGetString(GL11.GL_RENDERER));
		System.err.println("GL_VERSION: " + GL11.glGetString(GL11.GL_VERSION));

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0.13f, 0.17f, 0.32f, 0.0f);
		GL11.glColor3f(1.0f, 1.0f, 1.0f);

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glFrontFace(GL11.GL_CCW);

		GL11.glEnable(GL11.GL_DEPTH_TEST);

		GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
		GL11.glPolygonOffset(1.0f, 1.0f); // ????

		SetCamera();

		tb.tbInit(0);

	}

	/**
	 * 
	 * <p>
	 * Note: previously void myinit
	 * 
	 * @param font
	 * @param context
	 * @param drawable
	 */
	public FTGLDemo(Font font, FontRenderContext context) {
		this.font = font;
		this.context = context;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Note: previously void parsekey(char key, int x, int y)
	 * 
	 * @param key
	 */
	public void keyTyped(char keyChar, int key) {
		if (keyChar != Keyboard.CHAR_NONE && key != Keyboard.KEY_BACK)
			if (mode == INTERACTIVE)
				myString = String.valueOf(keyChar);
			else {
				myString = myString.substring(0, carat) + keyChar
						+ myString.substring(carat);
				carat++;
			}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Note: added to allow zooming
	 */
	public void mouseWheelMoved(float amount) {
		if (amount == 0f)
			return;
		amount = amount * 0.004f;
		if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
				amount /= 10;
			zoom += amount;
			if (zoom <= 0)
				zoom = 1f;
			return;
		} else {
			FTGLExtrdFont f = (FTGLExtrdFont) fonts[FTGL_EXTRUDE];

			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
				amount *= 10;
			f.setDepth(f.getDepth() + amount);
			return;
		}
	}

	/**
	 * Processes the keys delivered by the KeyListener,
	 * <p>
	 * Note: keys will be processed in the JOGL's rendering thread.
	 */
	public void processKey(int keyCode) {
		switch (keyCode) {
		case Keyboard.KEY_ESCAPE:
			running = true;
			break;
		case Keyboard.KEY_BACK:
			if (carat > 0) {
				StringBuffer sb = new StringBuffer(myString);
				sb.deleteCharAt(carat - 1);
				myString = sb.toString();
				carat = carat - 1;
			}
			break;
		case Keyboard.KEY_RETURN:
			if (mode == EDITING) {
				mode = INTERACTIVE;
				carat = -1;
				if (myString != null && myString.length() > 0)
					myString = myString.substring(0, 1);
				else
					myString = "a";
			} else {
				carat = Math.max(myString.length() - 1, 0);
				mode = EDITING;
			}
			break;
		case Keyboard.KEY_LEFT:
			carat = Math.max(carat - 1, 0);
			break;
		case Keyboard.KEY_RIGHT:
			carat = Math.min(carat + 1, myString.length());
			break;
		case Keyboard.KEY_PRIOR:
			current_font++;
			if (current_font >= fonts.length)
				current_font = 0;
			break;
		case Keyboard.KEY_NEXT:
			current_font--;
			if (current_font < 0)
				current_font = fonts.length - 1;
			break;
		case Keyboard.KEY_F1:
			myString = "ftgl";
			carat = myString.length();
			break;
		case Keyboard.KEY_F2:
			myString = "Thou shall not steal.";
			carat = myString.length();
			break;
		case Keyboard.KEY_F3:
			myString = "My birthday present from me.";
			carat = myString.length();
			break;
		case Keyboard.KEY_F4:
			myString = "Sorry, I wrecked your car.";
			carat = myString.length();
			break;
		case Keyboard.KEY_F5:
			myString = "Happy wife, happy life.";
			carat = myString.length();
			break;
		case Keyboard.KEY_F6:
			myString = "Call your mom, she worries.";
			carat = myString.length();
			break;
		case Keyboard.KEY_F7:
			myString = "Ding Ding Bling Bling.";
			carat = myString.length();
			break;
		case Keyboard.KEY_F8:
			myString = "\u6FB3\u9580";
			carat = myString.length();
			break;
		default:
			// if (this.key.getKeyChar()==KeyEvent.CHAR_UNDEFINED)
			// System.err.println("Ignored:"+this.key);
			break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void displayChanged(boolean modeChanged, boolean deviceChanged) {
		hasFontChanged();
	}

	/**
	 * {@inheritDoc}
	 */
	public void reshape(int x, int y, int width, int height) {
		System.out.println("RESHAPE");
		hasFontChanged();

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glViewport(0, 0, width, height);
		GL11.glLoadIdentity();

		w_win = width;
		h_win = height;
		SetCamera();

		tb.tbReshape(w_win, h_win);
	}

	void SetCamera() {
		switch (current_font) {
		case FTGL_BITMAP:
		case FTGL_PIXMAP:
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GLU.gluOrtho2D(0, w_win, 0, h_win);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			break;
		case FTGL_OUTLINE:
		case FTGL_POLYGON:
		case FTGL_EXTRUDE:
		case FTGL_TEXTURE:
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GLU.gluPerspective(80, (float) w_win / h_win, 1, 100000);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			GLU.gluLookAt(0f, 0f, h_win / zoom, 0f, 0f, 0f, 0f, 1f, 0f);
			break;
		}
	}

	static boolean closeRequested = false;

	public static void main(String[] args) throws Exception {
		int width = 640;
		int height = 480;

		final JFrame frame = new JFrame("FTGL TEST");
		frame.setBounds(50, 50, width, height);

		String[] allfonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAvailableFontFamilyNames();
		JComboBox<String> box = new JComboBox<String>(allfonts);

		final Canvas drawable = new Canvas();
		final AtomicReference<Dimension> newCanvasSize = new AtomicReference<Dimension>();
		drawable.setPreferredSize(new Dimension(width, height));

		frame.addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				drawable.requestFocusInWindow();
			}
		});

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeRequested = true;
			}
		});

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(drawable, BorderLayout.CENTER);
		frame.getContentPane().add(box, BorderLayout.SOUTH);
		frame.pack();

		final FTGLDemo meshDemo = new FTGLDemo(Font.decode(allfonts[0])
				.deriveFont(172f), // ((Graphics2D)
									// frame.getGraphics()).getFontRenderContext()
				FTFont.STANDARDCONTEXT);

		drawable.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				newCanvasSize.set(drawable.getSize());
			}
		});

		Display.setParent(drawable);
		Display.setVSyncEnabled(true);

		box.addActionListener(meshDemo);
		drawable.requestFocus();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		PixelFormat pf = new PixelFormat(8, 16, 8, 16);
		Display.create(pf);

		meshDemo.reshape(0, 0, width, height);
		meshDemo.init();

		while (true) {
			while (Keyboard.next())
				if (Keyboard.getEventKeyState()) {
					meshDemo.keyTyped(Keyboard.getEventCharacter(),
							Keyboard.getEventKey());
					meshDemo.processKey(Keyboard.getEventKey());
				} else {
				}

			while (Mouse.next()) {
				int dwheel = Mouse.getEventDWheel();
				meshDemo.mouseWheelMoved(dwheel);
				int dx = Mouse.getEventDX();
				int dy = Mouse.getEventDY();
				if (dx != 0 || dy != 0)
					meshDemo.tb.mouseMoved(Mouse.getEventX(),
							Mouse.getEventY(), dx, dy);
				if (Mouse.getEventButton() != -1)
					if (Mouse.getEventButtonState())
						meshDemo.tb.mousePressed(Mouse.getEventButton(),
								Mouse.getX(), Mouse.getY());
					else
						meshDemo.tb.mouseReleased(Mouse.getEventButton(),
								Mouse.getX(), Mouse.getY());
			}

			meshDemo.displayChanged(false, false);
			meshDemo.display();

			Dimension size = newCanvasSize.getAndSet(null);
			if (size != null)
				meshDemo.reshape(0, 0, (int) size.getWidth(),
						(int) size.getHeight());

			Display.update();
			Display.sync(60);

			if (closeRequested)
				break;
		}

		Display.destroy();
		// animator.start();
		System.out.println(meshDemo.toString());
		try {
			int count_alt = 0;
			long time_alt = System.currentTimeMillis();
			while (!Thread.interrupted() && !meshDemo.running) {
				Thread.sleep(5000);

				long time = System.currentTimeMillis();
				int count = FTGLDemo.framesCounted;

				long dt = time - time_alt;
				int dc = count - count_alt;
				count_alt = count;
				System.out.println("" + dc + " frames in " + dt / 1000f
						+ " seconds = " + (1000f * dc / dt) + " FPS at "
						+ drawable.getWidth() + "x" + drawable.getHeight());

				time_alt = time;
			}
		} catch (InterruptedException e) {
			System.out.println("Thread was interrupted.");
		}
		// animator.stop();
		drawable.setEnabled(false);
		// System.exit(0);

		// glutMainLoop();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JComboBox) {
			@SuppressWarnings("unchecked")
			JComboBox<String> b = (JComboBox<String>) e.getSource();
			String selfont = b.getSelectedItem().toString();
			System.out.println(selfont);
			updateFontTo = Font.decode(selfont).deriveFont(72f);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		switch (mode) {
		case EDITING:
			sb.append("\tMode: Edit Mode\n");
			break;
		case INTERACTIVE:
			sb.append("\tMode: Interactive Mode\n");
			break;
		}
		sb.append("\tType: ");
		switch (current_font) {
		case FTGL_BITMAP:
			sb.append("Bitmap Font\n");
			break;
		case FTGL_PIXMAP:
			sb.append("Pixmap Font\n");
			break;
		case FTGL_OUTLINE:
			sb.append("Outline Font\n");
			break;
		case FTGL_POLYGON:
			sb.append("Polygon Font\n");
			break;
		case FTGL_EXTRUDE:
			sb.append("Extruded Font\n");
			break;
		case FTGL_TEXTURE:
			sb.append("Texture Font\n");
			break;
		}

		if (font instanceof OpenType)
			sb.append(" (OpenType) ");

		sb.append("\tFontfile: ");
		sb.append(font.getFontName());
		sb.append("\n");
		return sb.toString();
	}

}