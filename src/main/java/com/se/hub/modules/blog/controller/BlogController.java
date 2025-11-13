package com.se.hub.modules.blog.controller;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.constant.MessageConstant;
import com.se.hub.common.constant.PaginationConstants;
import com.se.hub.common.controller.BaseController;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.request.SortRequest;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.blog.constant.BlogMessageCodes;
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
    ProfileProgressService  profileProgressService;

    @PostMapping
    @Operation(summary = "Create new blog",
            description = "Create a new blog in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog created successfully", 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Bad request - " + BlogMessageCodes.VALIDATION_ERROR),
            @ApiResponse(responseCode = "500", description = "Internal server error - " + BlogMessageCodes.INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<BlogResponse>> createBlog(@Valid @RequestBody CreateBlogRequest request) {
        log.info("BlogController_createBlog_Creating new blog");
        BlogResponse blogResponse = blogService.createBlog(request);

        //update user stats
        profileProgressService.updatePostsUploaded();

        log.info("BlogController_createBlog_Blog created successfully with id: {}", blogResponse.getId());
        return success(blogResponse, MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
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

        return success(blogService.getBlogs(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
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
        return success(blogService.getById(blogId), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
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

        return success(blogService.getBlogsByAuthorId(authorId, request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
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
        return success(blogService.updateBlogById(blogId, request), MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @DeleteMapping("/{blogId}")
    @Operation(summary = "Delete blog",
            description = "Delete a blog from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog deleted successfully", 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Blog not found - " + BlogMessageCodes.NOT_FOUND),
            @ApiResponse(responseCode = "500", description = "Internal server error - " + BlogMessageCodes.INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<Void>> deleteBlog(@PathVariable String blogId) {
        log.info("BlogController_deleteBlog_Deleting blog with id: {}", blogId);
        blogService.deleteBlogById(blogId);
        log.info("BlogController_deleteBlog_Blog deleted successfully with id: {}", blogId);
        return success(null, MessageCodeConstant.M004_DELETED, MessageConstant.DELETED);
    }

    @GetMapping("/popular")
    @Operation(summary = "Get most popular blogs",
            description = "Get list of most popular blogs sorted by view count with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved popular blogs successfully", 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Bad request - " + BlogMessageCodes.VALIDATION_ERROR),
            @ApiResponse(responseCode = "500", description = "Internal server error - " + BlogMessageCodes.INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<BlogResponse>>> getPopularBlogs(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
    ) {
        log.debug("BlogController_getPopularBlogs_Fetching popular blogs with page: {}, size: {}", page, size);
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
            @ApiResponse(responseCode = "200", description = "Retrieved liked blogs successfully", 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Bad request - " + BlogMessageCodes.VALIDATION_ERROR),
            @ApiResponse(responseCode = "500", description = "Internal server error - " + BlogMessageCodes.INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<BlogResponse>>> getLikedBlogs(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
    ) {
        log.debug("BlogController_getLikedBlogs_Fetching liked blogs with page: {}, size: {}", page, size);
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
            @ApiResponse(responseCode = "200", description = "Retrieved latest blogs successfully", 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Bad request - " + BlogMessageCodes.VALIDATION_ERROR),
            @ApiResponse(responseCode = "500", description = "Internal server error - " + BlogMessageCodes.INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<BlogResponse>>> getLatestBlogs(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
    ) {
        log.debug("BlogController_getLatestBlogs_Fetching latest blogs with page: {}, size: {}", page, size);
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
            @ApiResponse(responseCode = "200", description = "View count incremented successfully", 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Blog not found - " + BlogMessageCodes.NOT_FOUND),
            @ApiResponse(responseCode = "500", description = "Internal server error - " + BlogMessageCodes.INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<Void>> incrementViewCount(@PathVariable String blogId) {
        log.debug("BlogController_incrementViewCount_Incrementing view count for blog id: {}", blogId);
        blogService.incrementViewCount(blogId);
        return success(null, MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @PostMapping("/{blogId}/react")
    @Operation(summary = "Increment reaction count",
            description = "Increment or decrement reaction count for a blog (atomic operation). Use delta=1 for like, delta=-1 for unlike")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reaction count updated successfully", 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Blog not found - " + BlogMessageCodes.NOT_FOUND),
            @ApiResponse(responseCode = "400", description = "Bad request - " + BlogMessageCodes.VALIDATION_ERROR),
            @ApiResponse(responseCode = "500", description = "Internal server error - " + BlogMessageCodes.INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<Void>> incrementReactionCount(
            @PathVariable String blogId,
            @RequestParam(value = "delta", defaultValue = "1") int delta) {
        log.debug("BlogController_incrementReactionCount_Updating reaction count for blog id: {} with delta: {}", blogId, delta);
        blogService.incrementReactionCount(blogId, delta);
        return success(null, MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }
}
