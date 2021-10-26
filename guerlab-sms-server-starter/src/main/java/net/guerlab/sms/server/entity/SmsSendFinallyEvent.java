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
package net.guerlab.sms.server.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.guerlab.sms.core.handler.SendHandler;
import org.springframework.context.ApplicationEvent;

import java.util.Collection;
import java.util.Map;

/**
 * 发送完成事件
 *
 * @author guer
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class SmsSendFinallyEvent extends ApplicationEvent {

    /**
     * 发送渠道
     */
    private final String sendChannel;

    /**
     * 电话号码列表
     */
    private final Collection<String> phones;

    /**
     * 类型
     */
    private final String type;

    /**
     * 参数列表
     */
    private final Map<String, String> params;

    public SmsSendFinallyEvent(SendHandler source, String sendChannel, Collection<String> phones, String type, Map<String, String> params) {
        super(source);
        this.sendChannel = sendChannel;
        this.phones = phones;
        this.type = type;
        this.params = params;
    }
}
