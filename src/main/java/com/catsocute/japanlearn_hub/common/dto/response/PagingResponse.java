package com.catsocute.japanlearn_hub.common.dto.response;

import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagingResponse<T> {
    int currentPage;
    int pageSize;
    int totalPages;
    long totalElement;

    @Builder.Default
    List<T> data = Collections.emptyList();
}
