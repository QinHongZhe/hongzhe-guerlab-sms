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
package net.guerlab.sms.server.service;

import net.guerlab.sms.server.properties.RejectPolicy;
import net.guerlab.sms.server.properties.SmsAsyncProperties;
import org.springframework.lang.Nullable;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 抽象发送异步处理线程池
 *
 * @author guer
 */
public abstract class AbstractSendAsyncThreadPoolExecutor implements SendAsyncThreadPoolExecutor {

    protected final SmsAsyncProperties properties;

    public AbstractSendAsyncThreadPoolExecutor(SmsAsyncProperties properties) {
        this.properties = properties;
    }

    /**
     * 根据拒绝策略构造拒绝处理程序
     *
     * @param type
     *         拒绝策略
     * @return 拒绝处理程序
     */
    protected static RejectedExecutionHandler buildRejectedExecutionHandler(@Nullable RejectPolicy type) {
        if (type == null) {
            return new ThreadPoolExecutor.AbortPolicy();
        }
        switch (type) {
            case Caller:
                return new ThreadPoolExecutor.CallerRunsPolicy();
            case Discard:
                return new ThreadPoolExecutor.DiscardPolicy();
            case DiscardOldest:
                return new ThreadPoolExecutor.DiscardOldestPolicy();
            default:
                return new ThreadPoolExecutor.AbortPolicy();
        }
    }

    /**
     * 提交异步任务
     *
     * @param command
     *         待执行任务
     */
    @Override
    public final void submit(Runnable command) {
        submit0(command);
    }

    /**
     * 提交异步任务
     *
     * @param command
     *         待执行任务
     */
    protected abstract void submit0(Runnable command);

    /**
     * 默认线程工厂
     */
    protected static class DefaultThreadFactory implements ThreadFactory {

        private final ThreadGroup group;

        private final AtomicInteger threadNumber = new AtomicInteger(1);

        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "sms-send-async-thread-";
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(group, runnable, namePrefix + threadNumber.getAndIncrement(), 0);
            thread.setDaemon(false);
            thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        }
    }
}
