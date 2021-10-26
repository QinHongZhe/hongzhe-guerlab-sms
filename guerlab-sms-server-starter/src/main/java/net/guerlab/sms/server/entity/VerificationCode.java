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
package net.guerlab.sms.server.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 验证码
 *
 * @author guer
 */
@Data
public class VerificationCode {

    /**
     * 手机号
     */
    private String phone;

    /**
     * 验证码
     */
    private String code;

    /**
     * 识别码
     */
    private String identificationCode;

    /**
     * 可重试时间
     */
    private LocalDateTime retryTime;

    /**
     * 过期时间
     */
    private LocalDateTime expirationTime;
}
