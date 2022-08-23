package community.mingle.app.config.handler;

import community.mingle.app.config.secret.Secret;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.server.RequestPath.parse;

@Component
public class JwtHandler {

    private String type = "Bearer";

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

    public Optional<Claims> parse(String key, String token) {
        try {
            return Optional.of(Jwts.parser().setSigningKey(key.getBytes()).parseClaimsJws(untype(token)).getBody());
        } catch (JwtException e) {
            return Optional.empty();
        }
    }

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

    private String untype(String token) {
        return token.substring(type.length());
    }

}
