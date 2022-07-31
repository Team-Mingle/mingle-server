package community.mingle.app.src.post;

import community.mingle.app.src.domain.Banner;
import community.mingle.app.src.domain.Category;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.post.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import community.mingle.app.config.BaseException;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.utils.JwtService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.app.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final JwtService jwtService;
    private final PostRepository postRepository;


    /**
     * 3.1 광고 배너 API
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
     * 3.2 홍콩 배스트 게시판 API
     */
    public List<TotalPost> findTotalPostWithMemberLikeComment() throws BaseException{
        List<TotalPost> totalPosts = postRepository.findTotalPostWithMemberLikeComment();
        if (totalPosts.size() == 0) {
            throw new BaseException(EMPTY_BEST_POSTS);
        }
        return totalPosts;
    }

    /**
     * 3.3 학교 베스트 게시판 API
     */
    public List<UnivPost> findAllWithMemberLikeCommentCount() throws BaseException {
//       try { //null 이 나오기 전에 쿼리문에서 에러가 남.
        Long memberIdByJwt = jwtService.getUserIdx();  // jwtService 의 메소드 안에서 throw 해줌 -> controller 로 넘어감
//        } catch (Exception e) {
//            throw new BaseException(EMPTY_JWT);
//        }
        Member member;
//        try {
        member = postRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(DATABASE_ERROR); //무조건 찾아야하는데 못찾을경우 (이미 jwt 에서 검증이 되기때문)
        }
//        } catch (Exception e) {
//            throw new BaseException(DATABASE_ERROR); //무조건 찾아야하는데 못찾을경우 (이미 jwt 에서 검증이 되기때문)
//        }

//        try {
        List<UnivPost> univPosts = postRepository.findAllWithMemberLikeCommentCount(member);
        if (univPosts.size() == 0) {
            throw new BaseException(EMPTY_BEST_POSTS);
        }
        return univPosts;
//        } catch (Exception e) {
//            throw new BaseException(EMPTY_BEST_POSTS);
//        }
    }

    /**
     * 3.4 광장 게시판 리스트 API
     */
    public List<TotalPost> findTotalPost(int category) throws BaseException{
        List<TotalPost> getAll = postRepository.findTotalPost(category);
        if (getAll.size() == 0) {
            throw new BaseException(EMPTY_POSTS_LIST);
        }
        return getAll;
    }


    /**
     * 3.5 게시물 작성 API
     */
    @Transactional
    public PostCreateResponse createPost (PostCreateRequest postCreateRequest) throws BaseException{
        Member member;
        Category category;
        Long memberIdByJwt = jwtService.getUserIdx();
        member = postRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(USER_NOT_EXIST);
        }
        try {
            category = postRepository.findCategoryById(postCreateRequest.getCategoryId());
        } catch(Exception exception){
            throw new BaseException(INVALID_POST_CATEGORY);
        }
        try {
            UnivPost univPost = UnivPost.createPost(member, category, postCreateRequest);
            Long id = postRepository.save(univPost);
            return new PostCreateResponse(id);
        } catch (Exception e) {
            throw new BaseException(CREATE_FAIL_POST);
        }
    }

    /**
     * 3.9
     */

    public TotalPost getTotalPost(Long id) throws BaseException{

        TotalPost totalPost = postRepository.getTotalPostbyId(id);
        return totalPost;
    }

    public TotalPostDto getTotalPostDto(TotalPost totalPost) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        boolean isMyPost = false;
        boolean isLiked = false;
        boolean isScraped = false;
        try {
            if (totalPost.getMember().getId() == memberIdByJwt) {
                isMyPost = true;
            }
            if (postRepository.checkIsLiked(totalPost.getId(), memberIdByJwt) == true) {
                isLiked = true;
            }
            if (postRepository.checkIsScraped(totalPost.getId(), memberIdByJwt) == true) {
                isScraped = true;
            }
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

        TotalPostDto totalPostDto = new TotalPostDto(totalPost, isMyPost, isLiked, isScraped);

        return totalPostDto;
    }

    public List<TotalCommentDto> getTotalCommentList(Long id) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        List<TotalComment> totalCommentList = postRepository.getTotalComments(id);
        List<TotalComment> totalCocommentList = postRepository.getTotalCocomments(id);
        List<TotalCommentDto> totalCommentDtoList = new ArrayList<>();
        final boolean isLikedcc;
        for (TotalComment tc : totalCommentList) {
            List<TotalComment> coComments = totalCocommentList.stream()
                    .filter(obj -> tc.getId().equals(obj.getParentCommentId()))
                    .collect(Collectors.toList());
            List<TotalCocommentDto> coCommentDtos = coComments.stream()
                    .map(p -> new TotalCocommentDto(p, tc, isLikedcc))
                    .collect(Collectors.toList());

            boolean isLiked = postRepository.checkCommentIsLiked(tc.getId(), memberIdByJwt);
            TotalCommentDto totalCommentDto = new TotalCommentDto(tc, coCommentDtos, isLiked);
            totalCommentDtoList.add(totalCommentDto);
        }
        return totalCommentDtoList;

    }








}
