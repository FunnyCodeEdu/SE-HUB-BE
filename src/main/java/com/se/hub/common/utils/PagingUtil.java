package com.se.hub.common.utils;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.constant.PaginationConstants;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.request.SortRequest;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

public class PagingUtil {
    public static Sort createSort(PagingRequest pagingRequest) {
        if(pagingRequest == null || pagingRequest.getSortRequest() == null || pagingRequest.getSortRequest().getField() == null) {
            return Sort.unsorted();
        }

        SortRequest sortRequest = pagingRequest.getSortRequest();
        if(!StringUtils.hasText(sortRequest.getField())) {
            return Sort.unsorted();
        }

        Sort.Direction direction = Sort.Direction.fromString(sortRequest.getDirection());
        return Sort.by(direction, sortRequest.getField());
    }

    /**
     * Create Pageable from PagingRequest with validation
     * Validates page number >= MIN_PAGE_NUMBER before creating PageRequest
     */
    public static Pageable createPageable(PagingRequest pagingRequest) {
        if (pagingRequest == null) {
            throw new AppException(ErrorCode.DATA_INVALID);
        }
        
        if (pagingRequest.getPage() < PaginationConstants.MIN_PAGE_NUMBER) {
            throw new AppException(ErrorCode.PAGE_NUMBER_INVALID);
        }
        
        if (pagingRequest.getPageSize() < PaginationConstants.MIN_PAGE_SIZE) {
            throw new AppException(ErrorCode.PAGE_SIZE_INVALID);
        }
        
        return PageRequest.of(
                pagingRequest.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                pagingRequest.getPageSize(),
                createSort(pagingRequest)
        );
    }

    private PagingUtil() {}
}
