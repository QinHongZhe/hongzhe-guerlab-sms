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
package net.guerlab.sms.jpush;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collection;

/**
 * 接收者列表
 *
 * @author guer
 */
@Data
public class MultiRecipient {

    /**
     * 签名ID
     */
    @JsonProperty("sign_id")
    private Integer signId;

    /**
     * 模板ID
     */
    @JsonProperty("temp_id")
    private Integer tempId;

    /**
     * 标签
     */
    private String tag;

    /**
     * 接收者列表
     */
    private Collection<Recipient> recipients;
}
