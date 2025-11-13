package com.catsocute.japanlearn_hub.modules.lesson.service.impl;

import com.catsocute.japanlearn_hub.common.constant.GlobalVariable;
import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import com.catsocute.japanlearn_hub.common.dto.response.PagingResponse;
import com.catsocute.japanlearn_hub.common.enums.ErrorCode;
import com.catsocute.japanlearn_hub.common.exception.AppException;
import com.catsocute.japanlearn_hub.common.utils.PagingUtil;
import com.catsocute.japanlearn_hub.modules.auth.utils.AuthUtils;
import com.catsocute.japanlearn_hub.modules.course.entity.Course;
import com.catsocute.japanlearn_hub.modules.course.repository.CourseRepository;
import com.catsocute.japanlearn_hub.modules.lesson.dto.request.CreateLessonRequest;
import com.catsocute.japanlearn_hub.modules.lesson.dto.request.UpdateLessonRequest;
import com.catsocute.japanlearn_hub.modules.lesson.dto.response.LessonResponse;
import com.catsocute.japanlearn_hub.modules.lesson.entity.Lesson;
import com.catsocute.japanlearn_hub.modules.lesson.enums.LessonType;
import com.catsocute.japanlearn_hub.modules.lesson.mapper.LessonMapper;
import com.catsocute.japanlearn_hub.modules.lesson.repository.LessonRepository;
import com.catsocute.japanlearn_hub.modules.lesson.service.api.LessonService;
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
public class LessonServiceImpl implements LessonService {
    LessonRepository lessonRepository;
    LessonMapper lessonMapper;
    CourseRepository courseRepository;

    @Override
    public LessonResponse createLesson(String courseId, CreateLessonRequest request) {
        Lesson lesson = lessonMapper.toLesson(request);
        
        // Set parent lesson if provided
        if (request.getParentLessonId() != null && !request.getParentLessonId().isEmpty()) {
            Lesson parentLesson = lessonRepository.findById(request.getParentLessonId())
                    .orElseThrow(() -> {
                        log.error("Parent lesson id {} not found ", request.getParentLessonId());
                        return new AppException(ErrorCode.LESSON_PARENT_INVALID);
                    });
            lesson.setParentLesson(parentLesson);
        }
        
        // Set course if provided
        if (courseId != null) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> {
                        log.error("Course id {} not found!", courseId);
                        return new AppException(ErrorCode.COURSE_NOT_FOUND);
                    });
            lesson.setCourse(course);
        }
        
        String userId = AuthUtils.getCurrentUserId();
        lesson.setCreatedBy(userId);
        lesson.setUpdateBy(userId);

        return lessonMapper.toLessonResponse(lessonRepository.save(lesson));
    }

    @Override
    public LessonResponse getById(String lessonId) {
        return lessonMapper.toLessonResponse(lessonRepository.findById(lessonId)
                .orElseThrow(() -> {
                    log.error("Lesson id {} not found ", lessonId);
                    return new AppException(ErrorCode.LESSON_NOT_FOUND);
                }));
    }

    @Override
    public PagingResponse<LessonResponse> getLessonsByType(String type, PagingRequest request) {
        LessonType lessonType;
        try {
            lessonType = LessonType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid lesson type: {}", type);
            throw new AppException(ErrorCode.LESSON_TYPE_INVALID);
        }

        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Lesson> lessons = lessonRepository.findByType(lessonType, pageable);

        return PagingResponse.<LessonResponse>builder()
                .currentPage(lessons.getNumber())
                .totalPages(lessons.getTotalPages())
                .pageSize(lessons.getSize())
                .totalElement(lessons.getTotalElements())
                .data(lessons.getContent().stream()
                        .map(lessonMapper::toLessonResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public PagingResponse<LessonResponse> getLessonsByCourse(String courseId, PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Lesson> lessons = lessonRepository.findByCourseId(courseId, pageable);

        return PagingResponse.<LessonResponse>builder()
                .currentPage(lessons.getNumber())
                .totalPages(lessons.getTotalPages())
                .pageSize(lessons.getSize())
                .totalElement(lessons.getTotalElements())
                .data(lessons.getContent().stream()
                        .filter(lesson -> lesson.getParentLesson() == null) // get only parent lesson
                        .map(lessonMapper::toLessonResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public PagingResponse<LessonResponse> getLessonsByParent(String parentLessonId, PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Lesson> lessons = lessonRepository.findByParentLessonId(parentLessonId, pageable);

        return PagingResponse.<LessonResponse>builder()
                .currentPage(lessons.getNumber())
                .totalPages(lessons.getTotalPages())
                .pageSize(lessons.getSize())
                .totalElement(lessons.getTotalElements())
                .data(lessons.getContent().stream()
                        .map(lessonMapper::toLessonResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public PagingResponse<LessonResponse> getLessons(PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Lesson> lessons = lessonRepository.findAll(pageable);

        return PagingResponse.<LessonResponse>builder()
                .currentPage(lessons.getNumber())
                .totalPages(lessons.getTotalPages())
                .pageSize(lessons.getSize())
                .totalElement(lessons.getTotalElements())
                .data(lessons.getContent().stream()
                        .map(lessonMapper::toLessonResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public LessonResponse updateLessonById(String lessonId, UpdateLessonRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> {
                    log.error(" Lesson id {} not found", lessonId);
                    return new AppException(ErrorCode.LESSON_NOT_FOUND);
                });

        lessonMapper.updateLessonFromRequest(lesson, request);

        return lessonMapper.toLessonResponse(lessonRepository.save(lesson));
    }

    @Override
    public void deleteLessonById(String lessonId) {
        lessonRepository.deleteById(lessonId);
    }
}
