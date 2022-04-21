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

package net.guerlab.sms.template.support.freemarker;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import freemarker.cache.TemplateLoader;

import net.guerlab.sms.core.exception.SmsException;
import net.guerlab.sms.template.support.freemarker.loader.FreeMarkerTemplateLoader;
import net.guerlab.sms.template.support.freemarker.loader.StringFreeMarkerTemplateLoader;

/**
 * 内部加载器.
 *
 * @author guer
 */
final class InternalTemplateLoader implements TemplateLoader {

	private final Locale locale;

	private final String localeName;

	private final FreeMarkerTemplateLoader<?> defaultLoader;

	private final Map<String, FreeMarkerTemplateLoader<?>> loaderMap = new HashMap<>();

	InternalTemplateLoader(Locale locale) {
		this.locale = locale;
		localeName = "_" + locale;
		defaultLoader = StringFreeMarkerTemplateLoader.INSTANCE;
		loaderMap.put(defaultLoader.protocol(), defaultLoader);
	}

	public Locale getLocale() {
		return locale;
	}

	public void addLoader(FreeMarkerTemplateLoader<?> loader) {
		loaderMap.put(loader.protocol(), loader);
	}

	@Override
	public Object findTemplateSource(String name) throws IOException {
		String key = name;
		int index = name.lastIndexOf(".");
		if (index > 0) {
			String sub = name.substring(0, index);
			if (sub.endsWith(localeName)) {
				sub = sub.substring(0, sub.length() - localeName.length());
			}
			key = sub + name.substring(index);
		}
		else if (name.endsWith(localeName)) {
			key = name.substring(0, name.length() - localeName.length());
		}

		index = key.indexOf(FreeMarkerTemplateLoader.PROTOCOL_DIVISION);
		String protocol = null;
		String path = key;
		if (index > 0) {
			protocol = key.substring(0, index);
			path = key.substring(index + FreeMarkerTemplateLoader.PROTOCOL_DIVISION_LENGTH);
		}

		FreeMarkerTemplateLoader<?> loader = protocol == null ? defaultLoader : loaderMap.get(protocol);
		if (loader == null) {
			throw new SmsException("un support load protocol: " + protocol);
		}

		return loader.findTemplateSource(path);
	}

	@Override
	public long getLastModified(Object templateSource) {
		for (FreeMarkerTemplateLoader<?> loader : loaderMap.values()) {
			if (loader.accept(templateSource)) {
				return loader.getLastModified(templateSource);
			}
		}
		throw new SmsException("not support template source, type is " + templateSource.getClass());
	}

	@Override
	public Reader getReader(Object templateSource, String encoding) throws IOException {
		for (FreeMarkerTemplateLoader<?> loader : loaderMap.values()) {
			if (loader.accept(templateSource)) {
				return loader.getReader(templateSource, encoding);
			}
		}
		throw new SmsException("not support template source, type is " + templateSource.getClass());
	}

	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {
		for (FreeMarkerTemplateLoader<?> loader : loaderMap.values()) {
			if (loader.accept(templateSource)) {
				loader.closeTemplateSource(templateSource);
			}
		}
	}
}
