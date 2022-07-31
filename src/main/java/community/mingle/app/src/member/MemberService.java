package community.mingle.app.src.member;

import community.mingle.app.config.BaseException;
import community.mingle.app.src.auth.AuthRepository;
import community.mingle.app.src.domain.Member;
import community.mingle.app.utils.JwtService;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static community.mingle.app.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final JwtService jwtService;
    private final AuthRepository authRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public void modifyNickname(String nickname) throws BaseException {

        Long userIdByJwt = jwtService.getUserIdx();
        Member member = memberRepository.findMember(userIdByJwt);

        if (authRepository.findNickname(nickname) == true) {
            throw new BaseException(USER_EXISTS_NICKNAME);
        }

        try {
            member.modifyNickname(nickname);
        } catch (Exception e) {
            throw new BaseException(MODIFY_FAIL_NICKNAME);
        }
    }
}
