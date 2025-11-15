package com.se.hub.modules.course.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.course.dto.request.CreateCourseRequest;
import com.se.hub.modules.course.dto.request.UpdateCourseRequest;
import com.se.hub.modules.course.dto.response.CourseResponse;
import com.se.hub.modules.course.entity.Course;
import com.se.hub.modules.interaction.dto.response.ReactionInfo;
import com.se.hub.modules.interaction.enums.TargetType;
import com.se.hub.modules.interaction.service.api.ReactionService;
import com.se.hub.modules.course.exception.CourseErrorCode;
import com.se.hub.modules.course.mapper.CourseMapper;
import com.se.hub.modules.course.repository.CourseRepository;
import com.se.hub.modules.course.service.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseServiceImpl implements CourseService {
    CourseRepository courseRepository;
    CourseMapper courseMapper;
    ReactionService reactionService;

    private PagingResponse<CourseResponse> buildPagingResponse(Page<Course> courses) {
        List<Course> courseList = courses.getContent();
        String currentUserId = AuthUtils.getCurrentUserId();
        
        // Batch check reactions for all courses
        List<String> courseIds = courseList.stream().map(Course::getId).toList();
        Map<String, ReactionInfo> reactionsMap = reactionService
                .getReactionsForTargets(TargetType.COURSE, courseIds, currentUserId);
        
        return PagingResponse.<CourseResponse>builder()
                .currentPage(courses.getNumber())
                .totalPages(courses.getTotalPages())
                .pageSize(courses.getSize())
                .totalElement(courses.getTotalElements())
                .data(courseList.stream()
                        .map(course -> {
                            CourseResponse response = courseMapper.toCourseResponse(course);
                            ReactionInfo reactionInfo = reactionsMap.getOrDefault(
                                    course.getId(),
                                    ReactionInfo.builder().userReacted(false).type(null).build()
                            );
                            response.setReactions(reactionInfo);
                            return response;
                        })
                        .toList()
                )
                .build();
    }

    @Override
    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request) {
        log.debug("CourseService_createCourse_Creating new course for user: {}", AuthUtils.getCurrentUserId());
        String userId = AuthUtils.getCurrentUserId();

        if (courseRepository.existsByName(request.getName())) {
            log.error("CourseService_createCourse_Course name {} already exists", request.getName());
            throw CourseErrorCode.COURSE_NAME_EXISTED.toException();
        }

        Course course = courseMapper.toCourse(request);
        course.setCreatedBy(userId);
        course.setUpdateBy(userId);

        CourseResponse response = courseMapper.toCourseResponse(courseRepository.save(course));
        log.debug("CourseService_createCourse_Course created successfully with id: {}", response.getId());
        return response;
    }

    @Override
    public CourseResponse getById(String courseId) {
        log.debug("CourseService_getById_Fetching course with id: {}", courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.error("CourseService_getById_Course not found with id: {}", courseId);
                    return CourseErrorCode.COURSE_NOT_FOUND.toException();
                });
        
        CourseResponse response = courseMapper.toCourseResponse(course);
        String currentUserId = AuthUtils.getCurrentUserId();
        Map<String, ReactionInfo> reactionsMap = reactionService
                .getReactionsForTargets(TargetType.COURSE, List.of(courseId), currentUserId);
        ReactionInfo reactionInfo = reactionsMap.getOrDefault(
                courseId,
                ReactionInfo.builder().userReacted(false).type(null).build()
        );
        response.setReactions(reactionInfo);
        return response;
    }

    @Override
    public PagingResponse<CourseResponse> getCoursesByUserId(String userId, PagingRequest request) {
        log.debug("CourseService_getCoursesByUserId_Fetching courses for user: {} with page: {}, size: {}", 
                userId, request.getPage(), request.getPageSize());
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Course> courses = courseRepository.findAllByUsers_Id(userId, pageable);
        log.debug("CourseService_getCoursesByUserId_Found {} courses for user {}", courses.getTotalElements(), userId);
        return buildPagingResponse(courses);
    }

    @Override
    public PagingResponse<CourseResponse> getCourses(PagingRequest request) {
        log.debug("CourseService_getCourses_Fetching courses with page: {}, size: {}", 
                request.getPage(), request.getPageSize());
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Course> courses = courseRepository.findAll(pageable);
        log.debug("CourseService_getCourses_Found {} courses", courses.getTotalElements());
        return buildPagingResponse(courses);
    }

    @Override
    @Transactional
    public CourseResponse updateCoursesById(String courseId, UpdateCourseRequest request) {
        log.debug("CourseService_updateCoursesById_Updating course with id: {}", courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.error("CourseService_updateCoursesById_Course not found with id: {}", courseId);
                    return CourseErrorCode.COURSE_NOT_FOUND.toException();
                });

        course = courseMapper.updateCourseFromRequest(course, request);
        course.setUpdateBy(AuthUtils.getCurrentUserId());

        CourseResponse response = courseMapper.toCourseResponse(courseRepository.save(course));
        log.debug("CourseService_updateCoursesById_Course updated successfully with id: {}", courseId);
        return response;
    }

    @Override
    @Transactional
    public void deleteCourseById(String courseId) {
        log.debug("CourseService_deleteCourseById_Deleting course with id: {}", courseId);
        if (courseId == null || courseId.isBlank()) {
            log.error("CourseService_deleteCourseById_Course ID is required");
            throw CourseErrorCode.COURSE_ID_REQUIRED.toException();
        }

        if (!courseRepository.existsById(courseId)) {
            log.error("CourseService_deleteCourseById_Course not found with id: {}", courseId);
            throw CourseErrorCode.COURSE_NOT_FOUND.toException();
        }

        courseRepository.deleteById(courseId);
        log.debug("CourseService_deleteCourseById_Course deleted successfully with id: {}", courseId);
    }
}
