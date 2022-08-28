package community.mingle.app.config.security;

import community.mingle.app.config.TokenHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenHelper tokenHelper;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        Optional<String> t = extractToken(request); //resolveToken

        if (!(t.isEmpty())) {
            Authentication authentication = tokenHelper.validateToken(request, t.get()); //토큰 검증 - 유효 여부 확인
            SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContextHolder에 넣을 유저정보(Authenticaion 객체)를 받아오기
        } else {
            request.setAttribute("exception", "토큰 헤더가 잘못되었습니다. Authorization 을 넣어주세요.");
        }
        chain.doFilter(request, response);
    }


    private Optional<String> extractToken(ServletRequest request) {
        return Optional.ofNullable(((HttpServletRequest) request).getHeader("Authorization"));
    }
}

