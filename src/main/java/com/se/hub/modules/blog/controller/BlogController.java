package com.se.hub.modules.blog.controller;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.constant.MessageConstant;
import com.se.hub.common.constant.PaginationConstants;
import com.se.hub.common.constant.ResponseCode;
import com.se.hub.common.controller.BaseController;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.request.SortRequest;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.blog.constant.BlogMessageConstants;
import com.se.hub.modules.blog.dto.request.CreateBlogRequest;
import com.se.hub.modules.blog.dto.request.UpdateBlogRequest;
import com.se.hub.modules.blog.dto.response.BlogResponse;
import com.se.hub.modules.blog.dto.response.BlogSettingResponse;
import com.se.hub.modules.blog.service.BlogService;
import com.se.hub.modules.blog.service.api.BlogSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Blog Management",
        description = "Blog management API")
@RequestMapping("/blogs")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class BlogController extends BaseController {
    BlogService blogService;
    BlogSettingService blogSettingService;

    @PostMapping
    @Operation(summary = "Create new blog",
            description = "Create a new blog in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = BlogMessageConstants.API_BLOG_CREATED_SUCCESS, 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = BlogMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = BlogMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<BlogResponse>> createBlog(@Valid @RequestBody CreateBlogRequest request) {
        BlogResponse blogResponse = blogService.createBlog(request);
        return success(blogResponse, MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all blogs",
            description = "Get list of all blogs with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = BlogMessageConstants.API_BLOG_RETRIEVED_ALL_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = BlogMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = BlogMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<BlogResponse>>> getBlogs(
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
    ) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        return success(blogService.getBlogs(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/{blogId}")
    @Operation(summary = "Get blog by ID",
            description = "Get blog information by blog ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = BlogMessageConstants.API_BLOG_RETRIEVED_BY_ID_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = BlogMessageConstants.BLOG_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = BlogMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<BlogResponse>> getBlogById(@PathVariable String blogId) {
        return success(blogService.getById(blogId), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get blogs by author ID",
            description = "Get list of blogs for a specific author with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = BlogMessageConstants.API_BLOG_RETRIEVED_BY_AUTHOR_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = BlogMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = BlogMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<BlogResponse>>> getBlogsByAuthorId(
            @PathVariable String authorId,
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
    ) {

        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        return success(blogService.getBlogsByAuthorId(authorId, request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @PutMapping("/{blogId}")
    @Operation(summary = "Update blog",
            description = "Update blog information by blog ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = BlogMessageConstants.API_BLOG_UPDATED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = BlogMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = BlogMessageConstants.BLOG_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = BlogMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<BlogResponse>> updateBlog(
            @PathVariable String blogId,
            @Valid @RequestBody UpdateBlogRequest request) {
        return success(blogService.updateBlogById(blogId, request), MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @DeleteMapping("/{blogId}")
    @Operation(summary = "Delete blog",
            description = "Delete a blog from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = BlogMessageConstants.API_BLOG_DELETED_SUCCESS, 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = BlogMessageConstants.BLOG_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = BlogMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<Void>> deleteBlog(@PathVariable String blogId) {
        blogService.deleteBlogById(blogId);
        return success(null, MessageCodeConstant.M004_DELETED, MessageConstant.DELETED);
    }

    @GetMapping("/popular")
    @Operation(summary = "Get most popular blogs",
            description = "Get list of most popular blogs sorted by view count with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = BlogMessageConstants.API_BLOG_POPULAR_SUCCESS, 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = BlogMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = BlogMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<BlogResponse>>> getPopularBlogs(
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
    ) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        return success(blogService.getMostPopularBlogs(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/liked")
    @Operation(summary = "Get most liked blogs",
            description = "Get list of most liked blogs sorted by reaction count with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = BlogMessageConstants.API_BLOG_MOST_LIKED_SUCCESS, 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = BlogMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = BlogMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<BlogResponse>>> getLikedBlogs(
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
    ) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        return success(blogService.getMostLikedBlogs(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest blogs",
            description = "Get list of latest blogs sorted by created date with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = BlogMessageConstants.API_BLOG_LATEST_SUCCESS, 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = BlogMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = BlogMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<BlogResponse>>> getLatestBlogs(
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
    ) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        return success(blogService.getLatestBlogs(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @PostMapping("/{blogId}/view")
    @Operation(summary = "Increment view count",
            description = "Increment view count for a blog (atomic operation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = BlogMessageConstants.API_BLOG_VIEW_INCREMENTED_SUCCESS, 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = BlogMessageConstants.BLOG_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = BlogMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<Void>> incrementViewCount(@PathVariable String blogId) {
        blogService.incrementViewCount(blogId);
        return success(null, MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @PostMapping("/{blogId}/react")
    @Operation(summary = "Increment reaction count",
            description = "Increment or decrement reaction count for a blog (atomic operation). Use delta=1 for like, delta=-1 for unlike")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = BlogMessageConstants.API_BLOG_REACTION_UPDATED_SUCCESS, 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = BlogMessageConstants.BLOG_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = BlogMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = BlogMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<Void>> incrementReactionCount(
            @PathVariable String blogId,
            @RequestParam(value = "delta", defaultValue = "1") int delta) {
        blogService.incrementReactionCount(blogId, delta);
        return success(null, MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @PostMapping("/{blogId}/like")
    @Operation(summary = "Like a blog",
            description = "Like a blog. If already disliked, changes to like. If already liked, does nothing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = BlogMessageConstants.API_BLOG_LIKED_SUCCESS, 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = BlogMessageConstants.BLOG_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = BlogMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = BlogMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<BlogResponse>> likeBlog(@PathVariable String blogId) {
        BlogResponse response = blogService.likeBlog(blogId);
        return success(response, MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @PostMapping("/{blogId}/dislike")
    @Operation(summary = "Dislike a blog",
            description = "Dislike a blog. If already liked, changes to dislike. If already disliked, does nothing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = BlogMessageConstants.API_BLOG_DISLIKED_SUCCESS, 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = BlogMessageConstants.BLOG_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = BlogMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = BlogMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<BlogResponse>> dislikeBlog(@PathVariable String blogId) {
        BlogResponse response = blogService.dislikeBlog(blogId);
        return success(response, MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @DeleteMapping("/{blogId}/reaction")
    @Operation(summary = "Remove reaction from a blog",
            description = "Remove like or dislike reaction from a blog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = BlogMessageConstants.API_BLOG_UNREACTED_SUCCESS, 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = BlogMessageConstants.BLOG_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = BlogMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = BlogMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<BlogResponse>> removeReaction(@PathVariable String blogId) {
        BlogResponse response = blogService.removeReaction(blogId);
        return success(response, MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @PostMapping("/{blogId}/approve")
    @Operation(summary = "Approve a blog (Admin only)",
            description = "Approve a pending blog. Only admin can perform this action.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = BlogMessageConstants.API_BLOG_APPROVED_SUCCESS, 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = BlogMessageConstants.BLOG_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.FORBIDDEN_403, description = BlogMessageConstants.BLOG_FORBIDDEN_OPERATION_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = BlogMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = BlogMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<BlogResponse>> approveBlog(@PathVariable String blogId) {
        BlogResponse response = blogService.approveBlog(blogId);
        return success(response, MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @PostMapping("/{blogId}/reject")
    @Operation(summary = "Reject a blog (Admin only)",
            description = "Reject a pending blog. Only admin can perform this action.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = BlogMessageConstants.API_BLOG_REJECTED_SUCCESS, 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = BlogMessageConstants.BLOG_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.FORBIDDEN_403, description = BlogMessageConstants.BLOG_FORBIDDEN_OPERATION_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = BlogMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = BlogMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<BlogResponse>> rejectBlog(@PathVariable String blogId) {
        BlogResponse response = blogService.rejectBlog(blogId);
        return success(response, MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending blogs (Admin only)",
            description = "Get list of pending blogs that need approval. Only admin can access this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = BlogMessageConstants.API_BLOG_PENDING_RETRIEVED_SUCCESS, 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.FORBIDDEN_403, description = BlogMessageConstants.BLOG_FORBIDDEN_OPERATION_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = BlogMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = BlogMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<BlogResponse>>> getPendingBlogs(
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
    ) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        return success(blogService.getPendingBlogs(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @PostMapping("/settings/toggle-approval")
    @Operation(summary = "Toggle blog approval mode (Admin only)",
            description = "Toggle blog approval requirement mode. When disabled, blogs are auto-approved on creation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = "Blog approval mode toggled successfully"),
            @ApiResponse(responseCode = ResponseCode.FORBIDDEN_403, description = BlogMessageConstants.BLOG_FORBIDDEN_OPERATION_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = BlogMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<BlogSettingResponse>> toggleApprovalMode() {
        BlogSettingResponse response = blogSettingService.toggleApprovalMode();
        return success(response, MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @GetMapping("/settings/approval-mode")
    @Operation(summary = "Get blog approval mode",
            description = "Get current blog approval requirement setting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = "Blog approval mode retrieved successfully"),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = BlogMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<BlogSettingResponse>> getApprovalMode() {
        BlogSettingResponse response = blogSettingService.getApprovalMode();
        return success(response, MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }
}
