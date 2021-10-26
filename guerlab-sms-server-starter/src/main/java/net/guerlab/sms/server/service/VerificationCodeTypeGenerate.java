package net.guerlab.sms.server.service;

import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * 验证码类型生成
 *
 * @author guer
 */
@FunctionalInterface
public interface VerificationCodeTypeGenerate {

    /**
     * 根据电话号码和参数列表生成验证码类型
     *
     * @param phone
     *         电话号码
     * @param params
     *         参数列表
     * @return 验证码类型
     */
    @Nullable
    String getType(String phone, Map<String, String> params);
}
