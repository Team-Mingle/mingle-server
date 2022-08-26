package community.mingle.app.config.handler;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponseStatus;
import community.mingle.app.config.TokenHelper;
import community.mingle.app.config.newexception.BadRequestException;
import community.mingle.app.config.security.CustomUserDetailsService;
import community.mingle.app.utils.RedisService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.server.RequestPath.parse;

@Component
@RequiredArgsConstructor
public class JwtHandler {

    private String type = "Bearer";
//    private final CustomUserDetailsService customUserDetailsService;

    private final RedisService redisService;

    @Value("${jwt.key.access}") // 3
    private String accessKey;

    @Value("${jwt.key.refresh}") // 4
    private String refreshKey;


    public String createToken(String key, Map<String, Object> privateClaims, long maxAgeSeconds) {
        Date now = new Date();
        return type + " " + Jwts.builder()
//                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + maxAgeSeconds * 1000L))
                .addClaims(privateClaims)
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();
    }

    /** validateToken */
    public Optional<Claims> parse(String key, String token) {
        String exception = "exception";

        try {
            return Optional.of(Jwts.parser().setSigningKey(key.getBytes()).parseClaimsJws(untype(token)).getBody());
//        } catch (MalformedJwtException | SignatureException | UnsupportedJwtException e) {
//            request.setAttribute(exception, "토큰의 형식을 확인하세요");
//        } catch (ExpiredJwtException e) {
//            request.setAttribute(exception, "토큰이 만료되었습니다.");
//        } catch (IllegalArgumentException e) {
//            request.setAttribute(exception, "JWT compact of handler are invalid");
//        }
        } catch (JwtException e) {
            return Optional.empty();
        }
    }


    public Optional<Claims> parseToken(String key, String token) {
        try {
            return Optional.of(Jwts.parser().setSigningKey(key.getBytes()).parseClaimsJws(untype(token)).getBody());
        } catch (JwtException e) {
            return Optional.empty();
        }
    }


    public Optional<TokenHelper.PrivateClaims> parseToken(String token) {
        return parseToken(accessKey, token).map(claims -> convert(claims));
    }

    private TokenHelper.PrivateClaims convert(Claims claims) {
        return new TokenHelper.PrivateClaims(claims.get("MEMBER_ID", String.class), claims.get("ROLE_TYPES", String.class));
    }


    /**
     * refresh Token 재발급
     */
    public Optional<Claims> checkRefreshToken(String key, String refreshToken, String email) throws BaseException { // userId = 암호화된 email
        String redisRefreshToken = redisService.getValues(email);
        if (!refreshToken.equals(redisRefreshToken)) {
            throw new BadRequestException("토큰 재발급에 실패하였습니다.");
        }

        try {
            return Optional.of(Jwts.parser().setSigningKey(key.getBytes()).parseClaimsJws(untype(refreshToken)).getBody());
        } catch (BadRequestException e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
//            return Optional.empty();
        }
    }


    /**
     * validateToken
     */
//    public Authentication validateToken(HttpServletRequest request, String token) {
//        String exception = "exception";
//
//        try {
//            Jwts.parser().setSigningKey(accessKey.getBytes()).parseClaimsJws(untype(token));
//            return getAuthentication(token);
//
//        } catch (MalformedJwtException | SignatureException | UnsupportedJwtException e) {
//            request.setAttribute(exception, "토큰의 형식을 확인하세요");
//        } catch (ExpiredJwtException e) {
//            request.setAttribute(exception, "토큰이 만료되었습니다.");
//        } catch (IllegalArgumentException e) {
//            request.setAttribute(exception, "JWT compact of handler are invalid");
////        } catch (JwtException e) {
////            return Optional.empty();
//        }
//        return null;
//    }
//
//    private Authentication getAuthentication(String token) {
//        UserDetails userDetails = customUserDetailsService.loadUserByUsername(token);
//        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
//    }

//    public String extractSubject(String encodedKey, String token) {
//        return parse(encodedKey, token).getBody().getSubject();
//    }
//
//    public boolean validate(String encodedKey, String token) {
//        try {
//            //토큰을 파싱하면서 에러가 생기면 유효하지 않은 토큰
//            parse(encodedKey, token);
//            return true;
//        } catch (JwtException e) {
//            return false;
//        }
//    }
//
//    private Jws<Claims> parse(String key, String token) {
//        //parser를 이용하여 사용된 key를 지정해주고 파싱을 수행해줌
//        //토큰 문자열에는 토큰의 타입도 포함돼있으므로 untype 메소드로 제거
//        return Jwts.parser()
//                .setSigningKey(key)
//                .parseClaimsJws(untype(token));
//    }

    public String untype(String token) throws BadRequestException{
        if (token.length() < 6) {
            throw new BadRequestException("토큰을 입력해주세요.");
        }
        return token.substring(type.length());
    }

}
