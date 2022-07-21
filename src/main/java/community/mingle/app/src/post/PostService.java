package community.mingle.app.src.post;


import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import community.mingle.app.config.BaseException;
import community.mingle.app.src.domain.Member;
import community.mingle.app.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static community.mingle.app.config.BaseResponseStatus.*;

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


    /**
     * 2.3 학교 베스트 게시판 API
     */
    public List<UnivPost> findAllWithMemberLikeCommentCount() throws BaseException {
        try {
            Long memberIdByJwt = jwtService.getUserIdx();
            Member member = postRepository.findMemberbyId(memberIdByJwt);
            List<UnivPost> univPosts = postRepository.findAllWithMemberLikeCommentCount(member);
            return univPosts;
        } catch (BaseException e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 2.4 광장 게시판 리스트 API
     */


    /**
     * 2.5 게시물 작성 API
     */
}
