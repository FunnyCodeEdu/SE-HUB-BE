package com.catsocute.japanlearn_hub.common.dto.request;

import com.catsocute.japanlearn_hub.common.constant.ErrorCodeConstant;
import com.catsocute.japanlearn_hub.common.constant.PaginationConstants;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PagingRequest {
    @Min(value = PaginationConstants.MIN_PAGE_NUMBER,
            message = ErrorCodeConstant.PAGE_NUMBER_INVALID)
    int page;

    @Min(value = PaginationConstants.MIN_PAGE_SIZE,
            message = ErrorCodeConstant.PAGE_SIZE_INVALID)
    int pageSize;

    SortRequest sortRequest;

    public PagingRequest(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }
}
