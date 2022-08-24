package community.mingle.app.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import community.mingle.app.config.newexception.BasicResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String exception = (String) request.getAttribute("exception");

        //        throw new RuntimeException();
//        response.sendRedirect("/exception/entry-point");
//        response.setStatus(SC_UNAUTHORIZED);

        //new
//        BaseResponse baseResponse = new BaseResponse<>(BaseResponseStatus.DATABASE_ERROR);
//        response.getWriter().print(convertObjectToJson(baseResponse));

        BasicResponse exceptionDto = new BasicResponse(exception, HttpStatus.FORBIDDEN);
        response.getWriter().print(convertObjectToJson(exceptionDto));


    }

    private void setResponse(HttpServletResponse response) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }



    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        return mapper.writeValueAsString(object);
    }
}
