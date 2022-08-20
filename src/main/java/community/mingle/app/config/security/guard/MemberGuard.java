package community.mingle.app.config.security.guard;

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
        return memberRoles.contains("USER");

    }
}
