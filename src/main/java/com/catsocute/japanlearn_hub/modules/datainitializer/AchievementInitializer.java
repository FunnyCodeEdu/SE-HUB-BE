package com.catsocute.japanlearn_hub.modules.datainitializer;

import com.catsocute.japanlearn_hub.common.constant.InitializerOrder;
import com.catsocute.japanlearn_hub.modules.profile.entity.Achievement;
import com.catsocute.japanlearn_hub.modules.profile.repository.AchievementRepository;
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

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("Initializing achievements...");

        List<Achievement> achievements = loadAchievementsFromJson();

        for (Achievement achievement : achievements) {
            if (!achievementRepository.existsByAchievementType(achievement.getAchievementType())) {
                achievementRepository.save(achievement);
                log.info("Created achievement [{}]", achievement.getAchievementType());
            }
        }

        log.info("Achievements initialization completed!");
    }

    private List<Achievement> loadAchievementsFromJson() throws IOException {
        ClassPathResource resource = new ClassPathResource("/masterdata/AchievementMasterData.json");
        try (InputStream inputStream = resource.getInputStream()) {
            return Arrays.asList(objectMapper.readValue(inputStream, Achievement[].class));
        }
    }
}
