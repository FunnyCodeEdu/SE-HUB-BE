package com.se.hub.modules.search.service.impl;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.request.SortRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.blog.dto.response.BlogResponse;
import com.se.hub.modules.blog.service.BlogService;
import com.se.hub.modules.exam.dto.response.ExamResponse;
import com.se.hub.modules.exam.service.ExamService;
import com.se.hub.modules.profile.dto.response.ProfileResponse;
import com.se.hub.modules.profile.service.api.ProfileService;
import com.se.hub.modules.search.dto.request.SearchRequest;
import com.se.hub.modules.search.dto.response.SearchResponse;
import com.se.hub.modules.search.dto.response.SearchUserResponse;
import com.se.hub.modules.search.enums.SearchTarget;
import com.se.hub.modules.search.exception.SearchErrorCode;
import com.se.hub.modules.search.service.SearchService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SearchServiceImpl implements SearchService {

    BlogService blogService;
    ExamService examService;
    ProfileService profileService;

    @Override
    @Transactional(readOnly = true)
    public SearchResponse search(SearchRequest request) {
        if (request == null) {
            throw SearchErrorCode.KEYWORD_REQUIRED.toException();
        }

        PagingRequest pagingRequest = request.getPagingRequest();
        if (pagingRequest == null) {
            throw new AppException(ErrorCode.DATA_INVALID);
        }

        String keyword = sanitizeKeyword(request.getKeyword());
        Set<SearchTarget> targets = normalizeTargets(request.getTargets());

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<PagingResponse<BlogResponse>> blogFuture = null;
            CompletableFuture<PagingResponse<ExamResponse>> examFuture = null;
            CompletableFuture<PagingResponse<SearchUserResponse>> userFuture = null;
            List<CompletableFuture<?>> futures = new ArrayList<>();

            if (targets.contains(SearchTarget.BLOG)) {
                PagingRequest blogPaging = clonePagingRequest(pagingRequest);
                blogFuture = CompletableFuture.supplyAsync(
                        () -> blogService.searchBlogs(keyword, blogPaging), executor);
                futures.add(blogFuture);
            }

            if (targets.contains(SearchTarget.EXAM)) {
                PagingRequest examPaging = clonePagingRequest(pagingRequest);
                examFuture = CompletableFuture.supplyAsync(
                        () -> examService.searchExams(keyword, examPaging), executor);
                futures.add(examFuture);
            }

            if (targets.contains(SearchTarget.USER)) {
                PagingRequest userPaging = clonePagingRequest(pagingRequest);
                userFuture = CompletableFuture.supplyAsync(
                        () -> mapToUserSearchResponse(profileService.searchProfiles(keyword, userPaging)), executor);
                futures.add(userFuture);
            }

            waitForAll(futures);

            return SearchResponse.builder()
                    .blogs(joinFuture(blogFuture))
                    .exams(joinFuture(examFuture))
                    .users(joinFuture(userFuture))
                    .build();
        }
    }

    private void waitForAll(List<CompletableFuture<?>> futures) {
        CompletableFuture<?>[] futureArray = futures.stream()
                .filter(Objects::nonNull)
                .toArray(CompletableFuture[]::new);
        if (futureArray.length == 0) {
            throw SearchErrorCode.TARGET_REQUIRED.toException();
        }
        try {
            CompletableFuture.allOf(futureArray).join();
        } catch (CompletionException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new AppException(ErrorCode.SERVER_UNCATEGORIZED_EXCEPTION);
        }
    }

    private String sanitizeKeyword(String keyword) {
        String sanitized = keyword == null ? "" : keyword.trim();
        if (sanitized.isBlank()) {
            log.error("SearchService_sanitizeKeyword_Keyword is required");
            throw SearchErrorCode.KEYWORD_REQUIRED.toException();
        }
        return sanitized;
    }

    private Set<SearchTarget> normalizeTargets(Set<SearchTarget> targets) {
        if (targets == null || targets.isEmpty()) {
            return EnumSet.copyOf(SearchTarget.defaultTargets());
        }
        return EnumSet.copyOf(targets);
    }

    private PagingRequest clonePagingRequest(PagingRequest source) {
        SortRequest sortRequest = source.getSortRequest();
        SortRequest clonedSort = sortRequest == null ? null : SortRequest.builder()
                .direction(sortRequest.getDirection())
                .field(sortRequest.getField())
                .build();

        return PagingRequest.builder()
                .page(source.getPage())
                .pageSize(source.getPageSize())
                .sortRequest(clonedSort)
                .build();
    }

    private PagingResponse<SearchUserResponse> mapToUserSearchResponse(PagingResponse<ProfileResponse> response) {
        if (response == null) {
            return null;
        }
        List<SearchUserResponse> users = response.getData() == null
                ? List.of()
                : response.getData().stream()
                .map(this::mapProfileToSearchUser)
                .toList();

        return PagingResponse.<SearchUserResponse>builder()
                .currentPage(response.getCurrentPage())
                .pageSize(response.getPageSize())
                .totalPages(response.getTotalPages())
                .totalElement(response.getTotalElement())
                .data(users)
                .build();
    }

    private SearchUserResponse mapProfileToSearchUser(ProfileResponse profile) {
        if (profile == null) {
            return null;
        }
        return SearchUserResponse.builder()
                .profileId(profile.getId())
                .userId(profile.getUserId())
                .fullName(profile.getFullName())
                .username(profile.getUsername())
                .avatarUrl(profile.getAvtUrl())
                .email(profile.getEmail())
                .verified(profile.isVerified())
                .roles(profile.getUserRole())
                .status(profile.getUserStatus())
                .build();
    }

    private <T> T joinFuture(CompletableFuture<T> future) {
        return future == null ? null : future.join();
    }
}



