package com.ebiz.drivel.domain.token.api;

import com.ebiz.drivel.domain.token.exception.BlackListedTokenException;
import com.ebiz.drivel.domain.token.exception.DifferentRefreshTokenException;
import com.ebiz.drivel.global.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = {TokenController.class})
public class TokenExceptionHandler {

    @ExceptionHandler(DifferentRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleDifferentRefreshTokenException(DifferentRefreshTokenException e) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .message(e.getMessage())
                .build(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BlackListedTokenException.class)
    public ResponseEntity<ErrorResponse> handleBlackListedTokenException(BlackListedTokenException e) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .message(e.getMessage())
                .build(), HttpStatus.UNAUTHORIZED);
    }
}
