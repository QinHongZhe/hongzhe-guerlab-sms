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

/**
 * 通知模板适配器.
 *
 * @author guer
 */
public interface NoticeTemplateAdapter {

	/**
	 * 获取适配器key.
	 *
	 * @return 适配器key
	 */
	String adapterKey();

	/**
	 * 判断是否支持该类型模板.
	 *
	 * @param contentTemplate 内容模板
	 * @return 是否支持
	 */
	boolean support(String contentTemplate);

	/**
	 * 格式化通知模板内容.
	 *
	 * @param contentTemplate 内容模板
	 * @param params          参数表
	 * @return 通知内容
	 */
	String format(String contentTemplate, Map<String, String> params);
}
