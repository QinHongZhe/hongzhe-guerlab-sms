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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.guerlab.sms.core.domain.NoticeData;
import net.guerlab.sms.template.support.NoticeTemplate;
import net.guerlab.sms.template.support.freemarker.loader.FileFreeMarkerTemplateLoader;

/**
 * @author guer
 */
class FreeMarkerNoticeTemplateAdapterTests {

	@Test
	void setValidDefaultAdapterKey() {
		NoticeTemplate.setDefaultAdapterKey(FreeMarkerNoticeTemplateAdapter.ADAPTER_KEY);
		Assertions.assertEquals(FreeMarkerNoticeTemplateAdapter.ADAPTER_KEY, NoticeTemplate.getDefaultAdapterKey());
	}

	@Test
	void format1() {
		NoticeData noticeData = new NoticeData();
		Map<String, String> params = new HashMap<>();
		params.put("code", "080601");
		params.put("smsSign", "测试短信签名");
		params.put("time", "5");

		noticeData.setParams(params);

		String contentTemplate = "freemarker:验证码：${code}，有效期：${time}分钟【${smsSign}】";
		String targetContent = "验证码：080601，有效期：5分钟【测试短信签名】";
		String resultContent = NoticeTemplate.format(contentTemplate, noticeData);
		Assertions.assertEquals(targetContent, resultContent);
	}

	@Test
	void format2() {
		NoticeData noticeData = new NoticeData();
		Map<String, String> params = new HashMap<>();
		params.put("code", "080601");
		params.put("smsSign", "测试短信签名");
		params.put("time", "5");

		noticeData.setParams(params);

		String contentTemplate = "freemarker:验证码：${code}<#if time??>，有效期：${time}分钟</#if>【${smsSign}】";
		String targetContent = "验证码：080601，有效期：5分钟【测试短信签名】";
		String resultContent = NoticeTemplate.format(contentTemplate, noticeData);
		Assertions.assertEquals(targetContent, resultContent);
	}

	@Test
	void format3() {
		NoticeData noticeData = new NoticeData();
		Map<String, String> params = new HashMap<>();
		params.put("code", "080601");
		params.put("smsSign", "测试短信签名");

		noticeData.setParams(params);

		String contentTemplate = "freemarker:验证码：${code}<#if time??>，有效期：${time}分钟</#if>【${smsSign}】";
		String targetContent = "验证码：080601【测试短信签名】";
		String resultContent = NoticeTemplate.format(contentTemplate, noticeData);
		Assertions.assertEquals(targetContent, resultContent);
	}

	@Test
	void format4() {
		NoticeData noticeData = new NoticeData();
		Map<String, String> params = new HashMap<>();
		params.put("code", "080601");
		params.put("smsSign", "测试短信签名");

		noticeData.setParams(params);

		FreeMarkerNoticeTemplateAdapter.addTemplateLoader(new FileFreeMarkerTemplateLoader());

		String contentTemplate = "freemarker:file:./src/test/resources/template.fd";
		String targetContent = "验证码：080601【测试短信签名】";
		String resultContent = NoticeTemplate.format(contentTemplate, noticeData);
		Assertions.assertEquals(targetContent, resultContent);
	}
}
