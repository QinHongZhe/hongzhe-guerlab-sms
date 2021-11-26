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
package net.guerlab.sms.server.spring.autoconfigure;

import net.guerlab.loadbalancer.ILoadBalancer;
import net.guerlab.sms.core.domain.NoticeData;
import net.guerlab.sms.core.handler.SendHandler;
import net.guerlab.sms.server.loadbalancer.*;
import net.guerlab.sms.server.properties.SmsConfig;
import net.guerlab.sms.server.service.DefaultNoticeService;
import net.guerlab.sms.server.service.DefaultSendAsyncThreadPoolExecutor;
import net.guerlab.sms.server.service.NoticeService;
import net.guerlab.sms.server.service.SendAsyncThreadPoolExecutor;
import net.guerlab.sms.server.spring.properties.SmsAsyncProperties;
import net.guerlab.sms.server.spring.properties.SmsProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 短信服务配置
 *
 * @author guer
 */
@Configuration
@EnableConfigurationProperties({ SmsProperties.class, SmsAsyncProperties.class })
public class SmsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(NoticeService.class)
    public NoticeService noticeService(SmsProperties properties, SmsAsyncProperties asyncProperties,
            ILoadBalancer<SendHandler, NoticeData> smsSenderLoadbalancer,
            ObjectProvider<SendAsyncThreadPoolExecutor> executorProvider) {
        return new DefaultNoticeService(properties, asyncProperties, smsSenderLoadbalancer,
                executorProvider.getIfUnique());
    }

    /**
     * 构造发送异步处理线程池
     *
     * @param properties
     *         短信异步配置
     * @return 发送异步处理线程池
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = SmsAsyncProperties.PREFIX, name = "enable", havingValue = "true")
    public SendAsyncThreadPoolExecutor sendAsyncThreadPoolExecutor(SmsAsyncProperties properties) {
        return new DefaultSendAsyncThreadPoolExecutor(properties);
    }

    /**
     * 构造发送者负载均衡器
     *
     * @param properties
     *         短信配置
     * @return 发送者负载均衡器
     */
    @Bean
    @ConditionalOnMissingBean(SmsSenderLoadBalancer.class)
    public SmsSenderLoadBalancer smsSenderLoadBalancer(SmsConfig properties) {
        String type = properties.getLoadBalancerType();
        if (type == null) {
            return new RandomSmsLoadBalancer();
        }

        type = type.trim();

        if (RoundRobinSmsLoadBalancer.TYPE_NAME.equalsIgnoreCase(type)) {
            return new RoundRobinSmsLoadBalancer();
        } else if (WeightRandomSmsLoadBalancer.TYPE_NAME.equalsIgnoreCase(type)) {
            return new WeightRandomSmsLoadBalancer();
        } else if (WeightRoundRobinSmsLoadBalancer.TYPE_NAME.equalsIgnoreCase(type)) {
            return new WeightRoundRobinSmsLoadBalancer();
        } else {
            return new RandomSmsLoadBalancer();
        }
    }

    /**
     * 创建RestTemplate
     *
     * @return RestTemplate
     */
    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
