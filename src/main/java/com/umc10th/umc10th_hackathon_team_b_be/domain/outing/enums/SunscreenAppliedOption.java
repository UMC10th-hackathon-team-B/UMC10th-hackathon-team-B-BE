package com.umc10th.umc10th_hackathon_team_b_be.domain.outing.enums;

public enum SunscreenAppliedOption {
    NONE(null),
    BEFORE_120_MINUTES(120),
    BEFORE_60_MINUTES(60),
    BEFORE_30_MINUTES(30),
    BEFORE_15_MINUTES(15),
    BEFORE_5_MINUTES(5);

    private final Integer minutesBeforeStart;

    SunscreenAppliedOption(Integer minutesBeforeStart) {
        this.minutesBeforeStart = minutesBeforeStart;
    }

    public Integer getMinutesBeforeStart() {
        return minutesBeforeStart;
    }

    public boolean hasAppliedTime() {
        return minutesBeforeStart != null;
    }
}
