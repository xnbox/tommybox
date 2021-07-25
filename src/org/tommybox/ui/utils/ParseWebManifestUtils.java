package org.tommybox.ui.utils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.Map;
import java.util.TreeMap;

import org.tommybox.main.Settings;
import org.tommybox.webmanifest.EDisplay;
import org.tommybox.webmanifest.EWindowButton;
import org.tommybox.webmanifest.EWindowMenu;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ParseWebManifestUtils {
	public static void parseWebManifest(Settings settings, byte[] bs) throws JsonProcessingException {
		final String EMPTY_MANIFEST = "{}";
		String       manifestJson;
		if (bs == null)
			manifestJson = EMPTY_MANIFEST;
		else {
			manifestJson = new String(bs, StandardCharsets.UTF_8).trim();
			manifestJson = manifestJson.trim();
			if (manifestJson.isEmpty())
				manifestJson = EMPTY_MANIFEST;
		}

		Map<String, Object> manifestJsonMap = JacksonUtils.parseJsonToMap(manifestJson);

		/*
		 * W3C Editor's Draft 01 July 2021
		 * https://w3c.github.io/manifest
		 */

		/*
		 * W3C Working Draft 01 July 2021
		 * https://www.w3.org/TR/appmanifest
		 */
		String                    mfName                        = (String) manifestJsonMap.get("name");                         // standard
		String                    mfShort_name                  = (String) manifestJsonMap.get("short_name");                   // standard
		String                    mfLang                        = (String) manifestJsonMap.get("lang");                         // standard
		String                    mfDir                         = (String) manifestJsonMap.get("dir");                          // standard
		String                    mfStart_url                   = (String) manifestJsonMap.get("start_url");                    // standard
		String                    mfDisplay                     = (String) manifestJsonMap.get("display");                      // standard
		String                    mfBackground_color            = (String) manifestJsonMap.get("background_color");             // standard
		String                    mfOrientation                 = (String) manifestJsonMap.get("orientation");                  // standard
		Boolean                   mfPrefer_related_applications = (Boolean) manifestJsonMap.get("prefer_related_applications"); // standard
		String                    mfScope                       = (String) manifestJsonMap.get("scope");                        // standard
		String                    mfTheme_color                 = (String) manifestJsonMap.get("theme_color");                  // standard
		List<Map<String, Object>> mfIcons                       = (List) manifestJsonMap.get("icons");                          // standard
		List<Map<String, Object>> mfShortcuts                   = (List) manifestJsonMap.get("shortcuts");                      // standard
		List<Map<String, Object>> mfRelated_applications        = (List) manifestJsonMap.get("related_applications");           // standard

		Map<String, Object> mfStrings = (Map<String, Object>) manifestJsonMap.get("strings"); // standard
		settings.windowX = (Integer) manifestJsonMap.get("window_x"); // non-standard
		settings.windowY = (Integer) manifestJsonMap.get("window_y"); // non-standard

		Boolean      mfEnableFullScreen  = (Boolean) manifestJsonMap.get("enable_fullscreen");    // non-standard
		Boolean      mfWindowAlwaysOnTop = (Boolean) manifestJsonMap.get("window_always_on_top"); // non-standard
		Boolean      mfTrayIcon          = (Boolean) manifestJsonMap.get("tray_icon");            // non-standard
		String       mfWindowSize        = (String) manifestJsonMap.get("window_size");           // non-standard
		String       mfWindowMenu        = (String) manifestJsonMap.get("window_menu");           // non-standard
		List<String> mfWindowButtonsList = (List) manifestJsonMap.get("window_buttons");          // non-standard

		if (mfName != null) {
			mfName        = mfName.trim();
			settings.name = mfName;
		}

		if (mfShort_name != null) {
			mfShort_name        = mfShort_name.trim();
			settings.short_name = mfShort_name;
		}

		if (mfLang == null)
			mfLang = Locale.getDefault(Category.DISPLAY).getLanguage();
		else {
			mfLang = mfLang.trim();
			if (mfLang.isEmpty())
				mfLang = Locale.getDefault(Category.DISPLAY).getLanguage();
			else {
				if (mfLang.contains("-"))
					mfLang = new Locale.Builder().setLanguageTag(mfLang).build().getLanguage();
			}
		}
		settings.lang = mfLang;

		if (mfDir != null) {
			mfDir        = mfDir.trim();
			settings.dir = mfDir;
		}

		if (mfStart_url != null) {
			mfStart_url        = mfStart_url.trim();
			settings.start_url = mfStart_url;
		}

		try {
			settings.display = Enum.valueOf(EDisplay.class, mfDisplay.trim().replace('-', '_').toUpperCase(Locale.ENGLISH));
		} catch (Throwable e) {
			// ignore exception
		}

		if (mfBackground_color != null) {
			mfBackground_color        = mfBackground_color.trim();
			settings.background_color = mfBackground_color;
		}

		if (mfOrientation != null) {
			mfOrientation        = mfOrientation.trim();
			settings.orientation = mfOrientation;
		}

		if (mfPrefer_related_applications != null)
			settings.prefer_related_applications = mfPrefer_related_applications;

		if (mfScope != null) {
			mfScope        = mfScope.trim();
			settings.scope = mfScope;
		}

		if (mfTheme_color != null) {
			mfTheme_color        = mfTheme_color.trim();
			settings.theme_color = mfTheme_color;
		}

		if (mfIcons != null)
			settings.icons = mfIcons;

		if (mfShortcuts != null)
			settings.shortcuts = mfShortcuts;

		if (mfRelated_applications != null)
			settings.related_applications = mfRelated_applications;

		if (mfEnableFullScreen != null)
			settings.enableFullScreen = mfEnableFullScreen;

		if (mfWindowAlwaysOnTop != null)
			settings.windowAlwaysOnTop = mfWindowAlwaysOnTop;

		if (mfWindowSize != null) {
			mfWindowSize = mfWindowSize.trim();
			if (!mfWindowSize.isEmpty()) {
				if (mfWindowSize.contains("x")) {
					String[] arr = mfWindowSize.split("x");
					try {
						settings.windowWidth = Integer.parseInt(arr[0]);
					} catch (Throwable e) {
						// ignore exception
					}
					try {
						settings.windowHeight = Integer.parseInt(arr[1]);
					} catch (Throwable e) {
						// ignore exception
					}
				}
			}
		}

		if (mfTrayIcon != null)
			settings.trayIcon = mfTrayIcon;

		if (mfWindowMenu != null)
			try {
				settings.windowMenu = Enum.valueOf(EWindowMenu.class, mfWindowMenu.replace('-', '_').toUpperCase(Locale.ENGLISH));
			} catch (Exception e) {
				// ignore
			}

		if (mfWindowButtonsList != null) {
			settings.windowButtonsList.clear();
			for (String windowButtonStr : mfWindowButtonsList) {
				if (windowButtonStr == null)
					continue;
				windowButtonStr = windowButtonStr.trim();
				if (windowButtonStr.isEmpty())
					continue;
				try {
					EWindowButton windowButton = Enum.valueOf(EWindowButton.class, windowButtonStr.replace('-', '_').toUpperCase(Locale.ENGLISH));
					settings.windowButtonsList.add(windowButton);
				} catch (Exception e) {
					// ignore
				}
			}
		}

		if (mfStrings != null) {
			mfStrings = new TreeMap<>(mfStrings);
			String lang;
			Map<String, Object> enStrings = null;
			for (Map.Entry<String, Object> langStrings : mfStrings.entrySet()) {
				lang = langStrings.getKey();
				if (lang.startsWith("en")) {
					enStrings = (Map<String, Object>) langStrings.getValue();
					break;
				}
			}
			if (enStrings == null)
				enStrings = new HashMap<>();
			Map<String, Object> langStrings = (Map<String, Object>) mfStrings.get(settings.lang);
			if (langStrings != null)
				enStrings.putAll(langStrings);
			settings.strings = enStrings;
		}
	}

}
