package org.tommybox.ui.utils;

import java.math.BigInteger;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.mozilla.javascript.Context;

public class InvokeUtils {
	private static final JavaCompiler JAVA_COMPILER = ToolProvider.getSystemJavaCompiler();

	/**
	 * 
	 * @param tmpDir
	 * @param urlClassLoader
	 * @param generatedClassesMap
	 * @param javaCode
	 * @throws Throwable
	 */
	public static void runJavaCode(Path tmpDir, URLClassLoader urlClassLoader, Map<String, Class> generatedClassesMap, String javaCode) throws Throwable {
		MessageDigest md         = MessageDigest.getInstance("SHA-256");
		byte[]        hash       = md.digest(javaCode.getBytes(StandardCharsets.UTF_8));
		String        hex        = String.format("%032x", new BigInteger(1, hash));
		String        className  = "C" + hex;
		final String  methodName = "m";

		Class<?> cls = generatedClassesMap.get(className);
		if (cls == null) {
			String sourceContent = "public class " + className + "{public static void " + methodName + "(){" + (javaCode.endsWith(";") ? javaCode : javaCode + ";") + "}}";
			Path   sourceFile    = tmpDir.resolve(className + ".java");
			Files.writeString(sourceFile, sourceContent);
			JAVA_COMPILER.run(null, null, null, sourceFile.toString());
			cls = Class.forName(className, false, urlClassLoader);
			generatedClassesMap.put(className, cls);
		}
		cls.getDeclaredMethod(methodName).invoke(null);
	}

	/**
	 * 
	 * @param jsCode
	 */
	public static void runJsCode(String jsCode) {
		Context cx = Context.enter();
		cx.setLanguageVersion(Context.VERSION_ES6);
		try {
			cx.evaluateString(cx.initStandardObjects(), jsCode, "", 1, null);
		} finally {
			Context.exit();
		}
	}

}
