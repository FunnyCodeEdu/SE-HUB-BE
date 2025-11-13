package com.se.hub.modules.lesson.service.api;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.lesson.dto.request.CreateLessonRequest;
import com.se.hub.modules.lesson.dto.request.UpdateLessonRequest;
import com.se.hub.modules.lesson.dto.response.LessonResponse;

public interface LessonService {
    /**
     * create new lesson
     * @author catsocute
     */
    LessonResponse createLesson(String courseId, CreateLessonRequest request);

    /**
     * get lesson by id
     * @author catsocute
     */
    LessonResponse getById(String lessonId);

    /**
     * get lessons by type
     * @author catsocute
     */
    PagingResponse<LessonResponse> getLessonsByType(String type, PagingRequest request);

    /**
     * get lessons by course
     * @author catsocute
     */
    PagingResponse<LessonResponse> getLessonsByCourse(String courseId, PagingRequest request);

    /**
     * get lessons by parent lesson
     * @author catsocute
     */
    PagingResponse<LessonResponse> getLessonsByParent(String parentLessonId, PagingRequest request);

    /**
     * get lessons
     * @author catsocute
     */
    PagingResponse<LessonResponse> getLessons(PagingRequest request);

    /**
     * update lesson by id
     * @author catsocute
     */
    LessonResponse updateLessonById(String lessonId, UpdateLessonRequest request);

    /**
     * delete lesson by id
     * @author catsocute
     */
    void deleteLessonById(String lessonId);
}
