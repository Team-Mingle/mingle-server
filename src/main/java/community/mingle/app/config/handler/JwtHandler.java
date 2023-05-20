package community.mingle.app.config.handler;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponseStatus;
import community.mingle.app.config.TokenHelper;
import community.mingle.app.config.exception.BadRequestException;
import community.mingle.app.src.domain.UserRole;
import community.mingle.app.utils.RedisService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.server.RequestPath.parse;

@Component
@RequiredArgsConstructor
public class JwtHandler {

    private String type = "Bearer";

    private final RedisService redisService;

    @Value("${jwt.key.access}") // parse 할때
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


    /**
     * ===== 토큰 parse 후 privateClaim 으로 변환 =====
     */
    public Optional<TokenHelper.PrivateClaims> createPrivateClaim(String token) {
        return parseAccessToken(accessKey, token).map(claims -> convertClaim(claims));
    }

    /**
     * ㄴ 토큰 parse : getBody()
     */
    public Optional<Claims> parseAccessToken(String key, String token) {
        try {
            return Optional.of(Jwts.parser().setSigningKey(key.getBytes()).parseClaimsJws(untype(token)).getBody());
        } catch (JwtException e) {
            return Optional.empty();
        }
    }

    /**
     * ㄴ Claim -> privateClaim 변환
     */
    private TokenHelper.PrivateClaims convertClaim(Claims claims) {
        return new TokenHelper.PrivateClaims(claims.get("MEMBER_ID", String.class), claims.get("ROLE_TYPES", UserRole.class));
    }



    /**
     * ===== refresh Token 재발급할떄 validate =====
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
        }
    }


    public String untype(String token) throws BadRequestException{
        if (token.length() < 6) {
            throw new BadRequestException("토큰을 입력해주세요.");
        }
        return token.substring(type.length());
    }

}
