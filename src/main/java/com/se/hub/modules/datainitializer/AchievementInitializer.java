package com.se.hub.modules.datainitializer;

import com.se.hub.common.constant.InitializerOrder;
import com.se.hub.modules.profile.entity.Achievement;
import com.se.hub.modules.profile.repository.AchievementRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Order(InitializerOrder.ACHIEVEMENT)
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AchievementInitializer implements ApplicationRunner {
    AchievementRepository achievementRepository;
    ObjectMapper objectMapper;

    private static final String ACHIEVEMENT_MASTER_DATA_PATH = "/masterdata/AchievementMasterData.json";

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("AchievementInitializer_run_Starting achievement initialization");
        
        try {
            List<Achievement> achievements = loadAchievementsFromJson();
            
            if (achievements == null || achievements.isEmpty()) {
                log.warn("AchievementInitializer_run_No achievements found in master data file");
                return;
            }
            
            int createdCount = 0;
            int skippedCount = 0;

            for (Achievement achievement : achievements) {
                if (!achievementRepository.existsByAchievementType(achievement.getAchievementType())) {
                    achievementRepository.save(achievement);
                    createdCount++;
                    log.debug("AchievementInitializer_run_Created achievement: {}", achievement.getAchievementType());
                } else {
                    skippedCount++;
                    log.debug("AchievementInitializer_run_Achievement already exists, skipped: {}", achievement.getAchievementType());
                }
            }
            
            log.info("AchievementInitializer_run_Achievement initialization completed. Created: {}, Skipped: {}, Total: {}", 
                    createdCount, skippedCount, achievements.size());
        } catch (IOException e) {
            log.error("AchievementInitializer_run_Error loading achievements from JSON file: {}", ACHIEVEMENT_MASTER_DATA_PATH, e);
            throw new RuntimeException("Failed to load achievements from master data file", e);
        } catch (Exception e) {
            log.error("AchievementInitializer_run_Error during achievement initialization", e);
            throw e;
        }
    }

    /**
     * Load achievements from JSON master data file
     * 
     * @return List of achievements to initialize
     * @throws IOException if file cannot be read or parsed
     */
    private List<Achievement> loadAchievementsFromJson() throws IOException {
        log.debug("AchievementInitializer_loadAchievementsFromJson_Loading achievements from: {}", ACHIEVEMENT_MASTER_DATA_PATH);
        ClassPathResource resource = new ClassPathResource(ACHIEVEMENT_MASTER_DATA_PATH);
        
        if (!resource.exists()) {
            log.error("AchievementInitializer_loadAchievementsFromJson_Master data file not found: {}", ACHIEVEMENT_MASTER_DATA_PATH);
            throw new IOException("Achievement master data file not found: " + ACHIEVEMENT_MASTER_DATA_PATH);
        }
        
        try (InputStream inputStream = resource.getInputStream()) {
            Achievement[] achievements = objectMapper.readValue(inputStream, Achievement[].class);
            log.debug("AchievementInitializer_loadAchievementsFromJson_Loaded {} achievements from file", achievements.length);
            return Arrays.asList(achievements);
        }
    }
}
