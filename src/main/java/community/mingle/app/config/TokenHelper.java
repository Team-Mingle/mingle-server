package community.mingle.app.config;


import community.mingle.app.config.handler.JwtHandler;
import community.mingle.app.utils.RedisService;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class TokenHelper {

    private final JwtHandler jwtHandler;
    private final RedisService redisService;
    private final String key;
    private final long maxAgeSeconds;

//    private static final String SEP = ",";
    private static final String ROLE_TYPES = "ROLE_TYPES";
    private static final String MEMBER_ID = "MEMBER_ID";

    public String createAccessToken(PrivateClaims privateClaims) {
        return jwtHandler.createToken(key,
                Map.of(MEMBER_ID, privateClaims.getMemberId(), ROLE_TYPES, privateClaims.getRoleTypes()),
                maxAgeSeconds);
    }


    public String createRefreshToken(PrivateClaims privateClaims) {
        String refreshToken = jwtHandler.createToken(key,
                Map.of(MEMBER_ID, privateClaims.getMemberId(), ROLE_TYPES, privateClaims.getRoleTypes()),
                maxAgeSeconds);
        redisService.setValues(privateClaims.getMemberId(), refreshToken, maxAgeSeconds);
    }


    public Optional<PrivateClaims> parse(String token) {
        return jwtHandler.parse(key, token).map(this::convert);
    }

    private PrivateClaims convert(Claims claims) {
        return new PrivateClaims(claims.get(MEMBER_ID, String.class), claims.get(ROLE_TYPES, String.class));
    }

    @Getter
    @AllArgsConstructor
    public static class PrivateClaims {
        private String memberId;
        private String roleTypes;
    }


}
