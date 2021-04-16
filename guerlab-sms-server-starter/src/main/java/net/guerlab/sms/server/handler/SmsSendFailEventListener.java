package net.guerlab.sms.server.handler;

import net.guerlab.sms.server.entity.SmsSendFailEvent;
import org.springframework.context.ApplicationListener;

/**
 * 发送失败事件监听接口
 *
 * @author guer
 */
public interface SmsSendFailEventListener extends ApplicationListener<SmsSendFailEvent> {}
