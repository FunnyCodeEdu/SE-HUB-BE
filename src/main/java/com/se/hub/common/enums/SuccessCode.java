package com.se.hub.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SuccessCode {
    SUCCESS("SUCCESS","Successfully executed", HttpStatus.OK)
    ;

    String code;
    String message;
    HttpStatusCode httpStatusCode;
}
