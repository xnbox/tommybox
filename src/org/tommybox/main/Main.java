/*
MIT License

Copyright (c) 2021 xnbox team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

HOME:   https://xnbox.github.io
E-Mail: xnbox.team@outlook.com
*/

package org.tommybox.main;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.SplashScreen;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.catalina.startup.Tomcat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.tommy.common.utils.CommonUtils;
import org.tommy.common.utils.LoggerUtils;
import org.tommy.common.utils.ManifestUtils;
import org.tommy.common.utils.SystemProperties;
import org.tommy.common.utils.ZipUtils;
import org.tommy.main.CustomMain;
import org.tommybox.custom.WebmanifestUpdater;
import org.tommybox.ui.BrowserWindow;
import org.tommybox.ui.utils.InputStreamUtils;
import org.tommybox.ui.utils.OsUtils;
import org.tommybox.ui.utils.OsUtils.Arch;
import org.tommybox.ui.utils.OsUtils.Os;
import org.tommybox.ui.utils.ParseWebManifestUtils;
import org.tommybox.webmanifest.EDisplay;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
The manifest file can have any name, but is commonly named manifest.json and served from the root (your website's top-level directory).
The specification suggests the extension should be .webmanifest, but browsers also support .json extensions, which is may be easier for developers to understand.

<link rel="manifest" href="/manifest.json">

https://web.dev/add-manifest/
 */
public class Main {
	private static final String USER_TMP     = SystemProperties.JAVA_IO_TMPDIR;
	private static final String TOMMYBOX_TMP = USER_TMP + "/.tommybox";

	private static String LOCALHOST_IP = "127.0.0.1"; // used for embedded server (the default)

	private static Class       clazz  = Main.class;
	private static ClassLoader cl     = clazz.getClassLoader();
	private static Logger      logger = LoggerUtils.createLogger(clazz);

	// The range 49152–65535 (215 + 214 to 216 − 1) contains dynamic or private ports that cannot be registered with IANA.
	// This range is used for private or customized services, for temporary purposes, and for automatic allocation of ephemeral ports.

	private static final int HTTP_DEFAULT_PORT  = 80;
	private static final int HTTPS_DEFAULT_PORT = 443;
	private static final int ZERO_PORT          = 0;  // random available port number

	private static final String ARGS_APP_OPTION      = "--app";
	private static final String ARGS_PASSWORD_OPTION = "--password";
	private static final String ARGS_HELP_OPTION     = "--help";

	public static BrowserWindow bw;
	public static final String  CHECK_RUNNING_URL_PATTERN = "/check_running";

	public static void main(String[] args) throws Throwable {
		ManifestUtils.extractBuildDataFromManifest(logger);

		/* parse command line */
		int specialParamCount = 0;

		String jarFileName = args[0];
		specialParamCount += 1;

		String  app      = null;
		char[]  password = null;
		boolean help     = false;
		for (int i = 1; i < args.length; i++) {
			if (args[i].equals(ARGS_APP_OPTION)) {
				app                = args[++i];
				specialParamCount += 2;
			} else if (args[i].equals(ARGS_PASSWORD_OPTION)) {
				password           = args[++i].toCharArray();
				specialParamCount += 2;
			} else if (args[i].equals(ARGS_HELP_OPTION)) {
				help               = true;
				specialParamCount += 1;
			}
		}

		/**
		 * Custom command line args
		 */
		String[] argz = Arrays.copyOfRange(args, specialParamCount, args.length);

		if (app == null)
			if (help) {
				StringBuilder sb = new StringBuilder();
				sb.append("\n");
				sb.append(" 🟩 TommyBox " + System.getProperty("build.version") + '\n');
				sb.append("\n");
				sb.append(" Build: " + System.getProperty("build.timestamp") + '\n');
				sb.append(" OS:    " + SystemProperties.OS_NAME + " (" + SystemProperties.OS_ARCH + ")" + '\n');
				sb.append(" JVM:   " + SystemProperties.JAVA_JAVA_VM_NAME + " (" + SystemProperties.JAVA_JAVA_VERSION + ")\n");
				sb.append("\n");
				sb.append(" Usage:\n");
				sb.append("\n");
				sb.append(" java -jar tb.jar [options] [custom arg1] [custom arg2] ...\n");
				sb.append("\n");
				sb.append(" Options:\n");
				sb.append("  --help                   print help message\n");
				sb.append("  --app <file | dir | URL> run app from ZIP (or WAR) archive, directory or URL\n");
				sb.append("  --password <password>    provide password (for encrypted ZIP (or WAR) archive)\n");
				System.out.println(sb);
				System.exit(0);
			} else
				CustomMain.main(argz);

		/* JAR: META-INF/system.properties - System Properties (optional) */
		try (InputStream is = cl.getResourceAsStream("META-INF/system.properties"); Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
			if (is == null)
				logger.log(Level.WARNING, "\"META-INF/system.properties\" resource (optional) is not found");
			else
				System.getProperties().load(reader);
		} catch (IOException e) { // never throws
			logger.log(Level.SEVERE, "Unknown error", e);
		}

		//		/* JAR: META-INF/env.properties - Environment variables (optional) */
		//		try (InputStream is = cl.getResourceAsStream("META-INF/env.properties"); Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
		//			if (is == null)
		//				logger.log(Level.WARNING, "\"META-INF/env.properties\" resource (optional) is not found");
		//			else {
		//				Properties properties = new Properties();
		//				properties.load(reader);
		//
		//				Map<String, String> env = EnvVarUtils.getModifiableEnvironmentMap();
		//				for (Enumeration<?> enumeration = properties.propertyNames(); enumeration.hasMoreElements();) {
		//					String key   = (String) enumeration.nextElement();
		//					String value = properties.getProperty(key);
		//					try {
		//						env.put(key, value);
		//					} catch (Throwable e) {
		//						logger.log(Level.WARNING, "Env map is not mdiicable");
		//					}
		//				}
		//			}
		//		} catch (IOException e) { // never throws
		//			logger.log(Level.SEVERE, "Unknown error", e);
		//		}

		/* JAR: META-INF/manifest.json - Default PWA manifest */
		byte[] defaultWebmaifestBs = new byte[0];
		try (InputStream is = cl.getResourceAsStream("META-INF/manifest.json"); Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
			if (is != null)
				defaultWebmaifestBs = is.readAllBytes();
		}
		Map<String, Object> defaultWebmaifestMap = ParseWebManifestUtils.parseWebManifestToMap(defaultWebmaifestBs);
		/*
		 * Get port value from server.xml and update it with real port number if port was equals to zero.
		 */

		/* trying get port from "data.properties" file */
		Files.createDirectories(Paths.get(TOMMYBOX_TMP));

		Path dynamicPropertiesFileDir = Paths.get(TOMMYBOX_TMP, jarFileName);
		dynamicPropertiesFileDir.toFile().deleteOnExit();

		Files.createDirectories(dynamicPropertiesFileDir);

		Path         dynamicPropertiesFilePath = dynamicPropertiesFileDir.resolve("data.properties");
		final String PORT_KEY                  = "port";
		Properties   dynamicProperties         = new Properties();
		int          port                      = ZERO_PORT;
		if (Files.exists(dynamicPropertiesFilePath)) {
			try (Reader reader = new FileReader(dynamicPropertiesFilePath.toString(), StandardCharsets.UTF_8)) {
				dynamicProperties.load(reader);
				String portPropertyValue = dynamicProperties.getProperty(PORT_KEY);
				if (portPropertyValue != null) {
					portPropertyValue = portPropertyValue.trim();
					if (!portPropertyValue.isEmpty()) {
						port = Integer.parseInt(portPropertyValue);
						if (port < 1 || port > 65535)
							port = ZERO_PORT;
					}
				}
			}
		}

		/* handle port == 0 (random available port number) */
		if (port == ZERO_PORT)
			try (ServerSocket serverSocket = new ServerSocket(ZERO_PORT)) {
				port = serverSocket.getLocalPort();
				dynamicProperties.setProperty(PORT_KEY, Integer.toString(port));
				dynamicProperties.store(Files.newOutputStream(dynamicPropertiesFilePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING), null);
			} catch (IOException e) {
				e.printStackTrace();
			}

		String portString;
		if (port == HTTP_DEFAULT_PORT || port == HTTPS_DEFAULT_PORT)
			portString = "";
		else
			portString = ':' + Integer.toString(port);

		String origin = "http://" + LOCALHOST_IP + portString;

		File catalinaHomeFile = Files.createTempDirectory("catalina_home-").toFile();
		catalinaHomeFile.deleteOnExit();
		String catalinaHome     = catalinaHomeFile.getAbsolutePath();
		Path   catalinaHomePath = catalinaHomeFile.toPath();
		Path   webappsPath      = catalinaHomePath.resolve("webapps");
		Files.createDirectories(webappsPath);
		Path confPath = catalinaHomePath.resolve("conf");
		Files.createDirectories(confPath);

		Path warPath = CommonUtils.getWarPath(jarFileName, webappsPath, app, password);
		if (warPath == null) {
			logger.log(Level.SEVERE, "App not found.");
			System.exit(0);
		}

		/* extract index */
		byte[] bsIndexHtml = ZipUtils.unzip(warPath, "index.html");
		if (bsIndexHtml == null)
			bsIndexHtml = ZipUtils.unzip(warPath, "index.htm");
		if (bsIndexHtml == null)
			bsIndexHtml = ZipUtils.unzip(warPath, "index.jsp");

		String manifestJsonHref = null;
		if (bsIndexHtml != null) {
			try (InputStream is = new ByteArrayInputStream(bsIndexHtml)) {
				org.jsoup.nodes.Document doc  = Jsoup.parse(is, StandardCharsets.UTF_8.name(), ""); // url
				Element                  link = doc.select("link[rel='manifest']").first();         // E.g.: <link rel="manifest" href="/manifest.json">
				if (link != null)
					manifestJsonHref = link.attr("href");
			}
		}
		if (manifestJsonHref != null) {
			manifestJsonHref = manifestJsonHref.trim();
			if (manifestJsonHref.startsWith("/"))
				manifestJsonHref = manifestJsonHref.substring(1);
		}

		if (manifestJsonHref == null)
			manifestJsonHref = "manifest.json";

		/* extract webmanifest */
		byte[] webmanifestBs = ZipUtils.unzip(warPath, manifestJsonHref);
		if (webmanifestBs == null)
			webmanifestBs = ZipUtils.unzip(warPath, "manifest.webmanifest");
		if (webmanifestBs == null)
			webmanifestBs = ZipUtils.unzip(warPath, "manifest.json");

		Map<String, Object> manifestJsonMap = ParseWebManifestUtils.parseWebManifestToMap(webmanifestBs);
		defaultWebmaifestMap.putAll(manifestJsonMap);

		WebmanifestUpdater.update(app, argz, defaultWebmaifestMap);

		Settings settings = new Settings();
		ParseWebManifestUtils.parseWebManifest(settings, defaultWebmaifestMap);

		/*
		 * context path
		 */
		String contextPath = CommonUtils.getContextPath(settings.scope);

		/* start URL */
		String start_url = getStartUrl(settings);

		String contextUrl = origin + (contextPath.equals("/") ? "" : contextPath) + '/';

		String pageUrl = contextUrl + start_url;

		/* Check if already running */
		boolean running;
		try {
			HttpURLConnection connectionR = (HttpURLConnection) new URL(contextUrl + CHECK_RUNNING_URL_PATTERN).openConnection();
			connectionR.setRequestMethod("POST");
			connectionR.connect();
			connectionR.getResponseCode();
			running = true;
		} catch (Throwable e) {
			running = false;
		}
		if (running) {
			System.exit(0);
			return;
		}

		/*
		 * headless
		 */
		boolean headless = EDisplay.HEADLESS == settings.display;
		System.setProperty("java.awt.headless", Boolean.toString(headless));
		headless = GraphicsEnvironment.isHeadless();

		/*
		 * splashScreen
		 */
		SplashScreen splashScreen = SplashScreen.getSplashScreen();
		if (headless) {
			if (splashScreen != null && splashScreen.isVisible())
				splashScreen.close();
		} else {
			String splashScreenImage;
			if (settings.icons == null)
				splashScreenImage = null;
			else if (settings.icons.isEmpty())
				splashScreenImage = null;
			else
				splashScreenImage = (String) settings.icons.get(settings.icons.size() - 1).get("src");
			if (splashScreenImage != null) {
				splashScreenImage = splashScreenImage.trim();
				int    pos = splashScreenImage.lastIndexOf('/');
				String splashScreenImageFileName;
				if (pos == -1)
					splashScreenImageFileName = splashScreenImage;
				else
					splashScreenImageFileName = splashScreenImage.substring(pos + 1);
				InputStream is = InputStreamUtils.createInputStreamByUrl(contextUrl, splashScreenImage);
				byte[]      bsSplashScreenImage;
				if (is != null)
					bsSplashScreenImage = is.readAllBytes();
				else
					bsSplashScreenImage = ZipUtils.unzip(warPath, splashScreenImageFileName);

				Path targetSplashScreenImage = Paths.get(TOMMYBOX_TMP, splashScreenImageFileName);
				if (bsSplashScreenImage != null) {
					Files.write(targetSplashScreenImage, bsSplashScreenImage);
					targetSplashScreenImage.toFile().deleteOnExit();

					if (splashScreen != null && splashScreen.isVisible())
						try {
							splashScreen.setImageURL(targetSplashScreenImage.toUri().toURL());
						} catch (Throwable e) {
							// ignore
							logger.log(Level.WARNING, "Splash screen image not found: " + splashScreenImage);
						}
				}
			}
		}

		CommonUtils.prepareTomcatConf(confPath, port);

		Tomcat                      tomcat = CommonUtils.prepareTomcat(logger, catalinaHome, app, argz);
		org.apache.catalina.Context ctx    = tomcat.addWebapp(contextPath, warPath.toString());

		final String SERVLET_NAME = "CheckRunningServlet";
		Tomcat.addServlet(ctx, SERVLET_NAME, new HttpServlet() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				Main.bw.show(); // bring window to front
			}
		});
		ctx.addServletMappingDecoded(Main.CHECK_RUNNING_URL_PATTERN, SERVLET_NAME);

		logger.info("SERVER: " + ctx.getServletContext().getServerInfo());
		tomcat.start();

		/* Check if web application has UI part */
		HttpURLConnection connection = (HttpURLConnection) (new URL(pageUrl)).openConnection();
		connection.setRequestMethod("GET");
		int     responseCode = connection.getResponseCode();
		boolean ui           = responseCode == HttpURLConnection.HTTP_OK; // HTTP 200 OK

		//logger.log(Level.CONFIG, "System Properties: " + System.getProperties());
		//logger.log(Level.CONFIG, "Environment variables: " + System.getenv().toString());
		logger.log(Level.CONFIG, "URL: " + pageUrl);
		logger.log(Level.CONFIG, "WAR: " + warPath);
		logger.log(Level.CONFIG, "UI: " + ui);
		logger.log(Level.CONFIG, "Headless: " + headless);

		if (headless || !ui) {
			if (splashScreen != null && splashScreen.isVisible())
				splashScreen.close();
			tomcat.getServer().await();
		} else {
			if (settings.display == EDisplay.BROWSER) {
				if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
					Desktop.getDesktop().browse(new URI(pageUrl));
			} else {
				Path tmpDir = Files.createTempDirectory("java-");
				tmpDir.toFile().deleteOnExit();
				URLClassLoader     urlClassLoader      = URLClassLoader.newInstance(new URL[] { tmpDir.toUri().toURL() }, cl);
				Map<String, Class> generatedClassesMap = new HashMap<>();
				loadSwtJar();
				bw = new BrowserWindow(true, tmpDir, urlClassLoader, generatedClassesMap, splashScreen, settings, contextUrl, pageUrl, pageUrl);
				bw.launch();
			}
		}
	}

	private static void loadSwtJar() throws MalformedURLException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Os   os   = OsUtils.getOs();
		Arch arch = OsUtils.getArch();

		String osStr;
		if (os == Os.WINDOWS)
			osStr = "win32";
		else if (os == Os.MACOSX)
			osStr = "cocoa-macosx";
		else if (os == Os.LINUX)
			osStr = "gtk-linux";
		else
			osStr = "gtk-linux";

		String archStr;
		if (arch == Arch.X64)
			archStr = "x86_64";
		else if (arch == Arch.X86)
			archStr = "win32-x86_64";
		else if (arch == Arch.ARM64)
			archStr = "aarch64";
		else if (arch == Arch.PPC64LE)
			archStr = "ppc64le";
		else
			archStr = "x86_64";

		URL swtJarUrl = new URL("rsrc:org/lib/swt-" + osStr + "-" + archStr + ".jar"); // Jar-in-Jar class loader URL

		Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
		addUrlMethod.setAccessible(true);
		addUrlMethod.invoke(cl, swtJarUrl);
	}

	private static String getStartUrl(Settings settings) {
		String start_url = settings.start_url;
		if (start_url == null)
			start_url = "";
		start_url = start_url.trim();
		if (start_url.equals("."))
			start_url = "/";
		if (start_url.startsWith("/"))
			start_url = start_url.substring(1);
		return start_url;
	}

}