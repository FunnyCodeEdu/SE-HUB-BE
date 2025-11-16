package com.se.hub.modules.exam.constant.answer_report;

public class AnswerReportErrorCodeConstants {
    public static final String ANSWER_REPORT_QUESTION_ID_INVALID = "Question ID is required";
    public static final String ANSWER_REPORT_QUESTION_OPTION_ID_INVALID = "Question Option ID is required";
    public static final String ANSWER_REPORT_DESCRIPTION_INVALID = "Description must not exceed 1000 characters";
    public static final String ANSWER_REPORT_SUGGESTED_ANSWER_INVALID = "Suggested correct answer must not exceed 2000 characters";
    public static final String ANSWER_REPORT_NOT_FOUND = "Answer report not found";
    public static final String ANSWER_REPORT_ID_REQUIRED = "Answer report ID is required";
    public static final String ANSWER_REPORT_FORBIDDEN_OPERATION = "You do not have permission to perform this operation";
    public static final String ANSWER_REPORT_ALREADY_PROCESSED = "This report has already been processed";

    private AnswerReportErrorCodeConstants() {}
}

