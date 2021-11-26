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
package net.guerlab.sms.server.properties;

import lombok.Data;

/**
 * 验证码配置
 *
 * @author guer
 */
@Data
public class VerificationCodeConfig {

    /**
     * 默认验证码业务所使用的类型
     */
    public static final String DEFAULT_TYPE = "VerificationCode";

    /**
     * 验证码业务所使用的类型
     */
    private String type = DEFAULT_TYPE;

    /**
     * 验证码过期时间,小于等于0表示不过期,单位秒
     */
    private Long expirationTime;

    /**
     * 重新发送验证码间隔时间,小于等于0表示不启用,单位秒
     */
    private Long retryIntervalTime;

    /**
     * 验证码长度
     */
    private int codeLength = 6;

    /**
     * 是否使用识别码
     */
    private boolean useIdentificationCode = false;

    /**
     * 识别码长度
     */
    private int identificationCodeLength = 3;

    /**
     * 验证成功是否删除验证码
     */
    private boolean deleteByVerifySucceed = true;

    /**
     * 验证失败是否删除验证码
     */
    private boolean deleteByVerifyFail = false;

    /**
     * 模板中是否包含过期时间
     */
    private boolean templateHasExpirationTime = false;
}
