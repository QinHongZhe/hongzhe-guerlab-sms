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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.guerlab.sms.core.domain.NoticeData;

/**
 * 简易通知模板适配器测试.
 *
 * @author guer
 */
class NoticeTemplateTests {

	static NoticeData noticeData = new NoticeData();

	static String targetContent;

	@BeforeAll
	static void before() {
		Map<String, String> params = new HashMap<>();
		params.put("code", "080601");
		params.put("smsSign", "测试短信签名");
		params.put("time", "5");

		noticeData.setParams(params);

		targetContent = "验证码：080601，有效期：5分钟【测试短信签名】";
	}

	@Test
	void setInvalidDefaultAdapterKey() {
		NoticeTemplate.setDefaultAdapterKey("foo");
		Assertions.assertEquals(SimpleNoticeTemplateAdapter.ADAPTER_KEY, NoticeTemplate.getDefaultAdapterKey());
	}

	@Test
	void setDefaultAdapterKeyByEnv() {
		Assertions.assertEquals(SimpleNoticeTemplateAdapter.ADAPTER_KEY, NoticeTemplate.getDefaultAdapterKey());
	}

	@Test
	void auto() {
		String contentTemplate = "验证码：${code}，有效期：${time}分钟【${smsSign}】";
		String resultContent = NoticeTemplate.format(contentTemplate, noticeData);
		Assertions.assertEquals(targetContent, resultContent);
	}

	@Test
	void simple() {
		String contentTemplate = "simple:验证码：${code}，有效期：${time}分钟【${smsSign}】";
		String resultContent = NoticeTemplate.format(contentTemplate, noticeData);
		Assertions.assertEquals(targetContent, resultContent);
	}
}
