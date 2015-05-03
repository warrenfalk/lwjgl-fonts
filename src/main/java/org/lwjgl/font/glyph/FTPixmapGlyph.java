/* $ Id$
 * Created on 11.11.2004
 */
package org.lwjgl.font.glyph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

/**
 * FTPixmapGlyph is a specialisation of FTGlyph for creating pixmaps.
 * 
 * @see org.lwjgl.font.FTGlyphContainer
 * @author joda
 */
public class FTPixmapGlyph extends FTGlyph {
	/**
	 * The width of the glyph 'image'
	 */
	private int destWidth;

	/**
	 * The height of the glyph 'image'
	 */
	private int destHeight;

	/**
	 * offset from the pen position to the topleft corner of the pixmap
	 */
	private float offsetX;

	/**
	 * offset from the pen position to the topleft corner of the pixmap
	 */
	private float offsetY;

	/**
	 * Pointer to the 'image' data
	 */
	private ByteBuffer data;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createDisplayList() {
		Rectangle bounds = glyph.getBounds();

		// check the pixel mode
		// ft_pixel_mode_grays

		offsetX = (float) bounds.getX();
		offsetY = (float) bounds.getY();
		int srcWidth = bounds.width;
		int srcHeight = bounds.height;

		// FIXME What about dest alignment?
		destWidth = srcWidth;
		destHeight = srcHeight;

		if (destWidth > 0 && destHeight > 0) {
			BufferedImage image = new BufferedImage(bounds.width,
					bounds.height, BufferedImage.TYPE_BYTE_GRAY);
			Graphics2D g2d = (Graphics2D) image.getGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setColor(Color.WHITE);
			g2d.translate(-bounds.getX(), -bounds.getY());
			g2d.fill(glyph);

			data = ByteBuffer.allocateDirect(destWidth * destHeight * 4).order(
					ByteOrder.LITTLE_ENDIAN);
			data.order(ByteOrder.BIG_ENDIAN);

			int[] line = new int[destWidth];

			// Get the current glColor.
			float[] ftglColour = new float[4];
			FloatBuffer fb = ByteBuffer.allocateDirect(16 * 4)
					.order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
			GL11.glGetFloat(GL11.GL_CURRENT_COLOR, fb);
			fb.get(ftglColour);

			byte redComponent = (byte) (ftglColour[0] * 255.0f + 0.5f);
			byte greenComponent = (byte) (ftglColour[1] * 255.0f + 0.5f);
			byte blueComponent = (byte) (ftglColour[2] * 255.0f + 0.5f);
			float alpha = ftglColour[3];

			int colorComponent = ((redComponent & 0xFF) << 24)
					| ((greenComponent & 0xFF) << 16)
					| ((blueComponent & 0xFF) << 8);

			Raster imager = image.getRaster();
			SampleModel sm = imager.getSampleModel();

			assert sm.getNumBands() == 1 : "only 1-Band SampleModels supported";
			assert sm.getSampleSize(0) == 8 : "SampleSize should be 8 bit";

			// unsigned char* src = bitmap.buffer;

			if (ftglColour[3] == 1.0f)
				for (int y = 0; y < destHeight; y++) {
					imager.getSamples(0, y, destWidth, 1, 0, line);
					for (int x = 0; x < destWidth; x++)
						data.putInt(colorComponent | line[x]);
				}
			else
				for (int y = 0; y < destHeight; y++) {
					imager.getSamples(0, y, destWidth, 1, 0, line);
					for (int x = 0; x < destWidth; x++) {
						data.put(redComponent);
						data.put(greenComponent);
						data.put(blueComponent);
						data.put((byte) (alpha * line[x] + 0.5f));
					}
				}
			data.flip();
		}
	}

	/**
	 * Constructor
	 * 
	 * @param glyph
	 *            The Freetype glyph to be processed
	 */
	public FTPixmapGlyph(Shape glyph) {
		super(glyph);
		destWidth = 0;
		destHeight = 0;
		data = null;
	}

	/**
	 * {@inheritDoc} Destructor
	 */
	@Override
	public void dispose() {
		if (data != null)
			data = null;
		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float render(final float x, final float y, final float z) {
		if (data != null) {
			// Move the glyph origin
			GL11.glBitmap(0, 0, 0.0f, 0.0f, x + offsetX, y + offsetY, data); // TODO:
																				// used
																				// to
																				// pass
																				// null,
																				// but
																				// now
																				// passing
																				// this.data
																				// --
																				// not
																				// sure
																				// if
																				// correct

			GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
			data.rewind();
			GL11.glDrawPixels(destWidth, destHeight, GL11.GL_RGBA,
					GL11.GL_UNSIGNED_BYTE, data);

			// Restore the glyph origin
			GL11.glBitmap(0, 0, 0.0f, 0.0f, -x - offsetX, -y - offsetY, data); // TODO:
																				// used
																				// to
																				// pass
																				// null,
																				// but
																				// now
																				// passing
																				// this.data
																				// --
																				// not
																				// sure
																				// if
																				// correct
		}

		return advance;
	}

}