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
