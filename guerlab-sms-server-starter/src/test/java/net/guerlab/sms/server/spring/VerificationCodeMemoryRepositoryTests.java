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

package net.guerlab.sms.server.spring;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import net.guerlab.sms.server.entity.VerificationCode;
import net.guerlab.sms.server.properties.VerificationCodeMemoryRepositoryConfig;
import net.guerlab.sms.server.repository.VerificationCodeMemoryRepository;

/**
 * @author guer
 */
@Slf4j
public class VerificationCodeMemoryRepositoryTests {

	@Test
	public void test() {
		VerificationCodeMemoryRepositoryConfig properties = new VerificationCodeMemoryRepositoryConfig();
		properties.setGcFrequency(30L);

		VerificationCodeMemoryRepository repository = new VerificationCodeMemoryRepository(properties);

		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, r -> {
			Thread thread = new Thread(r);
			thread.setName("test-add");
			return thread;
		});

		executor.scheduleAtFixedRate(() -> {
			LocalDateTime now = LocalDateTime.now();
			VerificationCode verificationCode = new VerificationCode();

			verificationCode.setPhone(now.toString());
			verificationCode.setExpirationTime(now);

			repository.save(verificationCode);
		}, 1, 1, TimeUnit.SECONDS);

	}
}
