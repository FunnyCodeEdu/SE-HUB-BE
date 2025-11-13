package com.se.hub.modules.lesson.service.api;

import com.se.hub.modules.lesson.dto.request.AddContentToLessonRequest;

public interface BaseContentService {
    /**
     * add content for lesson
     * @author catsocute
     */
    void addContent(String lessonId, AddContentToLessonRequest request);
}
