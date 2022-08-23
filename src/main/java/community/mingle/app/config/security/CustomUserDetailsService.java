package community.mingle.app.config.security;

import community.mingle.app.config.TokenHelper;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
//    private final MemberRepository memberRepository;
    @Autowired
    private final TokenHelper accessTokenHelper;


    @Override
    public CustomUserDetails loadUserByUsername(String token) throws UsernameNotFoundException {
//        Member member = memberRepository.findMember(Long.valueOf(userId));
//        return new CustomUserDetails(String.valueOf(member.getId()), List.of(new SimpleGrantedAuthority(member.getRole())));
        return accessTokenHelper.accessParse(token).map(this::convert)
                .orElse(null);
    }

    private CustomUserDetails convert(TokenHelper.PrivateClaims privateClaims) {
        return new CustomUserDetails(privateClaims.getMemberId(), new SimpleGrantedAuthority(privateClaims.getRoleTypes()));

    }
}
