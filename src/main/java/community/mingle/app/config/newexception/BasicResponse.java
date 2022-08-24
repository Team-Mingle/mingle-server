package community.mingle.app.config.newexception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class BasicResponse {
    private String message;
    private HttpStatus status;
}