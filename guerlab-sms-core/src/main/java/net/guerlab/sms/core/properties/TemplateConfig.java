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

package net.guerlab.sms.core.properties;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.springframework.lang.Nullable;

/**
 * 基础短信配置类.
 *
 * @author guer
 */
@Setter
@Getter
public class TemplateConfig {

	/**
	 * 短信模板.
	 */
	protected Map<String, String> templates;

	/**
	 * 获取短信模板.
	 *
	 * @param type 类型
	 * @return 短信模板
	 */
	@Nullable
	public String getTemplates(String type) {
		return templates == null ? null : templates.get(type);
	}
}
