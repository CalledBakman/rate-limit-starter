package com.github.calledbakman.rate_limit_starter.enums;

import lombok.Getter;

@Getter
public enum PeriodType {
    SEC(1000), MIN(60000), HOUR(3600000);

    private long millis;

    PeriodType(long millis){
        this.millis = millis;
    }
}
