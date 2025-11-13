package com.catsocute.japanlearn_hub.modules.profile.service.api;

public interface ProfileProgressService {
    void updatePoints(int pointDelta);
    void updateExamsDone();
    void updateCmtCount();
    void updateDocsUploaded();
    void updatePostsUploaded();
    void updatePostShared();
    void updateLevel(String userId);
    void updateAchievements(String userId);
}
