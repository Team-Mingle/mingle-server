package community.mingle.app.config;


import community.mingle.app.config.handler.JwtHandler;
import community.mingle.app.config.security.CustomAuthenticationToken;
import community.mingle.app.config.security.CustomUserDetails;
import community.mingle.app.config.security.CustomUserDetailsService;
import community.mingle.app.utils.RedisService;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenHelper {

    private final JwtHandler jwtHandler;
    private final CustomUserDetailsService customUserDetailsService;
    private final RedisService redisService;
//    private final String key;
//    private final long maxAgeSeconds;

    @Value("${jwt.max-age.access}") // 1
    private Long accessTokenMaxAgeSeconds;

    @Value("${jwt.max-age.refresh}") // 2
    private Long refreshTokenMaxAgeSeconds;

    @Value("${jwt.key.access}") // 3
    private String accessKey;

    @Value("${jwt.key.refresh}") // 4
    private String refreshKey;

//    private static final String SEP = ",";
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



//<<<<<<< HEAD
//    public Optional<PrivateClaims> refreshParse(String token) {
//        return jwtHandler.parse(refreshKey, token).map(claims -> convert(claims));
//=======
//    public Optional<PrivateClaims> accessParse(String token) {
//        return jwtHandler.parse(accessKey, token).map(claims -> convert(claims));
//    }

    public Optional<PrivateClaims> refreshParse(String token, String email) throws BaseException {
        return jwtHandler.checkRefreshToken(refreshKey, token, email).map(claims -> convert(claims));
//>>>>>>> 6c0c810f33103bcb6e0dd9e54fe6b32a8f080a5f
    }

    private PrivateClaims convert(Claims claims) {
        return new PrivateClaims(claims.get(MEMBER_ID, String.class), claims.get(ROLE_TYPES, String.class));
    }

    //추출
//    public Optional<PrivateClaims> parseToken(String token) {
//        return jwtHandler.parseToken(accessKey, token).map(claims -> convert(claims));
//    }

    /**
     * validateToken
     */
    public Authentication validateToken(HttpServletRequest request, String token) {
        String exception = "exception";

        try {
            Jwts.parser().setSigningKey(accessKey.getBytes()).parseClaimsJws(jwtHandler.untype(token));
            return getAuthentication(token);

        } catch (MalformedJwtException | SignatureException | UnsupportedJwtException e) {
            request.setAttribute(exception, "토큰의 형식을 확인하세요");
        } catch (ExpiredJwtException e) {
            request.setAttribute(exception, "토큰이 만료되었습니다.");
        } catch (IllegalArgumentException e) {
            request.setAttribute(exception, "JWT compact of handler are invalid");
//        } catch (JwtException e) {
//            return Optional.empty();
        }
        return null;
    }

    private Authentication getAuthentication(String token) {
        CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(token);
        return new CustomAuthenticationToken(userDetails, userDetails.getAuthorities());
    }


    @Getter
    @AllArgsConstructor
    public static class PrivateClaims {
        private String memberId;
        private String roleTypes;
    }
}
