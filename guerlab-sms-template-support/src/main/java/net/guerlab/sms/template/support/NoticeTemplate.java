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

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.springframework.lang.Nullable;

import net.guerlab.sms.core.domain.NoticeData;
import net.guerlab.sms.core.utils.StringUtils;

/**
 * 通知模板适配器.
 *
 * @author guer
 */
public final class NoticeTemplate {

	/**
	 * 默认适配器环境变量配置名称.
	 */
	public static final String DEFAULT_ADAPTER_ENV_NAME = "net.guerlab.sms.template.defaultAdapterKey";

	/**
	 * 适配器key链接符.
	 */
	public static final String ADAPTER_KEY_LINK_CHARACTER = ":";

	/**
	 * 适配器表.
	 */
	private static final Map<String, NoticeTemplateAdapter> ADAPTER_MAP = new HashMap<>();

	/**
	 * 默认适配器key.
	 */
	private static String defaultAdapterKey = SimpleNoticeTemplateAdapter.ADAPTER_KEY;

	static {
		ServiceLoader<NoticeTemplateAdapter> serviceLoader = ServiceLoader.load(NoticeTemplateAdapter.class);
		serviceLoader.forEach(adapter -> ADAPTER_MAP.put(adapter.adapterKey().toUpperCase(), adapter));

		String defaultAdapterKey = System.getenv(DEFAULT_ADAPTER_ENV_NAME);
		setDefaultAdapterKey(defaultAdapterKey);
	}

	private NoticeTemplate() {
	}

	/**
	 * 获取默认适配器key.
	 *
	 * @return 默认适配器key.
	 */
	public static String getDefaultAdapterKey() {
		return defaultAdapterKey;
	}

	/**
	 * 设置默认适配器key.
	 * <p>
	 * 仅当该适配器存在时，设置才会生效
	 *
	 * @param adapterKey 默认适配器key.
	 */
	public static void setDefaultAdapterKey(@Nullable String adapterKey) {
		if (adapterKey != null && ADAPTER_MAP.containsKey(adapterKey)) {
			defaultAdapterKey = adapterKey;
		}
	}

	/**
	 * 格式化通知模板内容.
	 *
	 * @param contentTemplate 内容模板
	 * @param noticeData      通知内容
	 * @return 通知内容
	 */
	public static String format(String contentTemplate, NoticeData noticeData) {
		Map<String, String> params = noticeData.getParams();
		if (StringUtils.isBlank(contentTemplate) || params == null || params.isEmpty()) {
			return "";
		}

		int adapterKeyIndex = contentTemplate.indexOf(ADAPTER_KEY_LINK_CHARACTER);
		NoticeTemplateAdapter templateAdapter = null;
		if (adapterKeyIndex > 0) {
			String adapterKey = contentTemplate.substring(0, adapterKeyIndex).toUpperCase();
			contentTemplate = contentTemplate.substring(adapterKeyIndex + 1);
			templateAdapter = ADAPTER_MAP.get(adapterKey);
		}

		if (templateAdapter == null) {
			String content = contentTemplate;
			templateAdapter = ADAPTER_MAP.values().stream().filter(adapter -> adapter.support(content))
					.findFirst()
					.orElse(ADAPTER_MAP.get(defaultAdapterKey));
		}

		return templateAdapter != null ? templateAdapter.format(contentTemplate, params) : contentTemplate;
	}
}
