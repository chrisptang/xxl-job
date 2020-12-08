package com.xxl.job.admin.cat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.dianping.cat.Cat;

public class CatAppenderLogback extends UnsynchronizedAppenderBase<LoggingEvent> {

    @Override
    protected void append(LoggingEvent eventObject) {
        if (null != eventObject && eventObject.getLevel().isGreaterOrEqual(Level.ERROR)) {
            if (null != eventObject.getThrowableProxy() && eventObject.getThrowableProxy() instanceof ThrowableProxy) {
                ThrowableProxy throwableProxy = (ThrowableProxy) eventObject.getThrowableProxy();
                Cat.logError(eventObject.getMessage(), throwableProxy.getThrowable());
            } else {
                Cat.logError(eventObject.getMessage(), new Exception());
            }
        }
    }
}
