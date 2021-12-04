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
 * 短信配置
 *
 * @author guer
 */
@Data
public class SmsConfig {

    /**
     * 手机号码正则规则
     */
    private String reg;

    /**
     * 负载均衡类型
     * <p>
     * 可选值:
     * Random、RoundRobin、WeightRandom、WeightRoundRobin，
     * 默认:
     * Random
     */
    private String loadBalancerType = "Random";
}
