// $Id: Logger.java,v 1.1 2004/11/12 19:44:23 funsheep Exp $
// Created on 20.07.2004
package org.lwjgl.font.demos.util;

import java.util.logging.Handler;
import java.util.logging.Level;

/**
 * @author Sven
 */
public class Logger
{
	private static final String CLASSNAME = Logger.class.getName();

	private final java.util.logging.Logger logger;

	public Logger(java.util.logging.Logger l)
	{
		this.logger = l;
	}

	public Handler[] getHandlers()
	{
		return this.logger.getHandlers();
	}

	public boolean finest(String s)
	{
		this.logger.finest(s);
		return true;
	}

	public boolean finer(String s)
	{
		this.logger.finer(s);
		return true;
	}

	public boolean fine(String s)
	{
		this.logger.fine(s);
		return true;
	}

	public boolean info(String s)
	{
		this.logger.info(s);
		return true;
	}

	public boolean warning(String s)
	{
		this.logger.warning(s);
		return true;
	}

	public boolean severe(String s)
	{
		this.logger.severe(s);
		return true;
	}

	//TODO many more methods

	public boolean log(String msg)
	{
		this.logger.log(Level.INFO, msg);
		return true;
	}

	public boolean log(Level l, String msg)
	{
		this.logger.log(l, msg);
		return true;
	}

	public boolean log(Level l, String msg, Object o)
	{
		this.logger.log(l, msg, o);
		return true;
	}

	public boolean log(Level l, String msg, Object[] o)
	{
		this.logger.log(l, msg, o);
		return true;
	}

	public boolean log(Level l, String msg, Throwable t)
	{
		this.logger.log(l, msg, t);
		return true;
	}

	private static String getClassName()
	{
		Exception e = new Exception();
		StackTraceElement[] st = e.getStackTrace();
		for (int i = 0; i < st.length; i++)
		{
			String n = st[i].getClassName();

			if (!CLASSNAME.equals(n))
			{
				return n;
			}
		}
		throw new RuntimeException("the roof is on fire");
	}

	private static String getPackageName()
	{
		String cn = getClassName();
		int i = cn.lastIndexOf(".");
		return i < 0 ? cn : cn.substring(0, i);
	}

	public static Logger getLogger()
	{
		return new Logger(java.util.logging.Logger.getLogger(getClassName()));
	}

	public static Logger getLogger(String bundlename)
	{
		return new Logger(java.util.logging.Logger.getLogger(getClassName(), bundlename));
	}

	public static Logger getPackageLogger()
	{
		return new Logger(java.util.logging.Logger.getLogger(getPackageName()));
	}

	public static Logger getPackageLogger(String bundlename)
	{
		return new Logger(java.util.logging.Logger.getLogger(getPackageName(), bundlename));
	}
}