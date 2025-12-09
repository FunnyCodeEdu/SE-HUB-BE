package com.se.hub.modules.gamification.entity;

import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.gamification.enums.SeasonStatus;

import java.time.Instant;
import java.util.List;

public class Season extends BaseEntity {
    String name;
    Instant startAt;
    Instant endAt;
    SeasonStatus status;
    List<Reward> rewards;
}
