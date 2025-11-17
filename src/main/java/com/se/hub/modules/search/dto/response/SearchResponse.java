package com.se.hub.modules.search.dto.response;

import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.blog.dto.response.BlogResponse;
import com.se.hub.modules.exam.dto.response.ExamResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchResponse {
    PagingResponse<BlogResponse> blogs;
    PagingResponse<ExamResponse> exams;
    PagingResponse<SearchUserResponse> users;
}


