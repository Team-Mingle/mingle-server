package community.mingle.app.src.post;


import community.mingle.app.config.BaseException;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.post.model.GetTotalBestPostsResponse;
import community.mingle.app.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static community.mingle.app.config.BaseResponseStatus.*;
import static java.time.LocalTime.now;

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

    public List<TotalPost> findTotalPostWithMemberLikeComment() throws BaseException{
        try{

            List<TotalPost> totalPosts = postRepository.findTotalPostWithMemberLikeComment();

            return totalPosts;
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }


    }

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
