package community.mingle.app.config;


import community.mingle.app.config.handler.JwtHandler;
import community.mingle.app.config.exception.BadRequestException;
import community.mingle.app.config.security.CustomAuthenticationToken;
import community.mingle.app.config.security.CustomUserDetails;
import community.mingle.app.config.security.CustomUserDetailsService;
import community.mingle.app.src.domain.UserRole;
import community.mingle.app.utils.RedisService;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenHelper {

    private final JwtHandler jwtHandler;
    private final CustomUserDetailsService customUserDetailsService;
    private final RedisService redisService;


    @Value("${jwt.max-age.access}") // 1
    private Long accessTokenMaxAgeSeconds;

    @Value("${jwt.max-age.refresh}") // 2
    private Long refreshTokenMaxAgeSeconds;

    @Value("${jwt.key.access}") // 3
    private String accessKey;

    @Value("${jwt.key.refresh}") // 4
    private String refreshKey;

    private static final String ROLE_TYPES = "ROLE_TYPES";
    private static final String MEMBER_ID = "MEMBER_ID";


    public String createAccessToken(PrivateClaims privateClaims) {
        return jwtHandler.createToken(accessKey,
                Map.of(MEMBER_ID, privateClaims.getMemberId(), ROLE_TYPES, privateClaims.getRoleTypes()),
                accessTokenMaxAgeSeconds);
    }


    public String createRefreshToken(PrivateClaims privateClaims, String email) {
        String refreshToken = jwtHandler.createToken(refreshKey,
                Map.of(MEMBER_ID, privateClaims.getMemberId(), ROLE_TYPES, privateClaims.getRoleTypes()),
                refreshTokenMaxAgeSeconds);
        redisService.setValues(email, refreshToken, Duration.ofDays(refreshTokenMaxAgeSeconds));
        return refreshToken;
    }


    public Optional<PrivateClaims> parseRefreshToken(String token, String email) throws BaseException {
        return jwtHandler.checkRefreshToken(refreshKey, token, email).map(claims -> convert(claims));
    }

    private PrivateClaims convert(Claims claims) {
        return new PrivateClaims(claims.get(MEMBER_ID, String.class), UserRole.valueOf(claims.get(ROLE_TYPES, String.class)));
    }

    /**
     * validate ACCESS Token
     * doFilter 에서 쓰임
     */
    public Authentication validateToken(HttpServletRequest request, String token) throws BadRequestException {
        String exception = "exception";

        try {

            if (!isDefaultToken(token)) {
                Jwts.parser().setSigningKey(accessKey.getBytes()).parseClaimsJws(jwtHandler.untype(token));
                return getAuthentication(token);
            } else {
                return getAuthentication(jwtHandler.untype(token).substring(1));
            }
             //loadByUserName 후 Authentication 형식인 CustomAuthenticationToken 반환 !!
        } catch (BadRequestException e) {
            request.setAttribute(exception, "토큰을 입력해주세요. (앞에 'Bearer ' 포함)");
        } catch (MalformedJwtException | SignatureException | UnsupportedJwtException e) {
            request.setAttribute(exception, "잘못된 토큰입니다."); //토큰의 형식을 확인하세요. Bearer 없음
        } catch (ExpiredJwtException e) {
            request.setAttribute(exception, "토큰이 만료되었습니다.");
        } catch (IllegalArgumentException e) { //Authorization, Bearer
            request.setAttribute(exception, "토큰을 입력해주세요.");
        } catch (JwtException e) {
            e.printStackTrace();
            request.setAttribute(exception, "토큰을 확인해주세요."); //추가
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(exception, "general exception"); //추가
        }
        return null;
    }

    /**
     * loadUserByUsername 으로 UserDetail 반환
     */
    private Authentication getAuthentication(String token) {
        CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(token);
        return new CustomAuthenticationToken(userDetails, userDetails.getAuthorities());
    }

    public Boolean isDefaultToken(String token) {
        List<String> defaultTokenList = Arrays.asList("Bearer mingle-user", "Bearer mingle-admin", "Bearer mingle-ksa", "Bearer mingle-freshman");
//        String jwtcheck = jwtHandler.untype(token);
//        String untypedToken = jwtHandler.untype(token).substring(1); //Bearer 띄어쓰기
        if (defaultTokenList.stream().noneMatch(it -> it.equals(token))) {
            return false;
        } else {
            return true;
        }
    }


    @Getter
    @AllArgsConstructor
    public static class PrivateClaims {
        private String memberId;
        private UserRole roleTypes;
    }
}
