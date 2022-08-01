package community.mingle.app.src.member;

import com.fasterxml.jackson.databind.ser.Serializers;
import community.mingle.app.config.BaseException;
import community.mingle.app.src.auth.AuthRepository;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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


    public List<UnivPost> getUnivScraps() throws BaseException {
        Long userIdByJwt = jwtService.getUserIdx();
        Member member = memberRepository.findMember(userIdByJwt);

        try {
            List<UnivPost> scraps = memberRepository.findUnivScraps(member.getId());
            return scraps;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<TotalPost> getTotalScraps() throws BaseException {
        Long userIdByJwt = jwtService.getUserIdx();
        Member member = memberRepository.findMember(userIdByJwt);

        try {
            List<TotalPost> scraps = memberRepository.findTotalScraps(member.getId());
            return scraps;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<TotalPost> getTotalPosts() throws BaseException {
        Long userIdByJwt = jwtService.getUserIdx();
        try {
            List<TotalPost> posts = memberRepository.findTotalPosts(userIdByJwt);
            return posts;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<UnivPost> getUnivPosts() throws BaseException {
        Long userIdByJwt = jwtService.getUserIdx();
        try {
            List<UnivPost> posts = memberRepository.findUnivPosts(userIdByJwt);
            return posts;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<TotalPost> getTotalComments() throws BaseException {
        Long userIdByJwt = jwtService.getUserIdx();
        try {
            List<TotalPost> comments = memberRepository.findTotalComments(userIdByJwt);
            return comments;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<UnivPost> getUnivComments() throws BaseException {
        Long userIdByJwt = jwtService.getUserIdx();
        try {
            List<UnivPost> comments = memberRepository.findUnivComments(userIdByJwt);
            return comments;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
