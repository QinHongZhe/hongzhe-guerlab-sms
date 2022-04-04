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

package net.guerlab.sms.server.spring.redis.repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.lang.Nullable;

import net.guerlab.sms.core.utils.StringUtils;
import net.guerlab.sms.server.entity.VerificationCode;
import net.guerlab.sms.server.repository.VerificationCodeRepository;
import net.guerlab.sms.server.spring.redis.properties.RedisProperties;

/**
 * 验证码redis储存实现.
 *
 * @author guer
 */
@Slf4j
public class VerificationCodeRedisRepository implements VerificationCodeRepository {

	private final RedisProperties properties;

	private final StringRedisTemplate redisTemplate;

	private final ObjectMapper objectMapper;

	public VerificationCodeRedisRepository(RedisProperties properties, StringRedisTemplate redisTemplate,
			ObjectMapper objectMapper) {
		this.properties = properties;
		this.redisTemplate = redisTemplate;
		this.objectMapper = objectMapper;
	}

	@Override
	public VerificationCode findOne(String phone, @Nullable String identificationCode) {
		String key = key(phone, identificationCode);
		String data = redisTemplate.opsForValue().get(key);

		if (StringUtils.isBlank(data)) {
			log.debug("json data is empty for key: {}", key);
			return null;
		}

		try {
			return objectMapper.readValue(data, VerificationCode.class);
		}
		catch (Exception e) {
			log.debug(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public void save(VerificationCode verificationCode) {
		String key = key(verificationCode.getPhone(), verificationCode.getIdentificationCode());

		ValueOperations<String, String> operations = redisTemplate.opsForValue();

		LocalDateTime expirationTime = verificationCode.getExpirationTime();

		String value;

		try {
			value = objectMapper.writeValueAsString(verificationCode);
		}
		catch (Exception e) {
			log.debug(e.getMessage(), e);
			throw new RuntimeException(e);
		}

		if (expirationTime == null) {
			operations.set(key, value);
		}
		else {
			long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
			long end = expirationTime.toEpochSecond(ZoneOffset.UTC);
			long timeout = end - now;

			operations.set(key, value, timeout, TimeUnit.SECONDS);
		}
	}

	@Override
	public void delete(String phone, @Nullable String identificationCode) {
		redisTemplate.delete(key(phone, identificationCode));
	}

	private String key(String phone, @Nullable String identificationCode) {
		String keyPrefix = StringUtils.trimToNull(properties.getKeyPrefix());
		String tempIdentificationCode = StringUtils.trimToNull(identificationCode);

		StringBuilder keyBuilder = new StringBuilder();

		if (keyPrefix != null) {
			keyBuilder.append(keyPrefix);
			keyBuilder.append("_");
		}

		keyBuilder.append(StringUtils.trimToNull(phone));

		if (tempIdentificationCode != null) {
			keyBuilder.append("_");
			keyBuilder.append(tempIdentificationCode);
		}

		return keyBuilder.toString();
	}

}
