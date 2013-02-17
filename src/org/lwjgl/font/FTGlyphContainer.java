/* $Id: FTGlyphContainer.java,v 1.1 2004/11/12 19:44:23 funsheep Exp $ */
package org.lwjgl.font;

import java.util.HashMap;
import java.util.Iterator;

import org.lwjgl.font.glyph.FTGlyph;



/**
 * FTGlyphContainer holds the post processed FTGlyph objects. It maps a character to a glyphindex.
 * @see FTGlyph
 */
public class FTGlyphContainer
{

	private HashMap<Integer, FTGlyph> cache = new HashMap<Integer, FTGlyph>();

	/**
	 * Current error code. Zero means no error.
	 */
	private int err; // was FT_Error


	/**
	 * Constructor
	 */
	public FTGlyphContainer()
	{
		/* empty */
	}


	/**
	 * Clears the cache from all glyphs.
	 */
	public void clear()
	{
		Iterator<FTGlyph> i = this.cache.values().iterator();
		while (i.hasNext())
		{
			i.next().dispose();
		}
		this.cache.clear();
	}

	/**
	 * Adds a glyph to this glyph list.
	 * @param glyph The FTGlyph to be inserted into the container
	 * @param characterCode The char code of the glyph NOT the glyph index.
	 */
	public void add(FTGlyph glyph, final int characterCode)
	{
		this.cache.put(new Integer(characterCode), glyph);
	}

	/**
	 * Get a glyph from the glyph list
	 * @param characterCode The char code of the glyph NOT the glyph index
	 * @return An FTGlyph or <code>null</code> is it hasn't been loaded.
	 */
	public final FTGlyph glyph(final int characterCode)
	{
		return this.cache.get(new Integer(characterCode));
	}

	/**
	 * Returns an Iterator with which to iterate through all glyphs, cached in this container.
	 * @return An Iterator with which to iterate through all glyphs.
	 */
	public Iterator<FTGlyph> getGlyphs()
	{
		return this.cache.values().iterator();
	}

	/**
	 * Returns the size of the cache.
	 * @return The size of the cache.
	 */
	public int size()
	{
		return this.cache.size();
	}

	/**
	 * Get the bounding box for a character.
	 * @param characterCode The char code of the glyph NOT the glyph index
	 * @return The bounding box for a character.
	 */
	public FTBBox getBBox(final int characterCode)
	{
		return this.glyph(characterCode).getBBox();
	}

	/**
	 * Queries the Font for errors.
	 * @return The current error code.
	 */
	public int error() // was FT_ERROR
	{
		return this.err;
	}

}