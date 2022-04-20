package com.github.bwly.rpc.core.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ThreadFactoryUtils {
    /**
     * 创建 ThreadFactory 。如果threadNamePrefix不为空则使用自建ThreadFactory，否则使用defaultThreadFactory
     *
     * @param prefixName 作为创建的线程名字的前缀
     * @param daemon           指定是否为 Daemon Thread(守护线程)
     * @return ThreadFactory
     */
    public static ThreadFactory createThreadFactory(String prefixName, boolean daemon) {
        if (prefixName != null) {
            if (daemon) {
                return new ThreadFactoryBuilder()
                        .setNameFormat(prefixName + "-%d")
                        .setDaemon(daemon).build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(prefixName + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }

    public static ThreadFactory createThreadFactory(String prefixName) {
        return createThreadFactory(prefixName, false);
    }
}
