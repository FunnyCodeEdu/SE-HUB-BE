package com.catsocute.japanlearn_hub.modules.lesson.service.api;

import com.catsocute.japanlearn_hub.modules.lesson.dto.request.AddContentToLessonRequest;

public interface BaseContentService {
    /**
     * add content for lesson
     * @author catsocute
     */
    void addContent(String lessonId, AddContentToLessonRequest request);
}
