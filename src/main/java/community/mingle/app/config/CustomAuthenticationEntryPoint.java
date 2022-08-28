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

    /**
     * 트러블슈팅: 잘못된 header 가 들어갔을때 null, FORBIDDEN 이 반환되는 문제
     * https://doozi0316.tistory.com/entry/Spring-Security-Spring-Security%EC%9D%98-%EA%B0%9C%EB%85%90%EA%B3%BC-%EB%8F%99%EC%9E%91-%EA%B3%BC%EC%A0%95
     * ㄴ 출처: 인증에 실패하여 AuthenticaionException 이 발생하면 인증 메커니즘을 지원하는 AuthenticationEntryPoint에 의해 처리된다.
     * 설명: 원래는 jwtAuthenticationFilter 에서 securityContext 에 Authenticated 객체가 들어가야지 다음에 doFilter 했을때 정상적으로 처리가 되는데 만약 header 가
     * Authorization 이 아니라면 isEmpty 가 충족이 안되므로 SecurityContext에 넣어지지 않고 다음 필터 실행. 이 경우 Exception이 나고 AuthenticationEntryPoint 에서 잡힘
     * 그치만 exception문구를 setAttribute 해주지 않았음으로 .getAttribute 했을때 null 이 담겨서 null, Forbidden 이 반환됨.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String exception = (String) request.getAttribute("exception");
        setResponse(response);

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
