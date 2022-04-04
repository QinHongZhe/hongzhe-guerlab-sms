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

package net.guerlab.sms.chinamobile;

import java.util.Collections;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import org.springframework.web.client.RestTemplate;

import net.guerlab.sms.core.domain.NoticeData;

public class ChinaMobileSignTests {

	@Test
	public void sendFailTest() {
		ChinaMobileProperties properties = new ChinaMobileProperties();
		properties.setEcName("政企分公司测试");
		properties.setApId("demo0");
		properties.setSecretKey("123qwe");
		properties.setSign("4sEuJxDpC");
		properties.setTemplates(Collections.singletonMap("test", "38516fabae004eddbfa3ace1d4194696"));
		properties.setParamsOrders(Collections.singletonMap("test", Collections.singletonList("code")));

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		ChinaMobileSendHandler sendHandler = new ChinaMobileSendHandler(properties, event -> {
		}, objectMapper,
				new RestTemplate());

		NoticeData noticeData = new NoticeData();
		noticeData.setType("test");
		noticeData.setParams(Collections.singletonMap("code", "abcde"));

		Assert.assertFalse(sendHandler.send(noticeData, Collections.singletonList("13800138000")));
	}
}
