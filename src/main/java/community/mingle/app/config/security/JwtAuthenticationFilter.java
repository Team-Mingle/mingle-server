package community.mingle.app.config.security;

import community.mingle.app.src.auth.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {

//    private final TokenService tokenService;
    private final CustomUserDetailsService userDetailsService;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //FilterChain: 이 필터 다음에 올 필터
//        String token = extractToken(request);
//        if (validateAccessToken(token)) {
//            //시큐리티컨텍스트에 Authentication을 집어넣음
//            setAccessAuthentication(token);
//        }
////        else if (validateRefreshToken(token)) {
////            setRefreshAuthentication("refresh", token);
////        }
//        chain.doFilter(request, response);
        extractToken(request).map(userDetailsService::loadUserByUsername).ifPresent(this::setAccessAuthentication);
    }

    private void setAccessAuthentication(CustomUserDetails userDetails) {
        SecurityContextHolder.getContext().setAuthentication(new CustomAuthenticationToken(userDetails, userDetails.getAuthorities()));
    }

    private Optional<String> extractToken(ServletRequest request) {
        return Optional.ofNullable(((HttpServletRequest) request).getHeader("Authorization"));
    }

//    private String extractToken(ServletRequest request) {
//        return ((HttpServletRequest) request).getHeader("Authorization");
//    }
//
//    private boolean validateAccessToken(String token) {
//        return token != null && tokenService.validateAccessToken(token);
//    }
//
//    private boolean validateRefreshToken(String token) {
//        return token != null && tokenService.validateRefreshToken(token);
//    }
//
//    private void setAccessAuthentication(String token) {
//        String userId = tokenService.extractAccessTokenSubject(token);
//        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(userId);
//        SecurityContextHolder.getContext().setAuthentication(new CustomAuthenticationToken(userDetails, userDetails.getAuthorities()));
//
//    }

//    private void setRefreshAuthentication(String type, String token) {
//        String userId = tokenService.extractRefreshTokenSubject(token);
//        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(userId);
//        SecurityContextHolder.getContext().setAuthentication(new CustomAuthenticationToken(type, userDetails, userDetails.getAuthorities()));
//    }
}

