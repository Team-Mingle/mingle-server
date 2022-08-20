package community.mingle.app.src.post;

import community.mingle.app.config.BaseException;
import community.mingle.app.src.auth.TokenService;
import community.mingle.app.src.domain.*;
import community.mingle.app.src.domain.Total.*;
import community.mingle.app.src.domain.Univ.*;
import community.mingle.app.src.post.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import community.mingle.app.utils.JwtService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
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
     * 3.4 페이징 테스트
     */
    public List<TotalPost> findTotalPostByPaging(int category, Long postId) throws BaseException {
        List<TotalPost> totalPostList = postRepository.findTotalPostByPaging(category, postId);
        if (totalPostList.size() == 0) {
            throw new BaseException(EMPTY_POSTS_LIST);
        }
        return totalPostList;
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
     * 3.6 통합 게시물 작성 API
     */
    @Transactional

    public PostCreateResponse createTotalPost (PostCreateRequest postCreateRequest) throws BaseException{
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
            TotalPost totalPost = TotalPost.createTotalPost(member, category, postCreateRequest);
            Long id = postRepository.save(totalPost);
            return new PostCreateResponse(id);
        } catch (Exception e) {
            throw new BaseException(CREATE_FAIL_POST);
        }
    }

    /**
     * 3.7 학교 게시물 작성 API
     */
    @Transactional
    public PostCreateResponse createUnivPost (PostCreateRequest postCreateRequest) throws BaseException{
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
            UnivPost univPost = UnivPost.createUnivPost(member, category, postCreateRequest);
            Long id = postRepository.save(univPost);
            return new PostCreateResponse(id);
        } catch (Exception e) {
            throw new BaseException(CREATE_FAIL_POST);
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
                        .map(p -> new TotalCocommentDto(p, postRepository.findTotalComment(p.getMentionId()), memberIdByJwt))
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
     * 3.10.1 학교 게시물 상세 - 게시물 API
     */
    @Transactional(readOnly = true)
    public UnivPostDTO getUnivPost(Long postId) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();  // jwtService 의 메소드 안에서 throw 해줌 -> controller 로 넘어감
        Member member;
        member = postRepository.findMemberbyId(memberIdByJwt);
        boolean isMyPost = false, isLiked = false, isScraped = false;
        try {
            UnivPost univPost = postRepository.getUnivPostById(postId);
            if (univPost.getMember().getId() == memberIdByJwt) {
                isMyPost = true;
            }
            if (postRepository.checkUnivPostIsLiked(postId, memberIdByJwt) == true) {
                isLiked = true;
            }
            if (postRepository.checkUnivPostIsScraped(postId, memberIdByJwt) == true) {
                isScraped = true;
            }
            UnivPostDTO univPostDTO = new UnivPostDTO(univPost, isMyPost, isLiked, isScraped);
            return univPostDTO;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }



    /**
     * 3.10.2 학교 게시물 상세 - 댓글 API
     */
    @Transactional(readOnly = true)
    public List<UnivCommentDTO> getUnivComments(Long postId) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();  // jwtService 의 메소드 안에서 throw 해줌 -> controller 로 넘어감
        Member member;
        member = postRepository.findMemberbyId(memberIdByJwt);
        try {
            //1. postId 의 댓글, 대댓글 리스트 각각 가져오기
            List<UnivComment> univComments = postRepository.getUnivComments(postId); //댓글
            List<UnivComment> univCoComments = postRepository.getUnivCoComments(postId); //대댓글
            //2. 댓글 + 대댓글 DTO 생성
            List<UnivCommentDTO> univCommentDTOList = new ArrayList<>();
            //3. 댓글 리스트 돌면서 댓글 하나당 대댓글 리스트 넣어서 합쳐주기
            for (UnivComment c : univComments) {
                //parentComment 하나당 해당하는 UnivComment 타입의 대댓글 찾아서 리스트 만들기
                List<UnivComment> CoCommentList = univCoComments.stream()
                        .filter(cc -> c.getId().equals(cc.getParentCommentId()))
                        .collect(Collectors.toList());

//                postRepository.checkCoCommentLiked(CoCommentList); 성능 저하. for문 돌때마다 쿼리문 나감

                //댓글 하나당 만들어진 대댓글 리스트를 대댓글 DTO 형태로 변환
                List<UnivCoCommentDTO> coCommentDTO = CoCommentList.stream()
                        .map(cc -> new UnivCoCommentDTO(c, cc, memberIdByJwt))
                        .collect(Collectors.toList());
                /** 쿼리문 나감. 결론: for 문 안에서 쿼리문 대신 DTO 안에서 해결 */
                //boolean isLiked = postRepository.checkCommentIsLiked(c.getId(), memberIdByJwt);
                //4. 댓글 DTO 생성 후 최종 DTOList 에 넣어주기
                UnivCommentDTO univCommentDTO = new UnivCommentDTO(c, coCommentDTO, memberIdByJwt);
                univCommentDTOList.add(univCommentDTO);
            }
            return univCommentDTOList;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    
    /**
     * 3.11 통합 게시물 수정 API
     */
    @Transactional
    public void updateTotalPost (Long id, PatchUpdatePostRequest patchUpdatePostRequest) throws BaseException {
        Member member;
        TotalPost totalPost;
        Long memberIdByJwt = jwtService.getUserIdx();
        member = postRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(USER_NOT_EXIST);
        }

        totalPost = postRepository.findTotalPostById(id);
        if (totalPost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }


        if (memberIdByJwt != totalPost.getMember().getId()) {
            throw new BaseException(MODIFY_NOT_AUTHORIZED);
        }
        try {
            totalPost.updateTotalPost(patchUpdatePostRequest);
        } catch (Exception e) {
            throw new BaseException(MODIFY_FAIL_POST);
        }
    }


    /**
     * 3.12 학교 게시물 수정 API
     */
    @Transactional
    public void updateUnivPost (Long id, PatchUpdatePostRequest patchUpdatePostRequest) throws BaseException {
        Member member;
        UnivPost univPost;
        Long memberIdByJwt = jwtService.getUserIdx();
        member = postRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(USER_NOT_EXIST);
        }

        try {
            univPost = postRepository.findUnivPostById(id);
        } catch (Exception e) {
            throw new BaseException(POST_NOT_EXIST);
        }

        if (memberIdByJwt != univPost.getMember().getId()) {
            throw new BaseException(MODIFY_NOT_AUTHORIZED);
        }
        try {
            univPost.updateUnivPost(patchUpdatePostRequest);
        } catch (Exception e) {
            throw new BaseException(MODIFY_FAIL_POST);
        }
    }

    /**
     * 3.13 통합 게시물 삭제 API
     */
    @Transactional
    public void deleteTotalPost (Long id) throws BaseException{
        Member member;
        TotalPost totalPost;
        Long memberIdByJwt = jwtService.getUserIdx();
        member = postRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(USER_NOT_EXIST);
        }

        totalPost = postRepository.findTotalPostById(id);
        if (totalPost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }

        if (memberIdByJwt != totalPost.getMember().getId()) {
            throw new BaseException(MODIFY_NOT_AUTHORIZED);
        }
        try {
            List <TotalComment> totalComments = postRepository.findAllTotalComment(id);
            for (TotalComment c: totalComments) {
                c.deleteTotalComment();
            }
            totalPost.deleteTotalPost();
        } catch (Exception e) {
            throw new BaseException(DELETE_FAIL_POST);
        }
    }

    /**
     * 3.14 학교 게시물 삭제 API
     */
    @Transactional
    public void deleteUnivPost (Long id) throws BaseException{
        Member member;
        UnivPost univPost;
        Long memberIdByJwt = jwtService.getUserIdx();
        member = postRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(USER_NOT_EXIST);
        }


        univPost = postRepository.findUnivPostById(id);
        if (univPost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }

        if (memberIdByJwt != univPost.getMember().getId()) {
            throw new BaseException(MODIFY_NOT_AUTHORIZED);
        }
        try {
            List <UnivComment> univComments = postRepository.findAllUnivComment(id);
            for (UnivComment c: univComments) {
                c.deleteUnivComment();
            }
            univPost.deleteUnivPost();
        } catch (Exception e) {
            throw new BaseException(DELETE_FAIL_POST);
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
            TotalPost totalpost = postRepository.findTotalPostById(postIdx);
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
     * 3.16 학교 게시물 좋아요 api
     */
    @Transactional
    public PostLikesUnivResponse likesUnivPost(Long postIdx) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();

        UnivPost univpost = postRepository.findUnivPostById(postIdx);
        if (univpost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }

        try {
            Member member = postRepository.findMemberbyId(memberIdByJwt);

            UnivPostLike univPostLike = UnivPostLike.likesUnivPost(univpost, member);
            Long id = postRepository.save(univPostLike);
            int likeCount = univpost.getUnivPostLikes().size();
            return new PostLikesUnivResponse(id, likeCount);

        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 3.17 통합 게시물 좋아요 취소 api
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
     * 3.18 학교 게시물 좋아요 취소 api
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
     * 3.19 통합 게시물 스크랩 api
     */
    @Transactional
    public PostScrapTotalResponse scrapTotalPost(Long postIdx) throws BaseException {
        Long memberIdByJwt;

        memberIdByJwt = jwtService.getUserIdx();

        TotalPost totalpost = postRepository.findTotalPostById(postIdx);
        if (totalpost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }

        try {
            Member member = postRepository.findMemberbyId(memberIdByJwt);

            TotalPostScrap totalPostScrap = TotalPostScrap.scrapTotalPost(totalpost, member);
            Long id = postRepository.save(totalPostScrap);
            int scrapCount = totalpost.getTotalPostScraps().size();
            return new PostScrapTotalResponse(id, scrapCount);

        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 3.20 학교 게시물 스크랩 api
     */
    @Transactional
    public PostScrapUnivResponse scrapUnivPost(Long postIdx) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();

        UnivPost univpost = postRepository.findUnivPostById(postIdx);
        if (univpost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
        try {

            Member member = postRepository.findMemberbyId(memberIdByJwt);

            UnivPostScrap univPostScrap = UnivPostScrap.scrapUnivPost(univpost, member);
            Long id = postRepository.save(univPostScrap);
            int scrapCount = univpost.getUnivPostScraps().size();
            return new PostScrapUnivResponse(id, scrapCount);

        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);

        }
    }


    /**
     * 3.21 통합 게시물 스크랩 취소 api
     */
    @Transactional
    public void deleteScrapTotal(Long scrapIdx) throws BaseException {

        try {
            postRepository.deleteTotalScrap(scrapIdx);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 3.22 학교 게시물 스크랩 취소 api
     */
    @Transactional
    public void deleteScrapUniv(Long scrapIdx) throws BaseException {

        try {
            postRepository.deleteUnivScrap(scrapIdx);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }



}
