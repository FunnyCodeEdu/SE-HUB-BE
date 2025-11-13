package com.se.hub.modules.course.mapper;

import com.se.hub.modules.course.dto.request.CreateCourseRequest;
import com.se.hub.modules.course.dto.request.UpdateCourseRequest;
import com.se.hub.modules.course.dto.response.CourseResponse;
import com.se.hub.modules.course.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    Course toCourse(CreateCourseRequest request);
    CourseResponse toCourseResponse(Course course);
    Course updateCourseFromRequest(@MappingTarget Course course, UpdateCourseRequest request);
}
