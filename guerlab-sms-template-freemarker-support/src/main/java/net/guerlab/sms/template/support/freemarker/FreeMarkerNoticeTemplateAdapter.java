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
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;

import net.guerlab.sms.core.exception.SmsException;
import net.guerlab.sms.template.support.NoticeTemplateAdapter;
import net.guerlab.sms.template.support.freemarker.loader.FreeMarkerTemplateLoader;

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

	private static final InternalTemplateLoader loader;

	private final Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

	static {
		Locale locale = Locale.getDefault();
		loader = new InternalTemplateLoader(locale);
	}

	public FreeMarkerNoticeTemplateAdapter() {
		configuration.setDefaultEncoding(StandardCharsets.UTF_8.displayName());
		configuration.setTemplateLoader(loader);
		configuration.setLocale(loader.getLocale());
	}

	/**
	 * 添加模板加载器.
	 *
	 * @param loader 模板加载器
	 */
	public static void addTemplateLoader(FreeMarkerTemplateLoader<?> loader) {
		FreeMarkerNoticeTemplateAdapter.loader.addLoader(loader);
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
			return writer.toString().trim();
		}
		catch (Exception e) {
			throw new SmsException(e);
		}
	}

}
