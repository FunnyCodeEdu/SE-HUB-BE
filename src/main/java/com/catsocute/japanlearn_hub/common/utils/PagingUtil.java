package com.catsocute.japanlearn_hub.common.utils;

import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import com.catsocute.japanlearn_hub.common.dto.request.SortRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

public class PagingUtil {
    public static Sort createSort(PagingRequest pagingRequest) {
        if(pagingRequest == null || pagingRequest.getSortRequest().getField() == null) {
            return Sort.unsorted();
        }

        SortRequest sortRequest = pagingRequest.getSortRequest();
        if(!StringUtils.hasText(sortRequest.getField())) {
            return Sort.unsorted();
        }

        Sort.Direction direction = Sort.Direction.fromString(sortRequest.getDirection());
        return Sort.by(direction, sortRequest.getField());
    }

    private PagingUtil() {}
}
