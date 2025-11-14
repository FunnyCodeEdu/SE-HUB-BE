package com.se.hub.modules.datainitializer;

import com.se.hub.common.constant.InitializerOrder;
import com.se.hub.modules.profile.constant.userlevel.UserLevelConstants;
import com.se.hub.modules.profile.entity.UserLevel;
import com.se.hub.modules.profile.enums.LevelEnums;
import com.se.hub.modules.profile.repository.UserLevelRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Order(InitializerOrder.LEVEL)
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LevelInitializer implements ApplicationRunner {
    UserLevelRepository userLevelRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("LevelInitializer_run_Starting level initialization");
        
        try {
            Set<UserLevel> levels = getLevels();
            int createdCount = 0;
            int skippedCount = 0;

            for (UserLevel level : levels) {
                if (!userLevelRepository.existsByLevel(level.getLevel())) {
                    userLevelRepository.save(level);
                    createdCount++;
                    log.debug("LevelInitializer_run_Created level: {} (min: {}, max: {})", 
                            level.getLevel(), level.getMinPoints(), level.getMaxPoints());
                } else {
                    skippedCount++;
                    log.debug("LevelInitializer_run_Level already exists, skipped: {}", level.getLevel());
                }
            }
            
            log.info("LevelInitializer_run_Level initialization completed. Created: {}, Skipped: {}, Total: {}", 
                    createdCount, skippedCount, levels.size());
        } catch (Exception e) {
            log.error("LevelInitializer_run_Error during level initialization", e);
            throw e;
        }
    }

    private Set<UserLevel> getLevels() {
        return Set.of(
                new UserLevel(LevelEnums.COPPER, UserLevelConstants.COPPER_MIN, UserLevelConstants.COPPER_MAX),
                new UserLevel(LevelEnums.SILVER, UserLevelConstants.SILVER_MIN, UserLevelConstants.SILVER_MAX),
                new UserLevel(LevelEnums.GOLD, UserLevelConstants.GOLD_MIN, UserLevelConstants.GOLD_MAX),
                new UserLevel(LevelEnums.PLATINUM, UserLevelConstants.PLATINUM_MIN, UserLevelConstants.PLATINUM_MAX),
                new UserLevel(LevelEnums.DIAMOND, UserLevelConstants.DIAMOND_MIN, UserLevelConstants.DIAMOND_MAX)
        );
    }
}
