/*
 * Copyright 2018-2022 guerlab.net and other contributors.
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

package net.guerlab.sms.server.service;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import net.guerlab.sms.server.properties.SmsAsyncConfig;

/**
 * 默认发送异步处理线程池实现.
 *
 * @author guer
 */
public class DefaultSendAsyncThreadPoolExecutor extends AbstractSendAsyncThreadPoolExecutor {

	private final ThreadPoolExecutor executor;

	public DefaultSendAsyncThreadPoolExecutor(SmsAsyncConfig properties) {
		super(properties);
		executor = new ThreadPoolExecutor(properties.getCorePoolSize(), properties.getMaximumPoolSize(),
				properties.getKeepAliveTime(), properties.getUnit(),
				new LinkedBlockingQueue<>(properties.getQueueCapacity()), new DefaultThreadFactory(),
				buildRejectedExecutionHandler(properties.getRejectPolicy()));
	}

	@Override
	protected void submit0(Runnable command) {
		executor.execute(command);
	}
}
