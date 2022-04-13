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

import java.io.CharArrayWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import net.guerlab.sms.core.exception.SmsException;
import net.guerlab.sms.template.support.NoticeTemplateAdapter;

/**
 * FreeMarker通知模板适配器.
 *
 * @author guer
 */
public class FreeMarkerNoticeTemplateAdapter implements NoticeTemplateAdapter {

	/**
	 * 适配器key.
	 */
	public static final String ADAPTER_KEY = "FREEMARKER";

	private final Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

	public FreeMarkerNoticeTemplateAdapter() {
		Locale locale = Locale.getDefault();

		configuration.setDefaultEncoding(StandardCharsets.UTF_8.displayName());
		configuration.setTemplateLoader(new InternalTemplateLoader(locale));
		configuration.setLocale(locale);
	}

	@Override
	public String adapterKey() {
		return ADAPTER_KEY;
	}

	@Override
	public boolean support(String contentTemplate) {
		return true;
	}

	@Override
	public String format(String contentTemplate, Map<String, String> params) {
		try {
			Template template = configuration.getTemplate(contentTemplate);
			CharArrayWriter writer = new CharArrayWriter();
			template.process(params, writer);
			return writer.toString();
		}
		catch (Exception e) {
			throw new SmsException(e);
		}
	}

	private static final class InternalTemplateLoader implements TemplateLoader {

		private final Locale locale;

		InternalTemplateLoader(Locale locale) {
			this.locale = locale;
		}

		@Override
		public Object findTemplateSource(String name) {
			String suffix = locale.toString();
			int length = suffix.length() + 1;
			return name.substring(0, name.length() - length);
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
}
