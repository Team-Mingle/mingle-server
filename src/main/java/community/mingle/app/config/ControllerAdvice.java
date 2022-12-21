//package community.mingle.app.config;
//
//import lombok.val;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.HttpMediaTypeNotSupportedException;
//import org.springframework.web.bind.MissingPathVariableException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import java.util.Map;
//
//import static org.springframework.boot.context.properties.bind.Bindable.mapOf;
//
//@RestControllerAdvice
//public class ControllerAdvice {
//
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(MissingPathVariableException::class)
//    fun handleMissingPathVariable(ex: MissingPathVariableException): Map<String, String> {
//        Sentry.captureException(ex)
//        val error:Map<String, String> = mapOf("code" to "E0001", "message" to "Parameter error")
//        return error
//    }
//
//    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
//    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
//    fun handleHttpMediaTypeNotSupported(ex: HttpMediaTypeNotSupportedException): Map<String, String> {
//        Sentry.captureException(ex)
//        val error: Map<String, String> = mapOf("code" to "E0002", "message" to "Unsupported Media Type")
//        return error
//    }
//
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler(Exception::class)
//    fun handleExceptions(ex: Exception): Map<String, String> {
//        Sentry.captureException(ex)
//        val error: Map<String, String> = mapOf("code" to "E0003", "message" to "Internal Server Error")
//        return error
//    }
//}