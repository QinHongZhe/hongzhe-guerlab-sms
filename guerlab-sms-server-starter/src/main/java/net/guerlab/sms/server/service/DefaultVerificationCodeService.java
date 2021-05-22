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
package net.guerlab.sms.server.service;

import net.guerlab.sms.core.domain.NoticeData;
import net.guerlab.sms.core.exception.PhoneIsNullException;
import net.guerlab.sms.core.exception.RetryTimeShortException;
import net.guerlab.sms.core.utils.StringUtils;
import net.guerlab.sms.server.entity.VerificationCode;
import net.guerlab.sms.server.properties.VerificationCodeProperties;
import net.guerlab.sms.server.repository.VerificationCodeRepository;
import net.guerlab.sms.server.utils.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 手机验证码服务实现
 *
 * @author guer
 *
 */
@Service
public class DefaultVerificationCodeService implements VerificationCodeService {

    private VerificationCodeRepository repository;

    private VerificationCodeProperties properties;

    private NoticeService noticeService;

    private CodeGenerate codeGenerate;

    private VerificationCodeTypeGenerate verificationCodeTypeGenerate;

    @Autowired
    public void setRepository(VerificationCodeRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setProperties(VerificationCodeProperties properties) {
        this.properties = properties;
    }

    @Autowired
    public void setNoticeService(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @Autowired
    public void setCodeGenerate(CodeGenerate codeGenerate) {
        this.codeGenerate = codeGenerate;
    }

    @Autowired(required = false)
    public void setVerificationCodeTypeGenerate(VerificationCodeTypeGenerate verificationCodeTypeGenerate) {
        this.verificationCodeTypeGenerate = verificationCodeTypeGenerate;
    }

    @Override
    public String find(String phone, String identificationCode) {
        if (StringUtils.isBlank(phone)) {
            return null;
        }

        phoneValidation(phone);

        VerificationCode verificationCode = repository.findOne(phone, identificationCode);

        return verificationCode == null ? null : verificationCode.getCode();
    }

    private String createIdentificationCode() {
        if (!properties.isUseIdentificationCode()) {
            return null;
        }

        return RandomUtils.nextString(properties.getIdentificationCodeLength());
    }

    @Override
    public void send(String tempPhone) {
        String phone = StringUtils.trimToNull(tempPhone);

        if (phone == null) {
            throw new PhoneIsNullException();
        }

        phoneValidation(phone);

        String identificationCode = createIdentificationCode();
        VerificationCode verificationCode = repository.findOne(phone, identificationCode);
        boolean newVerificationCode = false;

        Long expirationTime = properties.getExpirationTime();

        if (verificationCode == null) {
            verificationCode = new VerificationCode();
            verificationCode.setPhone(phone);
            verificationCode.setIdentificationCode(identificationCode);

            Long retryIntervalTime = properties.getRetryIntervalTime();

            if (expirationTime != null && expirationTime > 0) {
                verificationCode.setExpirationTime(LocalDateTime.now().plusSeconds(expirationTime));
            }
            if (retryIntervalTime != null && retryIntervalTime > 0) {
                verificationCode.setRetryTime(LocalDateTime.now().plusSeconds(retryIntervalTime));
            }

            verificationCode.setCode(codeGenerate.generate());
            newVerificationCode = true;
        } else {
            LocalDateTime retryTime = verificationCode.getRetryTime();

            if (retryTime != null) {
                long surplus =
                        retryTime.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

                if (surplus > 0) {
                    throw new RetryTimeShortException(surplus);
                }
            }
        }

        Map<String, String> params = new HashMap<>(4);
        params.put(MSG_KEY_CODE, verificationCode.getCode());
        if (verificationCode.getIdentificationCode() != null) {
            params.put(MSG_KEY_IDENTIFICATION_CODE, verificationCode.getIdentificationCode());
        }
        if (properties.isTemplateHasExpirationTime() && expirationTime != null && expirationTime > 0) {
            params.put(MSG_KEY_EXPIRATION_TIME_OF_SECONDS, String.valueOf(expirationTime));
            params.put(MSG_KEY_EXPIRATION_TIME_OF_MINUTES, String.valueOf(expirationTime / 60));
        }

        String type = null;
        if (verificationCodeTypeGenerate != null) {
            type = verificationCodeTypeGenerate.getType(phone, params);
        }
        if (type == null) {
            type = properties.getType();
        }

        NoticeData notice = new NoticeData();
        notice.setType(type);
        notice.setParams(params);

        if (noticeService.send(notice, phone) && newVerificationCode) {
            repository.save(verificationCode);
        }
    }

    @Override
    public boolean verify(String phone, String code, String identificationCode) {
        if (StringUtils.isAnyBlank(phone, code)) {
            return false;
        }

        phoneValidation(phone);

        VerificationCode verificationCode = repository.findOne(phone, identificationCode);

        if (verificationCode == null) {
            return false;
        }

        boolean verifyData = Objects.equals(verificationCode.getCode(), code);

        if (verifyData && properties.isDeleteByVerifySucceed()) {
            repository.delete(phone, identificationCode);
        }

        if (!verifyData && properties.isDeleteByVerifyFail()) {
            repository.delete(phone, identificationCode);
        }

        return verifyData;
    }

    private void phoneValidation(String phone) {
        if (!noticeService.phoneRegValidation(phone)) {
            throw new PhoneIsNullException();
        }
    }

}
