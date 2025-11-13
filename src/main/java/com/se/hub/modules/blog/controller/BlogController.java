package com.se.hub.modules.blog.controller;

import com.se.hub.common.constant.ApiConstant;
import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.constant.MessageConstant;
import com.se.hub.common.constant.PaginationConstants;
import com.se.hub.common.dto.MessageDTO;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.request.SortRequest;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.blog.dto.request.CreateBlogRequest;
import com.se.hub.modules.blog.dto.request.UpdateBlogRequest;
import com.se.hub.modules.blog.dto.response.BlogResponse;
import com.se.hub.modules.blog.service.api.BlogService;
import com.se.hub.modules.profile.service.api.ProfileProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Blog Management",
        description = "Blog management API")
@RequestMapping("/api/v1/blogs")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlogController {
    BlogService blogService;
    ProfileProgressService  profileProgressService;

    @PostMapping
    @Operation(summary = "Create new blog",
            description = "Create a new blog in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<BlogResponse>> createBlog(@Valid @RequestBody CreateBlogRequest request) {
        GenericResponse<BlogResponse> response = GenericResponse.<BlogResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M002_CREATED)
                        .messageDetail(MessageConstant.CREATED)
                        .build())
                .data(blogService.createBlog(request))
                .build();

        //update user stats
        profileProgressService.updatePostsUploaded();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all blogs",
            description = "Get list of all blogs with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved all blogs successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<BlogResponse>>> getBlogs(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
    ) {

        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        GenericResponse<PagingResponse<BlogResponse>> response = GenericResponse.<PagingResponse<BlogResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(blogService.getBlogs(request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{blogId}")
    @Operation(summary = "Get blog by ID",
            description = "Get blog information by blog ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved blog by ID successfully"),
            @ApiResponse(responseCode = "404", description = "Blog not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<BlogResponse>> getBlogById(@PathVariable String blogId) {
        GenericResponse<BlogResponse> response = GenericResponse.<BlogResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(blogService.getById(blogId))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get blogs by author ID",
            description = "Get list of blogs for a specific author with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved blogs by author ID successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<BlogResponse>>> getBlogsByAuthorId(
            @PathVariable String authorId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
    ) {

        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        GenericResponse<PagingResponse<BlogResponse>> response = GenericResponse.<PagingResponse<BlogResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(blogService.getBlogsByAuthorId(authorId, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{blogId}")
    @Operation(summary = "Update blog",
            description = "Update blog information by blog ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Blog not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<BlogResponse>> updateBlog(
            @PathVariable String blogId,
            @Valid @RequestBody UpdateBlogRequest request) {
        GenericResponse<BlogResponse> response = GenericResponse.<BlogResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M003_UPDATED)
                        .messageDetail(MessageConstant.UPDATED)
                        .build())
                .data(blogService.updateBlogById(blogId, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{blogId}")
    @Operation(summary = "Delete blog",
            description = "Delete a blog from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog deleted successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<Void>> deleteBlog(@PathVariable String blogId) {
        blogService.deleteBlogById(blogId);

        GenericResponse<Void> response = GenericResponse.<Void>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M004_DELETED)
                        .messageDetail(MessageConstant.DELETED)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }
}
