package com.se.hub.modules.gamification.repository;

import com.se.hub.modules.gamification.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeasonRepository extends JpaRepository<Season, String> {
}


