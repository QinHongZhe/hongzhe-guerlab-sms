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
package net.guerlab.sms.server.handler;

import net.guerlab.sms.server.entity.SmsSendSuccessEvent;
import org.springframework.context.ApplicationListener;

/**
 * 发送成功事件监听接口
 *
 * @author guer
 */
public interface SmsSendSuccessEventListener extends ApplicationListener<SmsSendSuccessEvent> {}
