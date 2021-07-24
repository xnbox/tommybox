package org.tommybox.ui.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

public class InputStreamUtils {

	/**
	 * 
	 * @param contextUrl
	 * @param url
	 * @return
	 */
	public static InputStream createInputStreamByUrl(String contextUrl, String url) {
		try {
			if (url.startsWith("data:")) {
				int    dataStartIndex = url.indexOf(",") + 1;
				String data           = url.substring(dataStartIndex);
				byte[] decoded        = Base64.getDecoder().decode(data);
				return new ByteArrayInputStream(decoded);
			} else if (url.startsWith("https:") || url.startsWith("http:") || url.startsWith("file:"))
				return new URL(url).openStream();
			else {
				while (url.startsWith("/"))
					url = url.substring(1);
				return new URL(contextUrl + url).openStream();
			}
		} catch (Exception e) {
			// ignore exception
		}
		return null;
	}

}
