package com.se.hub.modules.course.service;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.course.dto.request.CreateCourseRequest;
import com.se.hub.modules.course.dto.request.UpdateCourseRequest;
import com.se.hub.modules.course.dto.response.CourseResponse;

public interface CourseService {

    CourseResponse createCourse(CreateCourseRequest request);
    CourseResponse getById(String courseId);
    PagingResponse<CourseResponse> getCoursesByUserId(String userId, PagingRequest request);
    PagingResponse<CourseResponse> getCourses(PagingRequest request);
    CourseResponse updateCoursesById(String courseId, UpdateCourseRequest request);
    void deleteCourseById(String courseId);
}

