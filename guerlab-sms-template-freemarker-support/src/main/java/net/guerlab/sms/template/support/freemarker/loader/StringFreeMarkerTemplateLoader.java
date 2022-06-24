/*
 * Copyright 2018-2022 guerlab.net and other contributors.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.html
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.guerlab.sms.template.support.freemarker.loader;

import java.io.Reader;
import java.io.StringReader;

/**
 * 字符串模板加载器.
 *
 * @author guer
 */
public class StringFreeMarkerTemplateLoader implements FreeMarkerTemplateLoader<String> {

	/**
	 * 默认实例.
	 */
	public static final StringFreeMarkerTemplateLoader INSTANCE = new StringFreeMarkerTemplateLoader();

	@Override
	public String protocol() {
		return "text";
	}

	@Override
	public boolean accept(Object templateSource) {
		return templateSource instanceof String;
	}

	@Override
	public Object findTemplateSource(String name) {
		return name;
	}

	@Override
	public long getLastModified(Object templateSource) {
		return System.currentTimeMillis();
	}

	@Override
	public Reader getReader(Object templateSource, String encoding) {
		return new StringReader((String) templateSource);
	}

	@Override
	public void closeTemplateSource(Object templateSource) {

	}
}
