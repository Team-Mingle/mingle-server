package community.mingle.app.config.security.guard;

import community.mingle.app.src.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberGuard {

    private final AuthHelper authHelper;

    public boolean check() {
        return authHelper.isAuthenticated() && hasAuthority();
    }

    private boolean hasAuthority() {
//        Long memberId = authHelper.extractMemberId();
        Set<String> memberRoles = authHelper.extractMemberRoles();
        //TODO 인증 타입을 UserRole로 통일 할까 아니면 security에서만 String으로 인증할까 (고민)
        return memberRoles.contains("USER") || memberRoles.contains("ADMIN") || memberRoles.contains("KSA") || memberRoles.contains("FRESHMAN") ;
    }
}
