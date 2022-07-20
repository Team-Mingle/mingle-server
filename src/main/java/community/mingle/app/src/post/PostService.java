package community.mingle.app.src.post;


import community.mingle.app.config.BaseException;
import community.mingle.app.src.domain.Member;
import community.mingle.app.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static community.mingle.app.config.BaseResponseStatus.INVALID_USER_JWT;
import static community.mingle.app.config.BaseResponseStatus.PASSWORD_MATCH_ERROR;

@Service
@RequiredArgsConstructor
public class PostService {

    private final JwtService jwtService;
    private final PostRepository postRepository;


    /**
     * 2.1 광고 배너 API
     */


    /**
     * 2.2 홍콩 배스트 게시판 API
     */

//    public Member totalBests() throws  BaseException{
//        Long userIdxByJwt = jwtService.getUserIdx();
//        Member member = postRepository.findMemberbyId(userIdxByJwt);
//        if (member == null) {
//            throw new BaseException()
//        }
//
//    }

    /**
     * 2.3 학교 베스트 게시판 API
     */


    /**
     * 2.4 광장 게시판 리스트 API
     */


    /**
     * 2.5 게시물 작성 API
     */
}
