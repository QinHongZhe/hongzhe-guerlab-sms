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
package net.guerlab.sms.server.spring.webmvc;

import net.guerlab.sms.server.spring.autoconfigure.SmsAutoConfiguration;
import net.guerlab.sms.server.spring.autoconfigure.VerificationCodeAutoConfiguration;
import net.guerlab.sms.server.spring.webmvc.autoconfigure.SmsWebmvcConfiguration;
import net.guerlab.sms.server.spring.webmvc.autoconfigure.SmsWebmvcPathConfiguration;
import net.guerlab.sms.server.spring.webmvc.controller.SmsController;
import net.guerlab.sms.server.spring.webmvc.properties.SmsWebmvcProperties;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Optional;

/**
 * 自动配置测试
 *
 * @author guer
 */
public class AutoConfigurationTest {

    private AnnotationConfigApplicationContext context;

    @Before
    public void setUp() {
        context = new AnnotationConfigApplicationContext();
        context.register(SmsAutoConfiguration.class);
        context.register(VerificationCodeAutoConfiguration.class);
    }

    @After
    public void tearDown() {
        Optional.of(context).ifPresent(AnnotationConfigApplicationContext::close);
    }

    @Test
    public void controller() {
        TestPropertyValues.of(SmsWebmvcProperties.PREFIX + ".enable=true").applyTo(context);
        context.register(RequestMappingHandlerMapping.class);
        context.register(SmsWebmvcConfiguration.class);
        context.register(SmsWebmvcPathConfiguration.class);
        context.refresh();
        Assert.assertNotNull(context.getBean(SmsController.class));
    }
}
