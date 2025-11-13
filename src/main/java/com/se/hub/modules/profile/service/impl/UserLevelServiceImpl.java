package com.se.hub.modules.profile.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.modules.profile.dto.request.CreateUserLevelRequest;
import com.se.hub.modules.profile.dto.response.UserLevelResponse;
import com.se.hub.modules.profile.entity.UserLevel;
import com.se.hub.modules.profile.mapper.UserLevelMapper;
import com.se.hub.modules.profile.repository.UserLevelRepository;
import com.se.hub.modules.profile.service.api.UserLevelService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserLevelServiceImpl implements UserLevelService {
    UserLevelRepository userLevelRepository;
    UserLevelMapper userLevelMapper;

    /**
     * create level
     * use initializer data to create level
     *
     * @param request CreateUserLevelRequest
     * @return UserLevel
     */
    @Override
    public UserLevel createUserLevel(CreateUserLevelRequest request) {
        //validate level existed
        if (userLevelRepository.existsByLevel(request.getLevel())) {
            log.error("Level {} already exists!", request.getLevel());
            throw new AppException(ErrorCode.DATA_EXISTED);
        }

        UserLevel userLevel = userLevelMapper.toUserLevel(request);
        return userLevelRepository.save(userLevel);
    }

    /**
     * get level for update profile
     * do not create api end point for this func
     * 
     * @param points int
     * @return UserLevel
     */
    @Override
    public UserLevel getUserLevelByPoints(int points) {
        if (points < 0) {
            log.error("Invalid points provided: {}", points);
            throw new AppException(ErrorCode.DATA_INVALID);
        }

        return userLevelRepository.findLevelByPoints(points)
                .orElseThrow(() -> {
                    log.error("Cannot find Level by points: {}", points);
                    return new AppException(ErrorCode.DATA_NOT_FOUND);
                });
    }

    @Override
    public void deleteById(String id) {
        userLevelRepository.deleteById(id);
    }

    @Override
    public PagingResponse<UserLevelResponse> getAllUserLevels(PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(
                pagingRequest.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                pagingRequest.getPageSize(),
                PagingUtil.createSort(pagingRequest)
        );

        Page<UserLevel> userLevelPage = userLevelRepository.findAll(pageable);

        return PagingResponse.<UserLevelResponse>builder()
                .currentPage(userLevelPage.getNumber())
                .pageSize(userLevelPage.getSize())
                .totalPages(userLevelPage.getTotalPages())
                .totalElement(userLevelPage.getTotalElements())
                .data(userLevelPage.getContent().stream()
                        .map(userLevelMapper::toUserLevelResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public UserLevelResponse getUserLevelById(String id) {
        UserLevel userLevel = getExistingUserLevelById(id);
        return userLevelMapper.toUserLevelResponse(userLevel);
    }

    private UserLevel getExistingUserLevelById(String id) {
        return userLevelRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
    }
}
