package net.guerlab.sms.core.exception;

import java.util.Locale;

import net.guerlab.commons.exception.ApplicationException;

/**
 * 手机号无效
 *
 * @author guer
 *
 */
public class PhoneIsNullError extends ApplicationException {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MSG;

    static {
        Locale locale = Locale.getDefault();

        if (Locale.CHINA.equals(locale)) {
            DEFAULT_MSG = "手机号无效";
        } else {
            DEFAULT_MSG = "Invalid phone number.";
        }
    }

    /**
     * 手机号无效
     */
    public PhoneIsNullError() {
        super(DEFAULT_MSG);
    }
}
