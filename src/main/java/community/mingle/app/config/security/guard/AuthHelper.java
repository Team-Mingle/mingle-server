package community.mingle.app.config.security.guard;

import community.mingle.app.config.security.CustomAuthenticationToken;
import community.mingle.app.config.security.CustomUserDetails;
import community.mingle.app.src.domain.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;


@Component
@Slf4j
public class AuthHelper {

    public boolean isAuthenticated() {
        return getAuthentication() instanceof CustomAuthenticationToken && getAuthentication().isAuthenticated();
    }

    public Long extractMemberId() {
        return Long.valueOf(getUserDetails().getUserId());
    }

    public Set<UserRole> extractMemberRoles() {
        return getUserDetails().getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .map(strAuth -> UserRole.valueOf(String.valueOf(strAuth)))
                .collect(Collectors.toSet());
    }

//    public boolean isAccessTokenType() {
//        return "access".equals(((CustomAuthenticationToken) getAuthentication()).getType());
//    }
//
//    public boolean isRefreshTokenType() {
//        return "refresh".equals(((CustomAuthenticationToken) getAuthentication()).getType());
//    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private CustomUserDetails getUserDetails() {
        return (CustomUserDetails) getAuthentication().getPrincipal();
    }
}
