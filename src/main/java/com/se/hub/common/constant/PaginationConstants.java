package com.se.hub.common.constant;

public class PaginationConstants {
    // =======  PAGEABLE VALUE VALIDATION =======
    public static final int MIN_PAGE_SIZE =  1;
    public static final int MIN_PAGE_NUMBER =  1;

    // =======  PAGEABLE PARAMETER NAMES =======
    public static final String PARAM_PAGE = "page";
    public static final String PARAM_SIZE = "size";

    // =======  PAGEABLE DEFAULT VALUES =======
    public static final String DEFAULT_PAGE = "1";
    public static final String DEFAULT_PAGE_SIZE = "10";

    // =======  SORT DIRECTION =======
    public static final String DESC =  "desc";
    public static final String ASC =  "asc";

    private PaginationConstants() {}
}
