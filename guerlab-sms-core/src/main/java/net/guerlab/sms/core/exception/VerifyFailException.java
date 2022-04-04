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

package net.guerlab.sms.core.exception;

import java.util.Locale;

/**
 * 验证失败.
 *
 * @author guer
 */
public class VerifyFailException extends SmsException {

	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_MSG;

	static {
		Locale locale = Locale.getDefault();

		if (Locale.CHINA.equals(locale)) {
			DEFAULT_MSG = "验证失败";
		}
		else {
			DEFAULT_MSG = "Validation fails";
		}
	}

	/**
	 * 验证失败.
	 */
	public VerifyFailException() {
		super(DEFAULT_MSG);
	}
}
