package com.shotaroi.medsafety.common;

import org.slf4j.MDC;

/**
 * Thread-local access to correlation ID (set by CorrelationIdFilter).
 */
public final class CorrelationIdHolder {

    private CorrelationIdHolder() {}

    public static String get() {
        return MDC.get(CorrelationIdFilter.MDC_KEY);
    }
}
