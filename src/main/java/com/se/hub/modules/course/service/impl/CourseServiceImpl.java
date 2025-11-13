package com.se.hub.modules.course.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.course.dto.request.CreateCourseRequest;
import com.se.hub.modules.course.dto.request.UpdateCourseRequest;
import com.se.hub.modules.course.dto.response.CourseResponse;
import com.se.hub.modules.course.entity.Course;
import com.se.hub.modules.course.mapper.CourseMapper;
import com.se.hub.modules.course.repository.CourseRepository;
import com.se.hub.modules.course.service.api.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseServiceImpl implements CourseService {
    CourseRepository courseRepository;
    CourseMapper courseMapper;

    @Override
    public CourseResponse createCourse(CreateCourseRequest request) {
        if(courseRepository.existsByName(request.getName())) {
            log.error("Course name {} already exists", request.getName());
            throw new AppException(ErrorCode.DATA_EXISTED);
        }
        Course course = courseMapper.toCourse(request);
        String userId = AuthUtils.getCurrentUserId();
        course.setCreatedBy(userId);
        course.setUpdateBy(userId);

        return courseMapper.toCourseResponse(courseRepository.save(course));
    }

    @Override
    public CourseResponse getById(String courseId) {
        return courseMapper.toCourseResponse(courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.error("Course id {} not found", courseId);
                    return new AppException(ErrorCode.COURSE_NOT_FOUND);
                }));
    }

    @Override
    public PagingResponse<CourseResponse> getCoursesByUserId(String userId, PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Course> courses = courseRepository.findAllByUsers_Id(userId, pageable);

        return PagingResponse.<CourseResponse>builder()
                .currentPage(courses.getNumber())
                .totalPages(courses.getTotalPages())
                .pageSize(courses.getSize())
                .totalElement(courses.getTotalElements())
                .data(courses.getContent().stream()
                        .map(courseMapper::toCourseResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public PagingResponse<CourseResponse> getCourses(PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Course> courses = courseRepository.findAll(pageable);

        return PagingResponse.<CourseResponse>builder()
                .currentPage(courses.getNumber())
                .totalPages(courses.getTotalPages())
                .pageSize(courses.getSize())
                .totalElement(courses.getTotalElements())
                .data(courses.getContent().stream()
                        .map(courseMapper::toCourseResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public CourseResponse updateCoursesById(String courseId, UpdateCourseRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        course = courseMapper.updateCourseFromRequest(course, request);

        return courseMapper.toCourseResponse(courseRepository.save(course));
    }

    @Override
    public void deleteCourseById(String courseId) {
        courseRepository.deleteById(courseId);
    }
}
