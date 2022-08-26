package community.mingle.app.config.newexception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) {
        super(message);
    }
}