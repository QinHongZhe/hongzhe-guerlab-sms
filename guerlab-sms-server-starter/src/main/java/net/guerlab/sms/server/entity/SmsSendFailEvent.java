package net.guerlab.sms.server.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.guerlab.sms.core.handler.SendHandler;
import org.springframework.context.ApplicationEvent;

import java.util.Collection;
import java.util.Map;

/**
 * 发送失败事件
 *
 * @author guer
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class SmsSendFailEvent extends ApplicationEvent {

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

    /**
     * 异常原因
     */
    private final Throwable cause;

    public SmsSendFailEvent(SendHandler source, String sendChannel, Collection<String> phones, String type,
            Map<String, String> params, Throwable cause) {
        super(source);
        this.sendChannel = sendChannel;
        this.phones = phones;
        this.type = type;
        this.params = params;
        this.cause = cause;
    }
}
