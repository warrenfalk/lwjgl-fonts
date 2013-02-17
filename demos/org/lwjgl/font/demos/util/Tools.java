package org.lwjgl.font.demos.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

import javax.naming.Context;

public class Tools {
	private final static Logger LOGGER = Logger.getLogger();

	public static void close(Socket s) {
		try {
			if (s != null)
				s.close();
		} catch (Exception e) {
			assert LOGGER.log(Level.INFO, "unimportant", e);
		}
	}

	public static void close(ServerSocket s) {
		try {
			if (s != null)
				s.close();
		} catch (Exception e) {
			assert LOGGER.log(Level.INFO, "unimportant", e);
		}
	}

	public static void close(InputStream s) {
		try {
			if (s != null)
				s.close();
		} catch (Exception e) {
			assert LOGGER.log(Level.INFO, "unimportant", e);
		}
	}

	public static void close(OutputStream s) {
		try {
			if (s != null)
				s.close();
		} catch (Exception e) {
			assert LOGGER.log(Level.INFO, "unimportant", e);
		}
	}

	public static void close(Context s) {
		try {
			if (s != null)
				s.close();
		} catch (Exception e) {
			assert LOGGER.log(Level.INFO, "unimportant", e);
		}
	}

	public static String trim(Object s) {
		return (s == null) ? "" : s.toString().trim();
	}

	public static String joinArray(String j, Object[] o) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < o.length; i++) {
			if (i > 0)
				sb.append(j);

			sb.append(o[i]);
		}
		return sb.toString();
	}

	public static void transfer(InputStream i, OutputStream o)
			throws IOException {
		byte[] b = new byte[4096];
		while (true) {
			int l = i.read(b);
			if (l < 0)
				break;

			o.write(b, 0, l);
		}
	}

	public static boolean compare(InputStream i1, InputStream i2)
			throws IOException {
		// TODO performance

		i1 = new BufferedInputStream(i1);
		i2 = new BufferedInputStream(i2);

		while (true) {
			int b1 = i1.read();
			int b2 = i2.read();

			if (b1 < 0 && b2 < 0)
				return true;

			if (b1 != b2)
				return false;
		}
	}

	public static Throwable unmaskInvocationTargetException(Throwable e) {
		if (e instanceof InvocationTargetException) {
			InvocationTargetException e2 = (InvocationTargetException) e;
			return e2.getTargetException();
		}

		return e;
	}

	/**
	 * Returns a classes name without the package.
	 * 
	 * @param c
	 *            the class
	 * @return the name of the class
	 */
	public static String getClassName(Class<?> c) {
		String n = c.getName();
		int i = n.lastIndexOf('.');
		return (i < 0) ? n : n.substring(i + 1);
	}
}