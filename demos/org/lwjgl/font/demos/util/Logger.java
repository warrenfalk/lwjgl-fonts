// $Id: Logger.java,v 1.1 2004/11/12 19:44:23 funsheep Exp $
// Created on 20.07.2004
package org.lwjgl.font.demos.util;

import java.util.logging.Handler;
import java.util.logging.Level;

/**
 * @author Sven
 */
public class Logger {
	private static final String CLASSNAME = Logger.class.getName();

	private final java.util.logging.Logger logger;

	public Logger(java.util.logging.Logger l) {
		logger = l;
	}

	public Handler[] getHandlers() {
		return logger.getHandlers();
	}

	public boolean finest(String s) {
		logger.finest(s);
		return true;
	}

	public boolean finer(String s) {
		logger.finer(s);
		return true;
	}

	public boolean fine(String s) {
		logger.fine(s);
		return true;
	}

	public boolean info(String s) {
		logger.info(s);
		return true;
	}

	public boolean warning(String s) {
		logger.warning(s);
		return true;
	}

	public boolean severe(String s) {
		logger.severe(s);
		return true;
	}

	// TODO many more methods

	public boolean log(String msg) {
		logger.log(Level.INFO, msg);
		return true;
	}

	public boolean log(Level l, String msg) {
		logger.log(l, msg);
		return true;
	}

	public boolean log(Level l, String msg, Object o) {
		logger.log(l, msg, o);
		return true;
	}

	public boolean log(Level l, String msg, Object[] o) {
		logger.log(l, msg, o);
		return true;
	}

	public boolean log(Level l, String msg, Throwable t) {
		logger.log(l, msg, t);
		return true;
	}

	private static String getClassName() {
		Exception e = new Exception();
		StackTraceElement[] st = e.getStackTrace();
		for (StackTraceElement element : st) {
			String n = element.getClassName();

			if (!CLASSNAME.equals(n))
				return n;
		}
		throw new RuntimeException("the roof is on fire");
	}

	private static String getPackageName() {
		String cn = getClassName();
		int i = cn.lastIndexOf(".");
		return i < 0 ? cn : cn.substring(0, i);
	}

	public static Logger getLogger() {
		return new Logger(java.util.logging.Logger.getLogger(getClassName()));
	}

	public static Logger getLogger(String bundlename) {
		return new Logger(java.util.logging.Logger.getLogger(getClassName(),
				bundlename));
	}

	public static Logger getPackageLogger() {
		return new Logger(java.util.logging.Logger.getLogger(getPackageName()));
	}

	public static Logger getPackageLogger(String bundlename) {
		return new Logger(java.util.logging.Logger.getLogger(getPackageName(),
				bundlename));
	}
}