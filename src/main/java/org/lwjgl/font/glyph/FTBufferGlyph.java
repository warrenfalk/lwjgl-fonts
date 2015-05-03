/* $Id: FTBufferGlyph.java,v 1.3 2005/07/27 23:14:31 joda Exp $ */
package org.lwjgl.font.glyph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import org.lwjgl.font.FTGlyphContainer;

/**
 * FTBufferGlyph is a specialisation of FTGlyph for creating pixmaps.
 * 
 * @see FTGlyphContainer
 */
public class FTBufferGlyph extends FTGlyph {

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
	private int destPitch = 0;

	/**
	 * offset from the pen position to the topleft corner of the pixmap
	 */
	private final double offsetX;
	/**
	 * offset from the pen position to the topleft corner of the pixmap
	 */
	private final double offsetY;

	/**
	 * Pointer to the 'image' data
	 */
	private byte[] data = null;

	private byte[] buffer = null;

	/**
	 * Constructor
	 * 
	 * @param glyph
	 *            The Freetype glyph to be processed
	 */
	public FTBufferGlyph(Shape glyph, byte[] clientBuffer, float advance) {
		super(glyph, advance);
		buffer = clientBuffer;

		Rectangle bounds = this.glyph.getBounds();

		destWidth = bounds.width;
		destHeight = bounds.height;

		if (destWidth == 0 || destHeight == 0) {
			offsetX = 0;
			offsetY = 0;
			return;
		}

		// int[] rgbarray = new int[this.destWidth*this.destHeight];

		BufferedImage image = new BufferedImage(bounds.width, bounds.height,
				BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.scale(1.0f, -1.0f);
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, bounds.width, bounds.height);
		g2d.setColor(Color.WHITE);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.translate(-this.glyph.getBounds2D().getX(), -this.glyph
				.getBounds2D().getY() - destHeight + 1);
		g2d.fill(this.glyph);

		destPitch = destWidth;

		if (destWidth > 0 && destHeight > 0) {
			// allows for close to one byte loss at the end of a row
			// Note: each start of a row has to be aligned at byte level
			data = new byte[(int) Math.ceil((destPitch + 7) * destHeight / 8f)];

			// Note: the first bit of each row must start at a new byte
			int pow = 0;
			int posi = 0;
			byte value = 0;
			for (int i = 0; i < destHeight; i++) {
				for (int j = 0; j < destPitch; j++) {
					value |= (byte) (image.getRGB(j, i) & 1);
					pow++;
					if (pow > 7) {
						pow = 0;
						data[posi++] = value;
						value = 0;
					} else
						value = (byte) (value << 1);
				}
				// is the last byte of this row not saved yet ?
				if (pow != 0) {
					// write data & jump to the next free byte
					data[posi++] =
					// move last bits of the row to the byte's highest bits
					(byte) (value << (7 - pow));
					pow = 0;
					value = 0;
				}
			}
			//
			// byte dest = data + (( destHeight - 1) * destPitch);
			//
			// byte src = bitmap.buffer;
			//
			// for( int y = 0; y < srcHeight; ++y)
			// {
			// memcpy( dest, src, srcPitch);
			// dest -= destPitch;
			// src += srcPitch;
			// }
		}

		offsetX = glyph.getBounds2D().getX();
		offsetY = -glyph.getBounds2D().getY();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		super.dispose();
		data = null;
	}

	@Override
	protected void createDisplayList() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float render(final float x, final float y, final float z) {
		return advance;
	}

}