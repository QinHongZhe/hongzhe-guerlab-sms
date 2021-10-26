/*
 * Copyright 2018-2021 the original author or authors.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.guerlab.sms.core.exception;

import java.util.Locale;

/**
 * 短信发送失败
 *
 * @author guer
 */
public class SendFailedException extends SmsException {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MSG;

    static {
        Locale locale = Locale.getDefault();

        if (Locale.CHINA.equals(locale)) {
            DEFAULT_MSG = "短信发送失败，";
        } else {
            DEFAULT_MSG = "SMS sending failed,";
        }
    }

    /**
     * 通过错误信息构造短信发送失败异常
     *
     * @param message
     *            错误信息
     */
    public SendFailedException(String message) {
        super(DEFAULT_MSG + message);
    }
}
