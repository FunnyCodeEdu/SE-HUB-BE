package com.se.hub.modules.lesson.service.api;

import com.se.hub.modules.lesson.enums.LessonType;

public interface ContentServiceFactory {
    /**
     * add content for lesson
     * @author catsocute
     */
    BaseContentService getService(LessonType lessonType);
}
