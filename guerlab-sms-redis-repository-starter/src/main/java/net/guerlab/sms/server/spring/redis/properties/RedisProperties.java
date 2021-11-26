/*
 * Copyright 2018-2022 the original author or authors.
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
package net.guerlab.sms.server.spring.redis.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * redis配置
 *
 * @author guer
 */
@Data
@ConfigurationProperties(prefix = RedisProperties.PREFIX)
public class RedisProperties {

    public static final String PREFIX = "sms.verification-code.repository.redis";

    /**
     * Key前缀
     */
    private String keyPrefix;
}
