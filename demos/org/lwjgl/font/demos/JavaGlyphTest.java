/* $Id: JavaGlyphTest.java,v 1.7 2005/07/27 23:14:31 joda Exp $
 * Created on 05.11.2004
 */
package org.lwjgl.font.demos;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Panel;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.lwjgl.font.FTContour;
import org.lwjgl.font.FTVectoriser;

/**
 * Displays a small panel with glyphs. TODO javadoc
 * 
 * @author joda
 */
class GlyphPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private final static double BORDER = 10;
	/**
	 * Allows to adjust the glyphs flatness.
	 */
	public final static double FLATNESS = 0;

	private Font font;
	private GlyphVector glyphs;
	private List<GeneralPath> contours = new LinkedList<GeneralPath>();
	private JLabel status;

	/**
	 * 
	 */
	public GlyphPanel(Font font, String text, JLabel status) {
		super();
		this.font = font;
		this.status = status;
		setBackground(Color.WHITE);
		this.setText(text);
	}

	public void setText(Font font, String text) {
		this.font = font;
		this.setText(text);
	}

	public void setText(String text) {
		glyphs = font.layoutGlyphVector(
				// ((Graphics2D)status.getGraphics()).getFontRenderContext()
				new FontRenderContext(null, true, true), text.toCharArray(), 0,
				text.length(), Font.LAYOUT_LEFT_TO_RIGHT);
		status.setText("Font:" + font.getFontName() + " Font size:"
				+ font.getSize2D() + " Bounds:"
				+ glyphs.getOutline().getBounds2D());
		FTVectoriser vec = new FTVectoriser(glyphs.getOutline(), 0.2f);
		contours.clear();
		for (int i = 0; i < vec.contourCount(); i++)
			contours.add(toShape(vec.contour(i)));
	}

	private static GeneralPath toShape(FTContour contour) {
		GeneralPath p = new GeneralPath();
		double[] help = contour.getPoint(0);
		p.moveTo((float) help[FTContour.X], (float) help[FTContour.Y]);
		for (int i = 1; i < contour.pointCount(); i++) {
			help = contour.getPoint(i);
			p.lineTo((float) help[FTContour.X], (float) help[FTContour.Y]);
		}
		p.closePath();
		return p;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setStroke(new BasicStroke(1f));
		scaleForFont(g2d, font);

		GeneralPath j2d = new GeneralPath();
		j2d.append(
				glyphs.getOutline().getPathIterator(null,
						Double.POSITIVE_INFINITY), false);

		Iterator<GeneralPath> iter = contours.iterator();
		while (iter.hasNext()) {
			Shape ftShape = iter.next();
			paintShape(g2d, ftShape);
		}
		g2d.setColor(new Color(0, 0, 255, 128));
		g2d.draw(j2d);
	}

	void scaleForShape(Graphics2D g2d, Shape s) {
		Rectangle2D rect = s.getBounds2D();
		g2d.translate(BORDER, getHeight() / 2d);
		// double factor =(this.getWidth()-2*BORDER)/rect.getWidth();
		// g2d.scale(factor, factor);
		g2d.translate(-rect.getX(), -rect.getY() - rect.getHeight() / 2d);
	}

	void scaleForFont(Graphics2D g2d, Font f) {
		Rectangle2D rect = f.getMaxCharBounds(g2d.getFontRenderContext());
		g2d.translate(BORDER, getHeight() / 2d);
		// double factor =(this.getWidth()-2*BORDER)/rect.getWidth();
		// g2d.scale(factor, factor);
		g2d.translate(-rect.getX(), -rect.getY() - rect.getHeight() / 2d);
	}

	void paintShape(Graphics2D g2d, Shape s) {
		g2d.setColor(new Color(255, 0, 0, 32));
		g2d.draw(s.getBounds2D());
		Color help = new Color(s.hashCode());
		g2d.setColor(new Color(0, help.getGreen(), 0, 32));
		g2d.fill(s);
		g2d.setColor(Color.BLACK);
		g2d.draw(s);
	}

}

/**
 * TODO javadoc
 * 
 * @author joda
 */
public class JavaGlyphTest extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * @throws java.awt.HeadlessException
	 */
	public JavaGlyphTest(Font font, String text) throws HeadlessException {
		super("Java Glyph Test");
		Container content = getContentPane();
		content.setLayout(new BorderLayout());
		final JLabel status = new JLabel(font.getName());
		final JTextField input = new JTextField(text);
		final GlyphPanel glyph = new GlyphPanel(font, text, status);
		input.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == input) {
					glyph.setText(e.getActionCommand());
					glyph.repaint();
				}
			}
		});
		content.add(status, BorderLayout.SOUTH);
		content.add(glyph, BorderLayout.CENTER);
		content.add(input, BorderLayout.NORTH);
	}

	public static void main(String[] args) {
		Font font = null;

		if (args.length > 1 && args[1] != null)
			font = Font.decode(args[1]);
		else
			font = Font.decode("Times New Roman");
		String text = "aBcD%f";
		if (args.length > 0 && args != null)
			text = args[0];

		JFrame f = new JavaGlyphTest(font.deriveFont(270f), text);
		f.setSize(320, 240);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}