package com.catsocute.japanlearn_hub.modules.course.service.api;

import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import com.catsocute.japanlearn_hub.common.dto.response.PagingResponse;
import com.catsocute.japanlearn_hub.modules.course.dto.request.CreateCourseRequest;
import com.catsocute.japanlearn_hub.modules.course.dto.request.UpdateCourseRequest;
import com.catsocute.japanlearn_hub.modules.course.dto.response.CourseResponse;
import com.catsocute.japanlearn_hub.modules.course.entity.Course;

public interface CourseService {
    /**
     * create new course
     * @author catsocute
     */
    CourseResponse createCourse(CreateCourseRequest request);

    /**
     * get course by id
     * @author catsocute
     */
    CourseResponse getById(String courseId);
    /**
     * get courses by userId
     * @author catsocute
     */
    PagingResponse<CourseResponse> getCoursesByUserId(String userId, PagingRequest request);

    /**
     * get courses
     * @author catsocute
     */
    PagingResponse<CourseResponse> getCourses(PagingRequest request);

    /**
     * update course by id
     * @author catsocute
     */
    CourseResponse updateCoursesById(String courseId, UpdateCourseRequest request);

    /**
     * delete course by id
     * @author catsocute
     */
    void deleteCourseById(String courseId);
}
