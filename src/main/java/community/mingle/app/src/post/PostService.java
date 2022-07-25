package community.mingle.app.src.post;


import community.mingle.app.src.auth.AuthRepository;
import community.mingle.app.src.auth.model.PostSignupResponse;
import community.mingle.app.src.domain.Banner;
import community.mingle.app.src.domain.Category;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.UnivName;
import community.mingle.app.src.post.model.PostCreateRequest;
import community.mingle.app.src.post.model.PostCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import community.mingle.app.config.BaseException;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.utils.JwtService;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Banner> findBanner() throws BaseException{
        try{
            List<Banner> banner = postRepository.findBanner();
            return banner;
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 2.2 홍콩 배스트 게시판 API
     */
    public List<TotalPost> findTotalPostWithMemberLikeComment() throws BaseException{
        try{
            List<TotalPost> totalPosts = postRepository.findTotalPostWithMemberLikeComment();
            return totalPosts;
        }catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 2.3 학교 베스트 게시판 API
     */
    public List<UnivPost> findAllWithMemberLikeCommentCount() throws BaseException {
        Long memberIdByJwt;
        try {
            memberIdByJwt = jwtService.getUserIdx();
        } catch (Exception e) {
            throw new BaseException(EMPTY_JWT);
        }

        try {
            Member member = postRepository.findMemberbyId(memberIdByJwt);
            List<UnivPost> univPosts = postRepository.findAllWithMemberLikeCommentCount(member);
            return univPosts;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 2.4 광장 게시판 리스트 API
     */
    public List<TotalPost> findTotalPost(int category) throws BaseException{
        try{
            List<TotalPost> getAll = postRepository.findTotalPost(category);
            return getAll;
        }catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 2.5 게시물 작성 API
     */
    @Transactional
    public PostCreateResponse createPost (PostCreateRequest postCreateRequest) throws BaseException{
        Long memberIdByJwt;
        try {
            memberIdByJwt = jwtService.getUserIdx();
        } catch (Exception e) {
            throw new BaseException(EMPTY_JWT);
        }
        try{

            Member member = postRepository.findMemberbyId(memberIdByJwt);

            Category category = postRepository.findCategoryById(postCreateRequest.getCategoryId());
            UnivPost univPost = UnivPost.createPost(member, category, postCreateRequest);
            Long id = postRepository.save(univPost);
            return new PostCreateResponse(id);

        }catch(Exception exception){
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);

        }
    }
}
