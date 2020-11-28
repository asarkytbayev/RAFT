package com.neu.project3.raft.util;

import org.springframework.context.ApplicationContext;

public final class ApplicationUtil {

    private ApplicationUtil() {}

    private static ApplicationContext appContext;

    public static void setContext(ApplicationContext context) {
        appContext = context;
    }

//    public static <T> T getBean(Class<T> type) {
//        return appContext.getBean(type);
//    }

    public static String getProperty(String key) {
        return appContext.getEnvironment().getProperty(key);
    }


}
