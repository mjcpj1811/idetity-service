package com.example.identity_service.exception;

import com.example.identity_service.dto.request.ApiResponse;
import com.example.identity_service.service.UserService;
import jakarta.validation.ConstraintViolation;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final ConversionService conversionService;
    private static final String MIN_ATTRIBUTE = "min";

    public GlobalExceptionHandler(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception){
        ApiResponse apiResponse=new ApiResponse();

        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception){
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse=new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getHttpStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception){
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED_ACCESS;

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(
                    ApiResponse.builder()
                            .code(errorCode.getCode())
                            .message(errorCode.getMessage())
                            .build()
                );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception){
        String enumKey= exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode= ErrorCode.INVALID_KEY;
        Map<String,Object> attributes = null;
        try {
            errorCode = errorCode.valueOf(enumKey);

            var contractViolation = exception.getBindingResult()
                    .getAllErrors().getFirst().unwrap(ConstraintViolation.class);

            attributes = contractViolation.getConstraintDescriptor().getAttributes();
        }catch (IllegalArgumentException e){
        }

        ApiResponse apiResponse=new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(Objects.nonNull(errorCode.getMessage()) ?
                mapAttributes(errorCode.getMessage(),attributes)
                : errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    private String mapAttributes(String message , Map<String, Object> attributes){
        String minvalue = String.valueOf(attributes.get(MIN_ATTRIBUTE)) ;

        return message.replace("{" + MIN_ATTRIBUTE + "}", minvalue);
    }
}
