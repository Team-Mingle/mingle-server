package community.mingle.app.config.newexception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BasicResponse> handle(BadRequestException e) {
        BasicResponse exceptionDto = new BasicResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }
}