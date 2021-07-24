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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tommybox.webmanifest.EDisplay;
import org.tommybox.webmanifest.EWindowButton;
import org.tommybox.webmanifest.EWindowMenu;

public class Settings {
	/*
	 * W3C Editor's Draft 01 July 2021
	 * https://w3c.github.io/manifest
	 */

	/*
	 * W3C Working Draft 01 July 2021
	 * https://www.w3.org/TR/appmanifest
	 */

	/**
	background_color
	dir
	display
	icons
	lang
	name
	orientation
	prefer_related_applications
	related_applications
	scope
	short_name
	shortcuts
	start_url
	theme_color
	 */
	public String                    name                        = "";                                                                                                                          // standard
	public String                    short_name                  = "";                                                                                                                          // standard
	public String                    lang                        = null;                                                                                                                        // standard
	public String                    dir                         = "";                                                                                                                          // standard
	public String                    start_url                   = "";                                                                                                                          // standard
	public EDisplay                  display                     = EDisplay.STANDALONE;                                                                                                         // standard
	public String                    background_color            = "";                                                                                                                          // standard
	public String                    scope                       = "/";                                                                                                                         // standard // contextPath
	public String                    theme_color                 = "";                                                                                                                          // standard
	public String                    orientation                 = "";                                                                                                                          // standard
	public Boolean                   prefer_related_applications = false;                                                                                                                       // standard
	public List<Map<String, Object>> icons                       = new ArrayList<>();                                                                                                           // standard
	public List<Map<String, Object>> shortcuts                   = new ArrayList<>();                                                                                                           // standard
	public List<Map<String, Object>> related_applications        = new ArrayList<>();                                                                                                           // standard
	public Map<String, Object>       strings                     = new HashMap<>();                                                                                                             // non-standard
	public boolean                   enableFullScreen            = true;                                                                                                                        // non-standard
	public boolean                   windowAlwaysOnTop           = false;                                                                                                                       // non-standard
	public boolean                   trayIcon                    = true;                                                                                                                        // non-standard
	public Integer                   windowX                     = null;                                                                                                                        // non-standard
	public Integer                   windowY                     = null;                                                                                                                        // non-standard
	public Integer                   windowWidth                 = null;                                                                                                                        // non-standard
	public Integer                   windowHeight                = null;                                                                                                                        // non-standard
	public EWindowMenu               windowMenu                  = EWindowMenu.CUSTOM;                                                                                                          // non-standard
	public List<EWindowButton>       windowButtonsList           = new ArrayList<>(Arrays.asList(new EWindowButton[] { EWindowButton.MINIMIZE, EWindowButton.MAXIMIZE, EWindowButton.CLOSE })); // non-standard
}
