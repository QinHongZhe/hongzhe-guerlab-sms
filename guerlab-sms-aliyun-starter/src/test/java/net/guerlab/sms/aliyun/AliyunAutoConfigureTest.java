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
package net.guerlab.sms.aliyun;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.guerlab.sms.server.loadbalancer.RandomSmsLoadBalancer;
import net.guerlab.sms.server.spring.autoconfigure.SmsAutoConfiguration;
import net.guerlab.sms.server.spring.autoconfigure.VerificationCodeAutoConfiguration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Optional;

public class AliyunAutoConfigureTest {

    private AnnotationConfigApplicationContext context;

    @Before
    public void setUp() {
        context = new AnnotationConfigApplicationContext();
        context.registerBean("objectMapper", ObjectMapper.class);
        context.registerBean("smsSenderLoadbalancer", RandomSmsLoadBalancer.class);
        context.register(SmsAutoConfiguration.class);
        context.register(VerificationCodeAutoConfiguration.class);
    }

    @After
    public void tearDown() {
        Optional.of(context).ifPresent(AnnotationConfigApplicationContext::close);
    }

    @Test
    public void testConditionalOnPropertyNull() {
        context.register(AliyunAutoConfigure.class);
        context.refresh();
        Assert.assertNotNull(context.getBean(AliyunSendHandler.class));
    }

    @Test
    public void testConditionalOnPropertyEmpty() {
        TestPropertyValues.of("sms.aliyun.enable=").applyTo(context);
        context.register(AliyunAutoConfigure.class);
        context.refresh();
        Assert.assertNotNull(context.getBean(AliyunSendHandler.class));
    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void testConditionalOnPropertyFalse() {
        TestPropertyValues.of("sms.aliyun.enable=false").applyTo(context);
        context.register(AliyunAutoConfigure.class);
        context.refresh();
        context.getBean(AliyunSendHandler.class);
    }

    @Test
    public void testConditionalOnPropertyTrue() {
        TestPropertyValues.of("sms.aliyun.enable=true").applyTo(context);
        context.register(AliyunAutoConfigure.class);
        context.refresh();
        Assert.assertNotNull(context.getBean(AliyunSendHandler.class));
    }
}
