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

package net.guerlab.sms.template.support;

import java.util.Map;

import lombok.Getter;

import net.guerlab.sms.core.utils.StringUtils;

/**
 * 简易通知模板适配器.
 *
 * @author guer
 */
@Getter
public class SimpleNoticeTemplateAdapter implements NoticeTemplateAdapter {

	/**
	 * 适配器key.
	 */
	public static final String ADAPTER_KEY = "SIMPLE";

	/**
	 * 默认前缀.
	 */
	public static final String DEFAULT_PREFIX = "${";

	/**
	 * 默认后缀.
	 */
	public static final String DEFAULT_SUFFIX = "}";

	/**
	 * 前缀.
	 */
	private final String prefix;

	/**
	 * 后缀.
	 */
	private final String suffix;

	/**
	 * 使用默认前缀和后缀构造简易通知模板.
	 */
	@SuppressWarnings("unused")
	public SimpleNoticeTemplateAdapter() {
		this(DEFAULT_PREFIX, DEFAULT_SUFFIX);
	}

	/**
	 * 使用指定前缀和后缀构造简易通知模板.
	 *
	 * @param prefix 前缀
	 * @param suffix 后缀
	 */
	public SimpleNoticeTemplateAdapter(String prefix, String suffix) {
		this.prefix = prefix;
		this.suffix = suffix;
	}

	@Override
	public String adapterKey() {
		return ADAPTER_KEY;
	}

	@Override
	public boolean support(String contentTemplate) {
		return contentTemplate.contains(prefix) && contentTemplate.contains(suffix);
	}

	@Override
	public String format(String contentTemplate, Map<String, String> params) {
		String template = contentTemplate;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			String key = StringUtils.trimToNull(entry.getKey());
			if (key == null) {
				continue;
			}

			String value = entry.getValue();

			template = template.replace(prefix + key + suffix, value);
		}

		return template;
	}
}
