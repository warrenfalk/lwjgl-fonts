/* $Id: FTBitmapGlyph.java,v 1.10 2005/07/27 23:14:31 joda Exp $ */
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

import org.lwjgl.font.FTGlyphContainer;
import org.lwjgl.opengl.GL11;

/**
 * FTBitmapGlyph is a specialisation of FTGlyph for creating bitmaps. It
 * provides the interface between Freetype glyphs and their openGL Renderable
 * counterparts. This is an abstract class and derived classes must implement
 * the <code>Render</code> function.
 * 
 * @see FTGlyphContainer
 */
public class FTBitmapGlyph extends FTGlyph {

	/**
	 * The width of the glyph 'image'
	 */
	private int destWidth = 0;

	/**
	 * The height of the glyph 'image'
	 */
	private int destHeight = 0;

	/**
	 * The pitch of the glyph 'image'
	 */
	private int destPitch = 1;

	/**
	 * offset from the pen position to the topleft corner of the pixmap
	 */
	private final float offsetX;

	/**
	 * offset from the pen position to the topleft corner of the pixmap
	 */
	private final float offsetY;

	/**
	 * Pointer to the 'image' data
	 */
	private ByteBuffer data = null;

	/**
	 * Constructor
	 * 
	 * @param glyph
	 *            The Freetype glyph to be processed
	 */
	public FTBitmapGlyph(Shape glyph, float advance) {
		super(glyph, advance);

		Rectangle bounds = this.glyph.getBounds();

		destWidth = bounds.width;
		destPitch = bounds.width;
		destHeight = bounds.height;

		if (destWidth > 0 && destHeight > 0) {
			BufferedImage image = new BufferedImage(bounds.width,
					bounds.height, BufferedImage.TYPE_BYTE_BINARY);
			Graphics2D g2d = (Graphics2D) image.getGraphics();
			// g2d.scale(1.0f, 1.0f);
			// g2d.setColor(Color.BLACK);
			// g2d.fillRect(0, 0, bounds.width, bounds.height);
			g2d.setColor(Color.WHITE);
			// g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			// RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);
			g2d.translate(-bounds.getX(), -bounds.getY());
			g2d.fill(this.glyph);

			Raster imager = image.getRaster();
			SampleModel sm = imager.getSampleModel();

			assert sm.getNumBands() == 1 : "only 1-Band SampleModels supported";
			assert sm.getSampleSize(0) > 0 : "SampleSize should be at least 1 bit";

			int min = 1 << (sm.getSampleSize(0) - 1);

			// Note: each start of a row has to be aligned at byte level
			// Note: the first bit of each row must start at a new byte

			int lineWidth = (destWidth + 7) / 8;

			int[] line1 = new int[lineWidth * 8];
			byte[] line2 = new byte[lineWidth];

			data = ByteBuffer.allocateDirect(lineWidth * destHeight).order(
					ByteOrder.LITTLE_ENDIAN);

			for (int i = 0; i < destHeight; i++) {
				imager.getSamples(0, i, destPitch, 1, 0, line1);

				int posj = 0;
				for (int j = 0; j < lineWidth; j++)
					line2[j] = (byte) ((line1[posj++] < min ? 0 : 128)
							| (line1[posj++] < min ? 0 : 64)
							| (line1[posj++] < min ? 0 : 32)
							| (line1[posj++] < min ? 0 : 16)
							| (line1[posj++] < min ? 0 : 8)
							| (line1[posj++] < min ? 0 : 4)
							| (line1[posj++] < min ? 0 : 2) | (line1[posj++] < min ? 0
							: 1));

				data.put(line2);
			}
		}

		offsetX = (float) this.glyph.getBounds().getX();
		offsetY = (float) -this.glyph.getBounds().getY();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	protected void createDisplayList() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float render(final float x, final float y, final float z) {
		if (data != null) {
			// GL11.glBitmap(0, 0, 0.0f, 0.0f, (float) (x + this.offsetX),
			// (float) (y - this.offsetY), null );

			GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, destPitch);
			data.rewind();
			GL11.glBitmap(destWidth, destHeight, -x + offsetX, y + offsetY,
					0.0f, 0.0f, data);

			// GL11.glBitmap(0, 0, 0.0f, 0.0f, (float) (-x - this.offsetX),
			// (float) (-y + this.offsetY), null );
		}

		return advance;
	}

}