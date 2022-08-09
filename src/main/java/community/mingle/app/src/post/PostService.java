package community.mingle.app.src.post;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.domain.Banner;
import community.mingle.app.src.domain.Category;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.post.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import community.mingle.app.config.BaseException;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Total.TotalPostLike;
import community.mingle.app.src.domain.Total.TotalPostScrap;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.Univ.UnivPostLike;
import community.mingle.app.src.domain.Univ.UnivPostScrap;
import community.mingle.app.src.post.model.*;
import community.mingle.app.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.util.List;
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
    public List<Banner> findBanner() throws BaseException {
        try {
            List<Banner> banner = postRepository.findBanner();
            return banner;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 3.2 홍콩 배스트 게시판 API
     */
    public List<TotalPost> findTotalPostWithMemberLikeComment() throws BaseException {
        List<TotalPost> totalPosts = postRepository.findTotalPostWithMemberLikeComment();
        if (totalPosts.size() == 0) {
            throw new BaseException(EMPTY_BEST_POSTS);
        }
        return totalPosts;
    }

    /**
     * 3.3 학교 베스트 게시판 API --> try-catch 설명
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
    public List<TotalPost> findTotalPost(int category) throws BaseException {
        List<TotalPost> getAll = postRepository.findTotalPost(category);
        if (getAll.size() == 0) {
            throw new BaseException(EMPTY_POSTS_LIST);
        }
        return getAll;
    }

    /**
     * 3.5 학교 게시판 리스트 API
     */
    public List<UnivPost> findUnivPost(int category) throws BaseException {
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

        List<UnivPost> getUnivAll = postRepository.findUnivPost(category);
        if (getUnivAll.size() == 0) {
            throw new BaseException(EMPTY_POSTS_LIST);
        }
        return getUnivAll;
    }


    /**
     * 3.5 게시물 작성 API
     */
    @Transactional
    public PostCreateResponse createPost(PostCreateRequest postCreateRequest) throws BaseException {
        Member member;
        Category category;
        Long memberIdByJwt = jwtService.getUserIdx();
        member = postRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(USER_NOT_EXIST);
        }
        try {
            category = postRepository.findCategoryById(postCreateRequest.getCategoryId());
        } catch (Exception exception) {
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
     * 3.15 통합 게시물 좋아요 api
     */
    @Transactional
    public PostLikesTotalResponse likesTotalPost(Long postIdx) throws BaseException {
        Long memberIdByJwt;
        try {
            memberIdByJwt = jwtService.getUserIdx();
        } catch (Exception e) {
            throw new BaseException(EMPTY_JWT);
        }
        try {
            TotalPost totalpost = postRepository.findTotalPostbyId(postIdx);
            Member member = postRepository.findMemberbyId(memberIdByJwt);


            TotalPostLike totalPostLike = TotalPostLike.likesTotalPost(totalpost, member);
            Long id = postRepository.save(totalPostLike);
            int likeCount = totalpost.getTotalPostLikes().size();
            return new PostLikesTotalResponse(id, likeCount);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 통합 게시물 좋아요 취소 api
     */

    @Transactional
    public void  unlikeTotal(Long likeIdx) throws BaseException {

        try {
            //totalPostLike.deleteLike(likeIdx);
            postRepository.deleteTotalLike(likeIdx);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }

    /**
     * 학교 게시물 좋아요 취소 api
     */

    @Transactional
    public void  unlikeUniv(Long likeIdx) throws BaseException {

        try {
            postRepository.deleteUnivLike(likeIdx);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }


    /**
     * 3.15 학교 게시물 좋아요 api
     */
    @Transactional
    public PostLikesUnivResponse likesUnivPost(Long postIdx) throws BaseException {
        Long memberIdByJwt;
        try {
            memberIdByJwt = jwtService.getUserIdx();
        } catch (Exception e) {
            throw new BaseException(EMPTY_JWT);
        }
        try {
            UnivPost univpost = postRepository.findUnivPostbyId(postIdx);
            Member member = postRepository.findMemberbyId(memberIdByJwt);


            UnivPostLike univPostLike = UnivPostLike.likesUnivPost(univpost, member);
            Long id = postRepository.save(univPostLike);
            int likeCount = univpost.getUnivPostLikes().size();
            return new PostLikesUnivResponse(id, likeCount);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }





    /**
     * 3.17 통합 게시물 스크랩 api
     */
    @Transactional
    public PostScrapTotalResponse scrapTotalPost(Long postIdx) throws BaseException {
        Long memberIdByJwt;
        try {
            memberIdByJwt = jwtService.getUserIdx();
        } catch (Exception e) {
            throw new BaseException(EMPTY_JWT);
        }
        try {
            TotalPost totalpost = postRepository.findTotalPostbyId(postIdx);
            Member member = postRepository.findMemberbyId(memberIdByJwt);


            TotalPostScrap totalPostScrap = TotalPostScrap.scrapTotalPost(totalpost, member);
            Long id = postRepository.save(totalPostScrap);
            int scrapCount = totalpost.getTotalPostScraps().size();
            return new PostScrapTotalResponse(id, scrapCount);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 3.18 학교 게시물 스크랩 api
     */
    @Transactional
    public PostScrapUnivResponse scrapUnivPost(Long postIdx) throws BaseException {
        Long memberIdByJwt;
        try {
            memberIdByJwt = jwtService.getUserIdx();
        } catch (Exception e) {
            throw new BaseException(EMPTY_JWT);
        }
        try {
            UnivPost univpost = postRepository.findUnivPostbyId(postIdx);
            Member member = postRepository.findMemberbyId(memberIdByJwt);


            UnivPostScrap univPostScrap = UnivPostScrap.scrapUnivPost(univpost, member);
            Long id = postRepository.save(univPostScrap);
            int scrapCount = univpost.getUnivPostScraps().size();
            return new PostScrapUnivResponse(id, scrapCount);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 3.9.1 통합 게시물 상세 - 게시물 API
     */
    @Transactional(readOnly = true)
    public TotalPost getTotalPost(Long id) throws BaseException{

        TotalPost totalPost = postRepository.getTotalPostbyId(id);
        return totalPost;
    }

    /**
     * 3.9.1 통합 게시물 상세 - 게시물 API
     */
    @Transactional(readOnly = true)
    public TotalPostDto getTotalPostDto(TotalPost totalPost) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        boolean isMyPost = false;
        boolean isLiked = false;
        boolean isScraped = false;
        try {
            if (totalPost.getMember().getId() == memberIdByJwt) {
                isMyPost = true;
            }
            if (postRepository.checkTotalIsLiked(totalPost.getId(), memberIdByJwt) == true) {
                isLiked = true;
            }
            if (postRepository.checkTotalIsScraped(totalPost.getId(), memberIdByJwt) == true) {
                isScraped = true;
            }
        } catch (Exception e) {
        throw new BaseException(DATABASE_ERROR);
        }
        TotalPostDto totalPostDto = new TotalPostDto(totalPost, isMyPost, isLiked, isScraped);

        return totalPostDto;
    }

    /**
     * 3.9.2 통합 게시물 상세 - 댓글 API
     */
    @Transactional(readOnly = true)
    public List<TotalCommentDto> getTotalCommentList(Long id) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        try {
            List<TotalComment> totalCommentList = postRepository.getTotalComments(id);
            List<TotalComment> totalCocommentList = postRepository.getTotalCocomments(id);
            List<TotalCommentDto> totalCommentDtoList = new ArrayList<>();
            for (TotalComment tc : totalCommentList) {
                List<TotalComment> coComments = totalCocommentList.stream()
                        .filter(obj -> tc.getId().equals(obj.getParentCommentId()))
                        .collect(Collectors.toList());
                List<TotalCocommentDto> coCommentDtos = coComments.stream()
                        .map(p -> new TotalCocommentDto(p, tc, memberIdByJwt))
                        .collect(Collectors.toList());

//            boolean isLiked = postRepository.checkCommentIsLiked(tc.getId(), memberIdByJwt);
                TotalCommentDto totalCommentDto = new TotalCommentDto(tc, coCommentDtos, memberIdByJwt);
                totalCommentDtoList.add(totalCommentDto);
            }
            return totalCommentDtoList;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }


    /**
     * 통합 게시물 스크랩 취소 api
     */

    @Transactional
    public void deleteScrapTotal(Long scrapIdx) throws BaseException {

        try {
            postRepository.deleteTotalScrap(scrapIdx);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }


    /**
     * 학교 게시물 스크랩 취소 api
     */

    @Transactional
    public void deleteScrapUniv(Long scrapIdx) throws BaseException {

        try {
            postRepository.deleteUnivScrap(scrapIdx);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }


    @Transactional(readOnly = true)
    public UnivPostDTO getUnivPostDto(UnivPost univPost) throws BaseException{
        try {
            Long memberIdByJwt = jwtService.getUserIdx();
            boolean isMyPost = false;
            boolean isLiked = false;
            boolean isScraped = false;
            if (univPost.getMember().getId() == memberIdByJwt) {
                isMyPost = true;
            }
            if (postRepository.checkUnivIsLiked(univPost.getId(), memberIdByJwt) == true) {
                isLiked = true;
            }
            if (postRepository.checkUnivIsScraped(univPost.getId(), memberIdByJwt) == true) {
                isScraped = true;
            }
            UnivPostDTO result = new UnivPostDTO(univPost, isMyPost, isLiked, isScraped);
            return result;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
