package com.se.hub.modules.search.controller;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.constant.MessageConstant;
import com.se.hub.common.constant.PaginationConstants;
import com.se.hub.common.controller.BaseController;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.request.SortRequest;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.common.constant.ResponseCode;
import com.se.hub.modules.search.constant.SearchConstants;
import com.se.hub.modules.search.constant.SearchMessageConstants;
import com.se.hub.modules.search.dto.request.SearchRequest;
import com.se.hub.modules.search.dto.response.SearchResponse;
import com.se.hub.modules.search.enums.SearchTarget;
import com.se.hub.modules.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(SearchConstants.SEARCH_BASE_PATH)
@Tag(name = "Global Search", description = "Search across exams, blogs and users")
public class SearchController extends BaseController {

    SearchService searchService;

    @GetMapping
    @Operation(summary = "Search across modules",
            description = "Search exams, blogs và users theo keyword, hỗ trợ pagination & sorting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200,
                    description = SearchMessageConstants.API_SEARCH_SUCCESS,
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400,
                    description = MessageConstant.VALIDATION_ERROR)
    })
    public ResponseEntity<GenericResponse<SearchResponse>> search(
            @RequestParam(name = SearchConstants.PARAM_KEYWORD) @NotBlank String keyword,
            @RequestParam(name = PaginationConstants.PARAM_PAGE,
                    required = false,
                    defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(name = PaginationConstants.PARAM_SIZE,
                    required = false,
                    defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = SearchConstants.PARAM_SORT_FIELD,
                    required = false,
                    defaultValue = BaseFieldConstant.CREATE_DATE) String sortField,
            @RequestParam(name = SearchConstants.PARAM_SORT_DIRECTION,
                    required = false,
                    defaultValue = PaginationConstants.DESC) String sortDirection,
            @RequestParam(name = SearchConstants.PARAM_TARGETS, required = false) Set<SearchTarget> targets
    ) {
        log.info("SearchController_search_Received request - keyword: {} page: {} size: {} sort: {} {}", keyword, page, size, sortDirection, sortField);
        PagingRequest pagingRequest = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(SortRequest.builder()
                        .direction(sortDirection)
                        .field(sortField)
                        .build())
                .build();

        SearchRequest searchRequest = SearchRequest.builder()
                .keyword(keyword)
                .pagingRequest(pagingRequest)
                .targets(targets)
                .build();

        SearchResponse response = searchService.search(searchRequest);
        return success(response, MessageCodeConstant.M005_RETRIEVED, SearchMessageConstants.API_SEARCH_SUCCESS);
    }
}

