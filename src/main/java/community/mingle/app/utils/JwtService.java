package community.mingle.app.utils;


import community.mingle.app.config.TokenHelper;
import community.mingle.app.config.BaseException;
import community.mingle.app.config.handler.JwtHandler;
import community.mingle.app.src.domain.UserRole;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static community.mingle.app.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final TokenHelper tokenHelper;
    private final JwtHandler jwtHandler;

    String type = "Bearer";

    @Value("${jwt.key.access}") // parse 할때
    private String accessKey;

    /**
     Header에서 AUTHORIZATION 으로 JWT 추출
     */
    public String getJwt(){ //resolveToken
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("Authorization");
    }


    /**
     header에서 받아온 JWT에서 userIdx 추출
     */
    public Long getUserIdx() throws BaseException{

        //1. JWT 추출
        String accessToken = untype(getJwt());
        if (tokenHelper.isDefaultToken("Bearer" + accessToken)) {
            switch (accessToken.substring(1)) {
                case "mingle-user":
                    return 657L;
                case "mingle-admin":
                    return 658L;
                case "mingle-ksa":
                    return 659L;
                case "mingle-freshman":
                    return 660L;
            }
        }
        // 2. userIdx 추출
        return Long.valueOf(
                Jwts.parser()
                        .setSigningKey(accessKey.getBytes())
                        .parseClaimsJws(accessToken) //io.jsonwebtoken.ExpiredJwtException: JWT expired at 2023-02-17T17:15:04Z. Current time: 2023-04-06T00:51:09Z, a difference of 4088165969 milliseconds.  Allowed clock skew: 0 milliseconds.
                        .getBody()
                        .get("MEMBER_ID", String.class));
    }


    private String untype(String token) throws BaseException{
        try {
            return token.substring(type.length());
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }




    public String getUserAuthority() throws BaseException{
        //1. JWT 추출
        String accessToken = untype(getJwt());
        // 2. 권한 추출
        return String.valueOf(
                Jwts.parser()
                        .setSigningKey(accessKey.getBytes())
                        .parseClaimsJws(accessToken)
                        .getBody().get("ROLE_TYPES", String.class));
    }





    /**
     * 주어진거
     */
//
//    /*
//    JWT 생성
//    @param userIdx
//    @return String
//     */
//    public String createJwt(Long userIdx) {
//        Date now = new Date();
//        return Jwts.builder()
//                .setIssuer("mingle.community")
//                .setHeaderParam("type","jwt")
//                .claim("userIdx",userIdx)
//                .setIssuedAt(now)
//                .setExpiration(new Date(System.currentTimeMillis()+1*(1000*60*60)))
//                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
//                .compact();
//    }
//
//    public String createRefreshJwt(Long userIdx) {
//        Date now = new Date();
//        return Jwts.builder()
//                .setIssuer("mingle.community")
//                .setHeaderParam("type","jwt")
//                .claim("userIdx",userIdx)
//                .setIssuedAt(now)
//                .setExpiration(new Date(System.currentTimeMillis()+1*(1000*60*60)))
//                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
//                .compact();
//    }



//    public Long getUserIdx() throws BaseException{
//
//        //1. JWT 추출
//        String accessToken = untype(getJwt());
//
//        if(accessToken == null || accessToken.length() == 0) {
//            throw new BaseException(EMPTY_JWT);
//        }
//
//        // 2. JWT parsing  -> getUserPk : jwt 에서 회원 구분 pk 추출
//        Jws<Claims> claims;
//        try{
//            claims = Jwts.parser()
//                    .setSigningKey(accessKey)
//                    .parseClaimsJws(accessToken);
//        } catch (Exception ignored) {
//            throw new BaseException(INVALID_JWT);
//        }
//
//        // 3. userIdx 추출
////        return claims.getBody().get("userIdx",Long.class);
//        return Long.valueOf(claims.getBody().get("MEMBER_ID", String.class));
//    }



}
