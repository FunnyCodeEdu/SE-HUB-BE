package com.se.hub.modules.user.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.profile.dto.request.CreateDefaultProfileRequest;
import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.profile.service.impl.ProfileServiceImpl;
import com.se.hub.modules.user.constant.role.PredefinedRole;
import com.se.hub.modules.user.dto.request.ChangePasswordRequest;
import com.se.hub.modules.user.dto.request.UserCreationRequest;
import com.se.hub.modules.user.dto.request.UserRolesUpdateRequest;
import com.se.hub.modules.user.dto.response.UserResponse;
import com.se.hub.modules.user.entity.Role;
import com.se.hub.modules.user.entity.User;
import com.se.hub.modules.user.enums.UserStatus;
import com.se.hub.modules.user.mapper.UserMapper;
import com.se.hub.modules.user.repository.RoleRepository;
import com.se.hub.modules.user.repository.UserRepository;
import com.se.hub.modules.user.service.api.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    ProfileServiceImpl  profileService;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse create(UserCreationRequest request) {
        validateUsernameExisted(request.getUsername());
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        setDefaultRole(user);

        User userCreated = userRepository.save(user);

        //create default profile
        CreateDefaultProfileRequest createDefaultProfileRequest = CreateDefaultProfileRequest.builder()
                .request(request)
                .build();
        Profile defaultProfile = profileService.createDefaultProfile(userCreated, createDefaultProfileRequest);
        userCreated.setProfile(defaultProfile);
        return userMapper.toUserResponse(userRepository.save(userCreated));
    }

    @Override
    public UserResponse changePassword(ChangePasswordRequest request) {
        String userId = AuthUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        boolean isCorrect = passwordEncoder.matches(request.getOldPassword(), user.getPassword());
        if(isCorrect) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            return userMapper.toUserResponse(userRepository.save(user));
        } else {
            throw new AppException(ErrorCode.AUTH_UNAUTHENTICATED);
        }
    }

    @Override
    public UserResponse updateRoles(String userId, UserRolesUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Role> roles = roleRepository.findAllById(request.getUserRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public PagingResponse<UserResponse> getUsers(PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<User> userPages = userRepository.findAll(pageable);

        return PagingResponse.<UserResponse>builder()
                .currentPage(request.getPage())
                .pageSize(userPages.getSize())
                .totalPages(userPages.getTotalPages())
                .totalElement(userPages.getTotalElements())
                .data(userPages.getContent().stream()
                        .map(userMapper::toUserResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public UserResponse getMyInfo() {
        String userId = AuthUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    @Override
    public void deleteById(String userId) {
        userRepository.deleteById(userId);
    }

    private void setDefaultRole(User user) {
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);
    }

    private void validateUsernameExisted(String username) {
        if(userRepository.existsByUsername(username)) {
            throw new AppException(ErrorCode.USER_USERNAME_EXISTED);
        }
    }
}
