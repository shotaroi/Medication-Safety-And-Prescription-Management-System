package com.shotaroi.medsafety.domain.exception;

public class MaxDailyDoseExceededException extends DomainException {

    private final int dailyDoseMg;
    private final int maxDailyDoseMg;

    public MaxDailyDoseExceededException(int dailyDoseMg, int maxDailyDoseMg) {
        super("Daily dose " + dailyDoseMg + " mg exceeds maximum allowed " + maxDailyDoseMg + " mg");
        this.dailyDoseMg = dailyDoseMg;
        this.maxDailyDoseMg = maxDailyDoseMg;
    }

    public int getDailyDoseMg() {
        return dailyDoseMg;
    }

    public int getMaxDailyDoseMg() {
        return maxDailyDoseMg;
    }
}
