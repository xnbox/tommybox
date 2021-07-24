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

import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonUtils {

	/**
	 * Parse JSON object to Map
	 *
	 * @param json
	 * @return
	 * @throws JsonProcessingException
	 */
	public static Map<String, Object> parseJsonToMap(String json) throws JsonProcessingException {
		JsonFactory  jsonFactory = JsonFactory.builder().enable(JsonReadFeature.ALLOW_TRAILING_COMMA).build();
		ObjectMapper om          = new ObjectMapper(jsonFactory);
		om.configure(Feature.ALLOW_COMMENTS, true);
		om.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		om.configure(Feature.ALLOW_SINGLE_QUOTES, true);
		return om.readValue(json, Map.class);
	}

}
