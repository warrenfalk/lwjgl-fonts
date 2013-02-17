/* $Id: OSInfo.java,v 1.1 2004/11/12 19:44:23 funsheep Exp $
 * Created on 02.10.2004
 */
package org.lwjgl.font.util.loader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * TODO javadoc
 * 
 * @author root
 */
public class OSInfo
{
	private final static Map NAME_ALIAS;
	private final static Map ARCH_ALIAS;

	public static final String OS_WIN32 = "win32";
	public static final String OS_MACOS = "macos";
	public static final String OS_MACOSX = "macosx";

	static
	{
		HashMap arch = new HashMap();
		arch.put("ppc", "power pc");
		arch.put("x86", "i\\d86");
		arch.put("x86_64", "amd64");
		arch.put("pa_risk", "pa-risk");

		HashMap name = new HashMap();
		name.put(OS_WIN32, "windows.*");
		name.put(OS_MACOS, "mac os");
		name.put(OS_MACOSX, "mac os x");

		compileValues(name);
		compileValues(arch);

		NAME_ALIAS = Collections.unmodifiableMap(name);
		ARCH_ALIAS = Collections.unmodifiableMap(arch);
	}

	private static void compileValues(Map m)
	{
		Iterator i = m.entrySet().iterator();
		while (i.hasNext())
		{
			Map.Entry e = (Map.Entry)i.next();
			String k = (String)e.getValue();
			e.setValue(Pattern.compile(k));
		}
	}

	private static String alias(Map m, String v)
	{
		v = v.toLowerCase();
		Iterator i = m.entrySet().iterator();
		while (i.hasNext())
		{
			Map.Entry e = (Map.Entry)i.next();
			Pattern p = (Pattern)e.getValue();
			if (p.matcher(v).matches())
			{
				return (String)e.getKey();
			}
		}
		return v;
	}

	private static String getDefaultName()
	{
		return System.getProperty("os.name");
	}

	private static String getDefaultArch()
	{
		return System.getProperty("os.arch");
	}

	private static int getDefaultModel()
	{
		try
		{
			return Integer.parseInt(System.getProperty("sun.arch.data.model", "0"));
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}

	private final String name;
	private final String arch;

	public OSInfo()
	{
		this(getDefaultName(), getDefaultArch(), getDefaultModel());
	}

	public OSInfo(String name, String arch, int datamodel)
	{
		name = alias(NAME_ALIAS, name);
		arch = alias(ARCH_ALIAS, arch);

		if (arch.equals("ppc") && datamodel > 32)
		{
			arch = arch + datamodel;
		}

		this.name = name;
		this.arch = arch;
	}

	public String getName()
	{
		return this.name;
	}

	public String getArch()
	{
		return this.arch;
	}
}