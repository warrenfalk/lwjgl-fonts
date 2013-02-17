/* $Id: LibraryLoader.java,v 1.1 2004/11/12 19:44:23 funsheep Exp $
 * Created on 02.10.2004
 */
package org.lwjgl.font.util.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.logging.Level;

import org.lwjgl.font.demos.util.Logger;
import org.lwjgl.font.demos.util.Tools;


/**
 * TODO javadoc
 * 
 * @author root
 */
public class LibraryLoader
{
	private static final Logger LOGGER = Logger.getLogger();
	private static final ClassLoader CLASSLOADER = LibraryLoader.class.getClassLoader();

	private final static String PREFIX = "ftgl_";

	private final static HashSet cache = new HashSet();

	private final OSInfo osinfo;

	private static synchronized void load0(String libname, String path, boolean windows)
	{
		if (cache.contains(path))
			return;

		URL u = CLASSLOADER.getResource(path);
		if (u == null)
			throw new RuntimeException("file " + path + " not found");

		assert LOGGER.log(Level.INFO, "library " + libname + " found at " + u);

		File p;

		if (u.getProtocol().equalsIgnoreCase("file"))
		{
			p = new File(u.getPath());
		}
		else
		{
			try
			{
				if (windows)
				{
					assert LOGGER.log(Level.INFO, "windows detected");
					p = extractLibraryWindows(libname, u);
				}
				else
				{
					assert LOGGER.log(Level.INFO, "no windows detected");
					p = extractLibraryOther(libname, u);
				}
			}
			catch (IOException e)
			{
				throw new RuntimeException("could not extract library", e);
			}
		}

		System.load(p.getAbsolutePath());

		//TODO make better somehow
		p.delete();

		cache.add(path);
	}

	public LibraryLoader()
	{
		this.osinfo = new OSInfo();
	}

	public void load(String libname)
	{
		libname = System.mapLibraryName(libname);

		String osname = this.osinfo.getName();
		String osarch = this.osinfo.getArch();
		String path = "natives/" + osname + "/" + osarch + "/" + libname;

		load0(libname, path, osname.equals(OSInfo.OS_WIN32));
	}

	private static File extractLibraryWindows(String libname, URL u) throws IOException
	{
		String tmpdir = System.getProperty("java.io.tmpdir", "");
		if (tmpdir.length() < 1)
		{
			throw new RuntimeException("no temp dir");
		}

		assert LOGGER.log(Level.INFO, "using tempdir " + tmpdir);

		String prefix = tmpdir + File.separator + PREFIX;

		int c = 0;
		while (true)
		{
			c++;
			File f = new File(prefix + c + libname);

			f.delete();

			//TODO does this throw excepotion if temp-dir is not writable?
			if (f.createNewFile())
			{
				assert LOGGER.log(Level.INFO, "created " + f);

				InputStream i = null;
				OutputStream o = null;

				try
				{
					i = u.openStream();
					o = new FileOutputStream(f);

					Tools.transfer(i, o);

					return f;
				}
				finally
				{
					Tools.close(i);
					Tools.close(o);
				}
			}

			if (f.isFile())
			{
				assert LOGGER.log(Level.INFO, "testing " + f);

				InputStream i1 = null;
				InputStream i2 = null;
				try
				{
					i1 = u.openStream();
					i2 = new FileInputStream(f);

					if (Tools.compare(i1, i2))
					{
						assert LOGGER.log(Level.INFO, f + " is OK");

						return f;
					}
				}
				catch (IOException e)
				{
					assert LOGGER.log(Level.INFO, "i/o-error", e);
				}
				finally
				{
					Tools.close(i1);
					Tools.close(i2);
				}
			}
		}
	}

	private static File extractLibraryOther(String libname, URL u) throws IOException
	{
		File f = File.createTempFile(PREFIX, libname);

		assert LOGGER.log(Level.INFO, "copying library to " + libname);

		InputStream i = null;
		OutputStream o = null;
		try
		{
			i = u.openStream();
			o = new FileOutputStream(f);
			Tools.transfer(i, o);
		}
		finally
		{
			Tools.close(i);
			Tools.close(o);
		}

		return f;
	}

}