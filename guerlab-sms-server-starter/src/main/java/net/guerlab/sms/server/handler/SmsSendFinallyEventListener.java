package net.guerlab.sms.server.handler;

import net.guerlab.sms.server.entity.SmsSendFinallyEvent;
import org.springframework.context.ApplicationListener;

/**
 * 发送完成事件监听接口
 *
 * @author guer
 */
public interface SmsSendFinallyEventListener extends ApplicationListener<SmsSendFinallyEvent> {}
