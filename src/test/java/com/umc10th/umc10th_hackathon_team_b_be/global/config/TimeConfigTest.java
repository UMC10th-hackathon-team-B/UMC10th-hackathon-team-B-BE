package com.umc10th.umc10th_hackathon_team_b_be.global.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class TimeConfigTest {

    @Test
    void clockUsesSeoulZone() {
        TimeConfig timeConfig = new TimeConfig();

        assertThat(timeConfig.clock().getZone()).isEqualTo(ZoneId.of("Asia/Seoul"));
    }
}