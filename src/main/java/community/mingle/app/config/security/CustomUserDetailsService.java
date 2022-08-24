package community.mingle.app.config.security;

import community.mingle.app.config.TokenHelper;
import community.mingle.app.config.handler.JwtHandler;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.member.MemberRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
//    private final MemberRepository memberRepository;
//    private final TokenHelper accessTokenHelper;
    private final JwtHandler jwtHandler;


//    @Override
//    public CustomUserDetails loadUserByUsername(String parsedToken) throws UsernameNotFoundException {
////        Member member = memberRepository.findMember(Long.valueOf(userId));
////        return new CustomUserDetails(String.valueOf(member.getId()), List.of(new SimpleGrantedAuthority(member.getRole())));
//
////        return accessTokenHelper.accessParse(token).map(privateClaims -> convert(privateClaims))
////                .orElse(null);
//    }

    @Override
    public CustomUserDetails loadUserByUsername(String parsedToken) throws UsernameNotFoundException {
        return convert(parsedToken);

    }

    private CustomUserDetails convert(String parsedToken) {
        Optional<TokenHelper.PrivateClaims> privateClaims = jwtHandler.parseToken(parsedToken);
        return new CustomUserDetails(privateClaims.get().getMemberId(), new SimpleGrantedAuthority(privateClaims.get().getRoleTypes()));
    }
}
