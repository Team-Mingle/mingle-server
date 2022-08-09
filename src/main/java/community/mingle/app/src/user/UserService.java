package community.mingle.app.src.user;

import community.mingle.app.config.BaseException;
import community.mingle.app.src.domain.Member;
import community.mingle.app.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static community.mingle.app.config.BaseResponseStatus.DATABASE_ERROR;
import static community.mingle.app.config.BaseResponseStatus.EMPTY_JWT;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;


    /**
     * 2.6 유저 삭제 api
     */
    //@Transactional
    public void  deleteMember(Long memberIdx) throws BaseException {
        Long memberIdByJwt;
        try {
            memberIdByJwt = jwtService.getUserIdx();
        } catch (Exception e) {
            throw new BaseException(EMPTY_JWT);
        }

        try {
            Member member = userRepository.findMemberbyId(memberIdByJwt);

            userRepository.deleteMember(memberIdx);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }


}
