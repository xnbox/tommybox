/*
MIT Licrense

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

package org.tommybox.ui;

import java.awt.Desktop;
import java.awt.SplashScreen;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.tommy.common.utils.LoggerUtils;
import org.tommybox.main.Settings;
import org.tommybox.ui.utils.GuiUtils;
import org.tommybox.ui.utils.InputStreamUtils;
import org.tommybox.ui.utils.InvokeUtils;
import org.tommybox.webmanifest.EDisplay;
import org.tommybox.webmanifest.EWindowButton;
import org.tommybox.webmanifest.EWindowMenu;

/*
 * TODO
 * - BACK and Forvard keys Alt+Left arrow, Alt+Right arrow
 * - mailto: BUG !!!!!!!!!!!!!!!!
 * - default res icon should be transparent 16x16 with black border 15x15 and white border 14x14
 * - get favicon from / as browsers do
 */
public final class BrowserWindow {

	private static final String SWT_DATA_MENU_ITEM_KEY = "tommybox.url";

	private static final String HTTP_HEADER_CACHE_CONTROL = "Cache-Control: no-cache, no-store, max-age=0, must-revalidate";
	private static final String HTTP_HEADER_PRAGMA        = "Pragma: no-cache";
	private static final String HTTP_HEADER_EXPIRES       = "Expires: Fri, 01 Jan 1990 00:00:00 GMT";

	private static final String DEFAULT_WINDOW_ICON = "org/tommybox/ui/res/no_tray_icon.png";

	private static Logger logger = LoggerUtils.createLogger(BrowserWindow.class);
	private Display       display;

	private String          contextUrl;
	private String          homeUrl;
	private volatile String currentUrl;

	private Settings settings;

	private Shell shell;

	private Browser browser;
	private Menu    browserMenu;

	private Tray     tray;
	private TrayItem trayItem;
	private Menu     trayMenu;

	private boolean firstWindow;
	private boolean firstPage = true;

	private static int   countOfOpenWindows;
	private SplashScreen splashScreen;

	private Path               tmpDir;
	private URLClassLoader     urlClassLoader;
	private Map<String, Class> generatedClassesMap;

	private static Color backgroundColor;

	private String init_title;

	private Image smallImage;

	/**
	 * BrowserWindow constructor
	 *
	 * @param firstWindow
	 * @param tmpDir
	 * @param urlClassLoader
	 * @param generatedClassesMap
	 * @param splashScreen
	 * @param rb
	 * @param settings
	 * @param homeUrl
	 * @param currentUrl
	 * @throws InterruptedException 
	 */
	public BrowserWindow(boolean firstWindow, Path tmpDir, URLClassLoader urlClassLoader, Map<String, Class> generatedClassesMap, SplashScreen splashScreen, Settings settings, String contextUrl, String homeUrl, String currentUrl) {
		this.firstWindow         = firstWindow;
		this.tmpDir              = tmpDir;
		this.urlClassLoader      = urlClassLoader;
		this.generatedClassesMap = generatedClassesMap;
		this.splashScreen        = splashScreen;
		this.settings            = settings;
		this.contextUrl          = contextUrl;
		this.homeUrl             = homeUrl;
		this.currentUrl          = currentUrl;
		this.display             = Display.getDefault();

		this.init_title = translate(getTitle());
		this.smallImage = getSmallImage(display, contextUrl, settings.icons);
		if (this.smallImage == null)
			// fallback to default icon
			try (InputStream is = BrowserWindow.class.getClassLoader().getResourceAsStream(DEFAULT_WINDOW_ICON)) {
				if (is != null)
					this.smallImage = new Image(display, is);
			} catch (IOException e) {
				// ignore
			}

		if (settings.background_color != null) {
			String bg = settings.background_color.trim();
			if (bg.startsWith("#")) // HTML hex color notation. E.g.: #FF0000
				backgroundColor = GuiUtils.rgbaToSwtColorFromHtmlHex(bg);
			else if (bg.startsWith("rgb(") || bg.startsWith("rgb(")) { // color CSS expression. E.g.: rgb(256, 0, 0)
				String[] backgroundColorStrArr = settings.background_color.trim().replace("rgb(", "").replace("rgba(", "").replace(")", "").trim().split(",");
				int[]    backgroundColorArr    = new int[backgroundColorStrArr.length];
				for (int i = 0; i < backgroundColorStrArr.length; i++)
					backgroundColorArr[i] = Integer.parseInt(backgroundColorStrArr[i].trim());
				backgroundColor = GuiUtils.rgbaToSwtColorFromIntArray(backgroundColorArr);
			} else // color names. E.g. red				
				backgroundColor = GuiUtils.rgbaToSwtColorFromHtmlColorName(bg);
		}

	}

