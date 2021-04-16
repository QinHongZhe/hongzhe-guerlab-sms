package net.guerlab.sms.server.handler;

import net.guerlab.sms.server.entity.SmsSendSuccessEvent;
import org.springframework.context.ApplicationListener;

/**
 * 发送成功事件监听接口
 *
 * @author guer
 */
public interface SmsSendSuccessEventListener extends ApplicationListener<SmsSendSuccessEvent> {}