	/**
	 * Window Launcher
	 */
	public void launch() {

		int shellStyle = SWT.NONE;

		if (settings.windowAlwaysOnTop)
			shellStyle |= SWT.ON_TOP;

		for (EWindowButton button : settings.windowButtonsList) {
			if (EWindowButton.CLOSE == button)
				shellStyle |= SWT.CLOSE;
			else if (EWindowButton.MINIMIZE == button)
				shellStyle |= SWT.MIN;
			else if (EWindowButton.MAXIMIZE == button) {
				shellStyle |= SWT.MAX;
				shellStyle |= SWT.RESIZE;
			}
		}

		if (firstWindow)
			//@formatter:off
			if (
				EDisplay.FULLSCREEN       == settings.display ||
				EDisplay.DESKTOP_AREA     == settings.display ||
				EDisplay.FRAMELESS_WINDOW == settings.display
			)
			//@formatter:on
				shellStyle |= SWT.NO_TRIM;

		shell = new Shell(display, shellStyle);
		shell.setBackgroundMode(SWT.INHERIT_FORCE);
		if (backgroundColor != null)
			shell.setBackground(backgroundColor);

		shell.setText(init_title);
		if (smallImage != null)
			shell.setImage(smallImage);
		if (firstWindow)
			if (settings.trayIcon)
				tray = display.getSystemTray();

		if (tray != null)
			trayItem = new TrayItem(tray, SWT.NONE);

		if (trayItem != null) {
			if (smallImage != null)
				trayItem.setImage(smallImage);
			if (!init_title.isEmpty())
				trayItem.setToolTipText(init_title);
		}
		if (trayItem != null)
			trayItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					toggleShowHide();
				}
			});

		if (trayItem != null) {
			trayMenu = createMenu(shell);
			trayItem.addListener(SWT.MenuDetect, new Listener() {
				public void handleEvent(Event event) {
					if (trayMenu != null)
						trayMenu.setVisible(true);
				}
			});
		}

		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth  = 0;
		shell.setLayout(gridLayout);

		browser = new Browser(shell, SWT.NONE);
		browser.setBackgroundMode(SWT.INHERIT_FORCE);
		if (backgroundColor != null)
			browser.setBackground(backgroundColor);

		if (EWindowMenu.CUSTOM == settings.windowMenu) {
			browserMenu = createMenu(browser);
			if (browserMenu == null)
				browser.setMenu(new Menu(browser));
			else
				browser.setMenu(browserMenu);
		} else if (EWindowMenu.NONE == settings.windowMenu)
			browser.setMenu(new Menu(browser));

		browser.addLocationListener(new LocationListener() {

			@Override
			public void changing(LocationEvent locationEvent) {
				String location = locationEvent.location;
				if ( //
				location.startsWith("mailto:") || //
				location.startsWith("tel:") //
				)
					Program.launch(location);
				else if (location.startsWith("quit:")) {
					locationEvent.doit = false;
					display.asyncExec(new Runnable() {

						@Override
						public void run() {
							System.exit(0);
						}
					});
				} else if (location.startsWith("minimize:")) {
					locationEvent.doit = false;
					toggleShowHide();
				} else if (location.startsWith("fullscreen:")) {
					locationEvent.doit = false;
					toggleFullscreen();
				} else if (location.startsWith("open:")) {
					locationEvent.doit = false;
					int    pos = location.indexOf(':');
					String rel = location.substring(pos + 1).trim();
					String href;
					if (rel.startsWith("https:") || rel.startsWith("http:") || rel.startsWith("file:"))
						href = rel;
					else
						href = homeUrl + rel;
					display.asyncExec(new Runnable() {

						@Override
						public void run() {
							Program.launch(href);
						}
					});
				} else if (location.startsWith("open_in_browser:")) {
					locationEvent.doit = false;
					int    pos = location.indexOf(':');
					String rel = location.substring(pos + 1).trim();
					String href;
					if (rel.startsWith("https:") || rel.startsWith("http:") || rel.startsWith("file:"))
						href = rel;
					else
						href = homeUrl + rel;
					display.asyncExec(new Runnable() {

						@Override
						public void run() {
							try {
								if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
									Desktop.getDesktop().browse(new URI(href));
							} catch (IOException | URISyntaxException e) {
								logger.severe(e.getMessage());
								e.printStackTrace();
							}
						}
					});
				} else if (location.startsWith("java:")) {
					locationEvent.doit = false;
					int    pos      = location.indexOf(':');
					String javaCode = location.substring(pos + 1).trim();
					display.asyncExec(new Runnable() {

						@Override
						public void run() {
							try {
								InvokeUtils.runJavaCode(tmpDir, urlClassLoader, generatedClassesMap, javaCode);
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					});
				} else if (location.startsWith("js:")) {
					locationEvent.doit = false;
					int    pos    = location.indexOf(':');
					String jsCode = location.substring(pos + 1).trim();
					display.asyncExec(new Runnable() {

						@Override
						public void run() {
							try {
								InvokeUtils.runJsCode(jsCode);
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					});
				}
			}

			@Override
			public void changed(LocationEvent locationEvent) {
				currentUrl = locationEvent.location;
			}

		});

		browser.addProgressListener(new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				browser.removeProgressListener(this);
				browser.refresh(); // clear cache
			}
		});

		browser.addProgressListener(new ProgressAdapter() {

			@Override
			public void completed(ProgressEvent event) {
				if (firstPage) {
					firstPage = false;

					String faviconUrl = browser.evaluate("function f(){el=document.querySelector(\"link[rel*='icon']\"); if (el == null) return ''; return el.href;}; return f();").toString();
					logger.info("favicon: " + faviconUrl);

					if (faviconUrl == null)
						faviconUrl = "";
					faviconUrl = faviconUrl.trim();
					if (faviconUrl.isEmpty())
						faviconUrl = contextUrl + "favicon.ico";

					Image favicon = createIconImageByUrl(display, contextUrl, faviconUrl);

					if (EDisplay.STANDALONE == settings.display || EDisplay.MINIMIZED_WINDOW == settings.display || EDisplay.FRAMELESS_WINDOW == settings.display) {
						int frameX = shell.getSize().x - shell.getClientArea().width;
						int frameY = shell.getSize().y - shell.getClientArea().height;

						int width;
						if (settings.windowWidth == null)
							width = shell.getClientArea().width;
						else
							width = frameX + settings.windowWidth;

						int height;
						if (settings.windowHeight == null)
							height = shell.getClientArea().height;
						else
							height = frameY + settings.windowHeight;

						shell.setSize(width, height);

						if (firstWindow) {
							Rectangle screenSize = display.getPrimaryMonitor().getBounds();

							int x;
							if (settings.windowX == null)
								x = (screenSize.width - width) / 2;
							else
								x = settings.windowX;

							int y;
							if (settings.windowY == null)
								y = (screenSize.height - height) / 2;
							else
								y = settings.windowY;

							shell.setLocation(x, y);
						}
					}

					if (favicon != null) {
						shell.setImage(favicon);
						if (trayItem != null)
							trayItem.setImage(favicon);
					}
				}
				urlUpdated();

				shell.open();

				if (firstWindow) {
					if (EDisplay.FULLSCREEN == settings.display)
						toggleFullscreen();
					else if (EDisplay.MINIMIZED_WINDOW == settings.display) {
						if (settings.trayIcon)
							shell.setVisible(!shell.isVisible());
						else
							shell.setMinimized(true);
					}
				}

				if (firstWindow)
					display.asyncExec(new Runnable() {

						@Override
						public void run() {
							if (splashScreen != null && splashScreen.isVisible())
								splashScreen.close();
						}
					});
			}
		});

		browser.addTitleListener(new TitleListener() {
			@Override
			public void changed(TitleEvent event) {
				String title = event.title;
				if (title == null)
					title = "";
				title = title.trim();
				title = translate(title);
				if (!title.isEmpty())
					shell.setText(title);
				if (trayItem != null)
					if (!title.isEmpty())
						trayItem.setToolTipText(title);
			}
		});

		browser.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 'q'))
					System.exit(0);
			}
		});

		browser.addListener(SWT.KeyDown, new Listener() {

			@Override
			public void handleEvent(Event e) {
				if (settings.enableFullScreen && GuiUtils.isFullScreenSupported())
					if (e.keyCode == SWT.F11) {
						toggleFullscreen();
						return;
					}
				if (((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 'r'))
					browser.refresh();
				else if (e.keyCode == SWT.F5)
					browser.refresh();
			}
		});

		shell.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {
				shell.requestLayout();
			}

		});

		shell.addShellListener(new ShellAdapter() {

			@Override
			public void shellIconified(ShellEvent e) {
				e.doit = false;
			}

			@Override
			public void shellDeiconified(ShellEvent e) {
				e.doit = false;
			}

			@Override
			public void shellClosed(ShellEvent e) {
				e.doit = false;
				if (firstWindow) {
					if (settings.trayIcon) {
						shell.setVisible(false);
						return;
					}
				}
				display.asyncExec(new Runnable() {

					@Override
					public void run() {
						if (countOfOpenWindows == 1)
							System.exit(0);
						else {
							browser.dispose();
							shell.dispose();
							countOfOpenWindows--;
						}
					}
				});
			}

		});
		browser.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		browser.setUrl(currentUrl, null, new String[] { HTTP_HEADER_CACHE_CONTROL, HTTP_HEADER_PRAGMA, HTTP_HEADER_EXPIRES });

		if (firstWindow)
			if (EDisplay.DESKTOP_AREA == settings.display || EDisplay.MAXIMIZED_WINDOW == settings.display)
				shell.setMaximized(true);

		countOfOpenWindows++;

		if (!firstWindow)
			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					if (splashScreen != null && splashScreen.isVisible())
						splashScreen.close();
				}
			});

		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
	}

	/**
	 * 
	 */
	public void show() {
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				if (firstWindow)
					shell.setVisible(true);
				if (shell.getMinimized())
					shell.setMinimized(false);
			}
		});
	}

	/**
	 * 
	 * @param display
	 * @param url
	 * @return
	 */
	private static Image createIconImageByUrl(Display display, String contextUrl, String url) {
		try (InputStream is = InputStreamUtils.createInputStreamByUrl(contextUrl, url)) {
			if (is != null) {
				Image     image     = new Image(display, is);
				ImageData imageData = image.getImageData();
				if (imageData.width > 16 || imageData.height > 16) { // fix icon size to 16x16
					imageData = imageData.scaledTo(16, 16);
					image.dispose();
					image = new Image(display, imageData);
				}
				return image;
			}
		} catch (Throwable e) {
			// image can be invalid, so ignore all exceptions
		}
		return null;
	}

	/**
	 * 
	 */
	private void toggleShowHide() {
		if (firstWindow)
			if (settings.trayIcon)
				shell.setVisible(!shell.isVisible());
		if (shell.getMinimized())
			shell.setMinimized(false);
		boolean fullScreen = shell.getFullScreen();
		if (fullScreen)
			toggleFullscreen();
	}

	/**
	 * 
	 */
	private void urlUpdated() {
		if (trayMenu != null)
			for (MenuItem menuItem : trayMenu.getItems()) {
				String shortcutUrl = (String) menuItem.getData(SWT_DATA_MENU_ITEM_KEY);
				if ("home:".equals(shortcutUrl))
					menuItem.setEnabled(!homeUrl.equals(currentUrl));
				else if ("forward:".equals(shortcutUrl))
					menuItem.setEnabled(browser.isForwardEnabled());
				else if ("back:".equals(shortcutUrl))
					menuItem.setEnabled(browser.isBackEnabled());
			}
		if (browserMenu != null) {
			for (MenuItem menuItem : browserMenu.getItems()) {
				String shortcutUrl = (String) menuItem.getData(SWT_DATA_MENU_ITEM_KEY);
				if ("home:".equals(shortcutUrl))
					menuItem.setEnabled(!homeUrl.equals(currentUrl));
				else if ("forward:".equals(shortcutUrl))
					menuItem.setEnabled(browser.isForwardEnabled());
				else if ("back:".equals(shortcutUrl))
					menuItem.setEnabled(browser.isBackEnabled());
			}
		}
	}

	/**
	 * 
	 */
	private void toggleFullscreen() {
		boolean fullScreen = shell.getFullScreen();

		if (!fullScreen) {
			if (firstWindow)
				if (settings.trayIcon)
					if (!shell.isVisible())
						shell.setVisible(true);
			if (shell.getMinimized())
				shell.setMinimized(false);
		}
		shell.setFullScreen(!fullScreen);
		if (trayMenu != null)
			for (MenuItem menuItem : trayMenu.getItems()) {
				String shortcutUrl = (String) menuItem.getData(SWT_DATA_MENU_ITEM_KEY);
				if ("fullscreen:".equals(shortcutUrl)) {
					menuItem.setSelection(!fullScreen);
					break;
				}
			}
		if (browserMenu != null) {
			for (MenuItem menuItem : browserMenu.getItems()) {
				String shortcutUrl = (String) menuItem.getData(SWT_DATA_MENU_ITEM_KEY);
				if ("fullscreen:".equals(shortcutUrl)) {
					menuItem.setSelection(!fullScreen);
					break;
				}
			}
		}
	}

	/**
	 * 
	 * @param parent
	 * @return
	 */
	private Menu createMenu(Control parent) {
		Menu menu = null;
		for (final Map<String, Object> shortcut : settings.shortcuts) {
			String name       = (String) shortcut.get("name");
			String short_name = (String) shortcut.get("short_name");

			if (name == null)
				name = "";
			name = name.trim();
			if (name.isEmpty())
				if (short_name != null)
					name = short_name.trim();

			String shortcutUrl = (String) shortcut.get("url");
			if (shortcutUrl == null)
				shortcutUrl = "";
			shortcutUrl = shortcutUrl.trim();

			if (name.isEmpty() && !shortcutUrl.startsWith("separator:"))
				continue;

			if (menu == null)
				menu = new Menu(parent);

			MenuItem menuItem;
			if ("separator:".equals(shortcutUrl)) {
				menuItem = new MenuItem(menu, SWT.SEPARATOR);
				continue;
			} else if ("fullscreen:".equals(shortcutUrl))
				menuItem = new MenuItem(menu, SWT.CHECK);
			else
				menuItem = new MenuItem(menu, SWT.PUSH);

			menuItem.setData(SWT_DATA_MENU_ITEM_KEY, shortcutUrl);
			menuItem.setText(translate(name));

			String description = (String) shortcut.get("description");
			if (description != null)
				description = description.trim();
			description = translate(description);
			if (!description.isEmpty())
				menuItem.setToolTipText(description);

			List<Map<String, Object>> icons = (List<Map<String, Object>>) shortcut.get("icons");
			Image                     icon  = getSmallImage(display, contextUrl, icons);
			if (icon != null)
				menuItem.setImage(icon);

			if (shortcutUrl.equals("fullscreen:"))
				menuItem.setEnabled(settings.enableFullScreen && GuiUtils.isFullScreenSupported());
			else if (shortcutUrl.equals("home:"))
				menuItem.setEnabled(false);
			else if (shortcutUrl.equals("back:"))
				menuItem.setEnabled(false);
			else if (shortcutUrl.equals("forward:"))
				menuItem.setEnabled(false);
			else if (shortcutUrl.equals("open_in_new_window:")) {
				boolean miOpenInNewWindowEnabled = settings.display != EDisplay.DESKTOP_AREA && settings.display != EDisplay.FULLSCREEN && settings.display != EDisplay.FRAMELESS_WINDOW;
				menuItem.setEnabled(miOpenInNewWindowEnabled);
			} else if (shortcutUrl.equals("open_in_browser:")) {
				boolean miOpenInBrowserEnabled = Desktop.getDesktop().isSupported(Desktop.Action.BROWSE) && settings.display != EDisplay.DESKTOP_AREA && settings.display != EDisplay.FULLSCREEN && settings.display != EDisplay.FRAMELESS_WINDOW;
				menuItem.setEnabled(miOpenInBrowserEnabled);
			}

			menuItem.addListener(SWT.Selection, new Listener() {

				public void handleEvent(Event e) {
					String shortcutUrl = (String) e.widget.getData(SWT_DATA_MENU_ITEM_KEY);
					if (shortcutUrl.isEmpty())
						return;
					if (shortcutUrl.equals("open_in_new_window:"))
						new BrowserWindow(false, tmpDir, urlClassLoader, generatedClassesMap, null, settings, contextUrl, homeUrl, currentUrl).launch();
					else if (shortcutUrl.equals("open_in_browser:"))
						display.asyncExec(new Runnable() {

							@Override
							public void run() {
								try {
									Desktop.getDesktop().browse(new URI(currentUrl));
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					else if (shortcutUrl.equals("home:"))
						browser.setUrl(homeUrl, null, new String[] { HTTP_HEADER_CACHE_CONTROL, HTTP_HEADER_PRAGMA, HTTP_HEADER_EXPIRES });
					else if (shortcutUrl.equals("back:"))
						browser.back();
					else if (shortcutUrl.equals("forward:"))
						browser.forward();
					else if (shortcutUrl.equals("reload:"))
						browser.refresh();
					else if (shortcutUrl.equals("fullscreen:"))
						toggleFullscreen();
					else if (shortcutUrl.equals("quit:"))
						display.asyncExec(new Runnable() {

							@Override
							public void run() {
								System.exit(0);
							}
						});
					else if (shortcutUrl.startsWith("open_in_new_window:")) {
						int pos = shortcutUrl.lastIndexOf(':');
						shortcutUrl = shortcutUrl.substring(pos + 1);
						if (shortcutUrl.startsWith("/"))
							shortcutUrl = shortcutUrl.substring(1);
						currentUrl = contextUrl + shortcutUrl;
						new BrowserWindow(false, tmpDir, urlClassLoader, generatedClassesMap, null, settings, contextUrl, homeUrl, currentUrl).launch();
					} else if (shortcutUrl.startsWith("open_in_this_window:")) { // the default
						int pos = shortcutUrl.lastIndexOf(':');
						shortcutUrl = shortcutUrl.substring(pos + 1);
						if (shortcutUrl.startsWith("/"))
							shortcutUrl = shortcutUrl.substring(1);
						currentUrl = contextUrl + shortcutUrl;
						browser.setUrl(currentUrl, null, new String[] { HTTP_HEADER_CACHE_CONTROL, HTTP_HEADER_PRAGMA, HTTP_HEADER_EXPIRES });
					} else if (shortcutUrl.startsWith("open_in_browser:")) {
						int pos = shortcutUrl.lastIndexOf(':');
						shortcutUrl = shortcutUrl.substring(pos + 1);
						if (shortcutUrl.startsWith("/"))
							shortcutUrl = shortcutUrl.substring(1);
						currentUrl = contextUrl + shortcutUrl;
						display.asyncExec(new Runnable() {

							@Override
							public void run() {
								try {
									Desktop.getDesktop().browse(new URI(currentUrl));
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					} else if (shortcutUrl.startsWith("java:")) {
						int    pos      = shortcutUrl.indexOf(':');
						String javaCode = shortcutUrl.substring(pos + 1).trim();
						display.asyncExec(new Runnable() {

							@Override
							public void run() {
								try {
									InvokeUtils.runJavaCode(tmpDir, urlClassLoader, generatedClassesMap, javaCode);
								} catch (Throwable e) {
									e.printStackTrace();
								}
							}
						});
					} else if (shortcutUrl.startsWith("js:")) {
						int    pos    = shortcutUrl.indexOf(':');
						String jsCode = shortcutUrl.substring(pos + 1).trim();
						display.asyncExec(new Runnable() {

							@Override
							public void run() {
								try {
									InvokeUtils.runJsCode(jsCode);
								} catch (Throwable e) {
									e.printStackTrace();
								}
							}
						});
					} else {
						currentUrl = contextUrl + (shortcutUrl.startsWith("/") ? shortcutUrl.substring(1) : shortcutUrl);
						browser.setUrl(currentUrl, null, new String[] { HTTP_HEADER_CACHE_CONTROL, HTTP_HEADER_PRAGMA, HTTP_HEADER_EXPIRES });
					}
				}
			});
		}
		return menu;
	}

	private String getTitle() {
		String title;
		title = settings.name;
		if (title == null)
			title = "";
		title = title.trim();
		if (title.isEmpty())
			title = settings.short_name;
		if (title == null)
			title = "";
		title = title.trim();
		return title;
	}

	private String translate(String s) {
		if (s == null)
			return "";
		s = s.trim();
		if (s.startsWith("${") && s.endsWith("}")) {
			String key    = s.substring(2, s.length() - 1).trim();
			String result = (String) settings.strings.get(key);
			if (result == null)
				return s;
			return result;
		}
		return s;
	}

	private static Image getSmallImage(Display display, String contextUrl, List<Map<String, Object>> icons) {
		if (icons == null)
			return null;
		// all icons with not null and not empty "src" property
		List<Image> images = new ArrayList<Image>(icons.size());
		for (Map<String, Object> iconMap : icons) {
			String src = (String) iconMap.get("src");
			if (src == null)
				continue;
			src = src.trim();
			if (src.isEmpty())
				continue;
			Image image = createIconImageByUrl(display, contextUrl, src);
			if (image == null)
				continue;
			images.add(image);
		}
		images.sort(new Comparator<Image>() {

			@Override
			public int compare(Image o1, Image o2) {
				return Integer.valueOf(o1.getImageData().width).compareTo(Integer.valueOf(o2.getImageData().width));
			}
		});
		if (!images.isEmpty())
			return images.get(0);
		return null;
	}

}