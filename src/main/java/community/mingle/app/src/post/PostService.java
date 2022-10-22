package community.mingle.app.src.post;

import community.mingle.app.config.BaseException;
import community.mingle.app.src.domain.*;
import community.mingle.app.src.domain.Total.*;
import community.mingle.app.src.domain.Univ.*;
import community.mingle.app.src.firebase.FirebaseCloudMessageService;
import community.mingle.app.src.post.model.PatchUpdatePostRequest;
import community.mingle.app.src.post.model.PostCreateRequest;
import community.mingle.app.src.post.model.PostCreateResponse;
import community.mingle.app.src.post.model.*;
import community.mingle.app.utils.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.utils.JwtService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static community.mingle.app.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final JwtService jwtService;
    private final PostRepository postRepository;

    private final S3Service s3Service;

    private final FirebaseCloudMessageService fcmService;


    /**
     * 유저 토큰에서 학교 추출
     */
    public UnivName findUniv() throws BaseException {
        Member member;
        Long memberIdByJwt = jwtService.getUserIdx();
        member = postRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(USER_NOT_EXIST);
        }
        return member.getUniv();
    }

//    public int findUnivId() throws BaseException {
//        Member member;
//        Long memberIdByJwt = jwtService.getUserIdx();
//        member = postRepository.findMemberbyId(memberIdByJwt);
//        if (member == null) {
//            throw new BaseException(USER_NOT_EXIST);
//        }
//        return member.getUniv().getId();
//    }


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
     * 3.3 학교 베스트 게시판 API
     */
    public List<UnivPost> findAllWithMemberLikeCommentCount() throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();  // jwtService 의 메소드 안에서 throw 해줌 -> controller 로 넘어감
        Member member;
        member = postRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(DATABASE_ERROR); //무조건 찾아야하는데 못찾을경우 (이미 jwt 에서 검증이 되기때문)
        }
        List<UnivPost> univPosts = postRepository.findAllWithMemberLikeCommentCount(member);
        if (univPosts.size() == 0) {
            throw new BaseException(EMPTY_BEST_POSTS);
        }
        return univPosts;
    }


    /**
     * 3.4 광장 게시판 리스트 API
     */
    public List<TotalPost> findTotalPost(int category, Long postId) throws BaseException {
        List<TotalPost> totalPostList = postRepository.findTotalPost(category, postId);
        if (totalPostList.size() == 0) {
            throw new BaseException(EMPTY_POSTS_LIST);
        }
        return totalPostList;
    }


    /**
     * 3.5 학교 게시판 리스트 API
     */
    public List<UnivPost> findUnivPost(int category, Long postId, int univId) throws BaseException {
        List<UnivPost> getUnivAll = postRepository.findUnivPost(category, postId, univId);
        if (getUnivAll.size() == 0) {
            throw new BaseException(EMPTY_POSTS_LIST);
        }
        return getUnivAll;
    }


    /**
     * 3.6 통합 게시물 작성 API
     */
    @Transactional
    public PostCreateResponse createTotalPost(PostCreateRequest postCreateRequest) throws BaseException {
        Member member;
        Category category;
        Long memberIdByJwt = jwtService.getUserIdx();
        member = postRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(USER_NOT_EXIST);
        }
//        try {
//            category = postRepository.findCategoryById(postCreateRequest.getCategoryId());
//        } catch (Exception exception) {
//            throw new BaseException(INVALID_POST_CATEGORY);
//        }
        category = postRepository.findCategoryById(postCreateRequest.getCategoryId());
        if (category == null) {
            throw new BaseException(INVALID_POST_CATEGORY);
        }
        try {
            TotalPost totalPost = TotalPost.createTotalPost(member, category, postCreateRequest);
            Long id = postRepository.save(totalPost);

            List<String> fileNameList = null;

            for (MultipartFile image : postCreateRequest.getMultipartFile()) {
                if(image.isEmpty()) {
                    break;
                }
                else {
                    fileNameList = s3Service.uploadFile(postCreateRequest.getMultipartFile(), "total");
                    for (String fileName: fileNameList) {
                        TotalPostImage totalPostImage = TotalPostImage.createTotalPost(totalPost,fileName);
                        postRepository.save(totalPostImage);
                    }
                }
            }
            return new PostCreateResponse(id, fileNameList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(CREATE_FAIL_POST);
        }
    }


    /**
     * 3.7 학교 게시물 작성 API
     */
    @Transactional
    public PostCreateResponse createUnivPost(PostCreateRequest postCreateRequest) throws BaseException {
        Member member;
        Category category;
        Long memberIdByJwt = jwtService.getUserIdx();
        member = postRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(USER_NOT_EXIST);
        }
//        try {
        category = postRepository.findCategoryById(postCreateRequest.getCategoryId());
        if (category == null) {
            throw new BaseException(INVALID_POST_CATEGORY);
        }
//        } catch (Exception exception) {
//            throw new BaseException(INVALID_POST_CATEGORY);
//        }
        try {
            UnivPost univPost = UnivPost.createUnivPost(member, category, postCreateRequest);
            Long id = postRepository.save(univPost);
            List<String> fileNameList = null;
            System.out.println("겟 멀티플파일" + postCreateRequest.getMultipartFile());
            if (postCreateRequest.getMultipartFile()==null || postCreateRequest.getMultipartFile().isEmpty()) {
                UnivPostImage univPostImage = UnivPostImage.createTotalPost(univPost, null);
                postRepository.save(univPostImage);
            } else {
//                for (MultipartFile image : postCreateRequest.getMultipartFile()) {
//                    if (image.isEmpty()) {
//                        break;
//                    } else {
                fileNameList = s3Service.uploadFile(postCreateRequest.getMultipartFile(), "univ");
                for (String fileName : fileNameList) {
                    UnivPostImage univPostImage = UnivPostImage.createTotalPost(univPost, fileName);
                    postRepository.save(univPostImage);
                }
//                    }
//                }
            }

            return new PostCreateResponse(id, fileNameList);
        } catch (Exception e) {
            throw new BaseException(CREATE_FAIL_POST);
        }
    }



    /**
     * 3.8 카테고리
     *
     * @return
     */
    public List<GetPostCategoryResponse> getPostCategory() throws BaseException {
        try {
            String authority = jwtService.getUserAuthority();
            List<Category> postCategory = postRepository.getPostCategory();
            List<GetPostCategoryResponse> result = postCategory.stream()
                    .map(m -> new GetPostCategoryResponse(m))
                    .collect(Collectors.toList());
            if (authority.equals("USER")) {
                result.remove(4);
            }
            return result;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }



    /**
     * 3.9.1 통합 게시물 상세 - 게시물 API
     */
    @Transactional(readOnly = true)
    public TotalPost getTotalPost(Long id) throws BaseException {
        TotalPost totalPost = postRepository.getTotalPostbyId(id);
        if (totalPost.getStatus().equals(PostStatus.INACTIVE) || totalPost.getStatus().equals(PostStatus.REPORTED)) {
            throw new BaseException(REPORTED_DELETED_POST);
        }
        return totalPost;
    }

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
        TotalPost totalPost = postRepository.checkTotalPostDisabled(id);
        if (totalPost.getStatus().equals(PostStatus.REPORTED) || totalPost.getStatus().equals(PostStatus.INACTIVE)) {
            throw new BaseException(REPORTED_DELETED_POST);
        }
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
        UnivPost univPost = postRepository.getUnivPostById(postId);
        if (univPost.getStatus().equals(PostStatus.INACTIVE) || univPost.getStatus().equals(PostStatus.REPORTED)) {
            throw new BaseException(REPORTED_DELETED_POST);
        }
        try {
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
        UnivPost univPost = postRepository.checkUnivPostDisabled(postId);
        if (univPost.getStatus().equals(PostStatus.REPORTED) || univPost.getStatus().equals(PostStatus.INACTIVE)) {
            throw new BaseException(REPORTED_DELETED_POST);
        }
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


                //댓글 하나당 만들어진 대댓글 리스트를 대댓글 DTO 형태로 변환
                List<UnivCoCommentDTO> coCommentDTO = CoCommentList.stream()
                        .map(cc -> new UnivCoCommentDTO(postRepository.findUnivComment(cc.getMentionId()), cc, memberIdByJwt))
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
    public void updateTotalPost(Long id, PatchUpdatePostRequest patchUpdatePostRequest) throws BaseException {
        Member member;
        TotalPost totalPost;
        Long memberIdByJwt = jwtService.getUserIdx();
        member = postRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(USER_NOT_EXIST);
        }

        totalPost = postRepository.findTotalPostById(id);
        if (totalPost.getStatus().equals(PostStatus.INACTIVE) || totalPost.getStatus().equals(PostStatus.REPORTED)) {
            throw new BaseException(REPORTED_DELETED_POST);
        }
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
    public void updateUnivPost(Long id, PatchUpdatePostRequest patchUpdatePostRequest) throws BaseException {
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
        if (univPost.getStatus().equals(PostStatus.INACTIVE) || univPost.getStatus().equals(PostStatus.REPORTED)) {
            throw new BaseException(REPORTED_DELETED_POST);
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
    public void deleteTotalPost(Long id) throws BaseException {
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
            List<TotalComment> totalComments = postRepository.findAllTotalComment(id);
            List<TotalPostImage> totalPostImages = postRepository.findAllTotalImage(id);
            for (TotalComment c : totalComments) {
                c.deleteTotalComment();
            }
            for (TotalPostImage pi : totalPostImages) {
                pi.deleteTotalImage();

                String imgUrl = pi.getImgUrl();
                String fileName = imgUrl.substring(imgUrl.lastIndexOf(".com/total/") + 11);
                s3Service.deleteFile(fileName, "total");
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
    public void deleteUnivPost(Long id) throws BaseException {
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
            List<UnivComment> univComments = postRepository.findAllUnivComment(id);
            List<UnivPostImage> univPostImages = postRepository.findAllUnivImage(id);
            for (UnivComment c : univComments) {
                c.deleteUnivComment();
            }

            for (UnivPostImage pi : univPostImages) {
                pi.deleteUnivImage();

                String imgUrl = pi.getImgUrl();
                String fileName = imgUrl.substring(imgUrl.lastIndexOf(".com/univ/") + 10);
                s3Service.deleteFile(fileName, "univ");
            }
            univPost.deleteUnivPost();


        } catch (Exception e) {
            throw new BaseException(DELETE_FAIL_POST);
        }
    }


    /**
     * 3.15 통합 게시물 좋아요 api + 인기 게시물 알림 기능
     */
    @Transactional
    public PostLikesTotalResponse likesTotalPost(Long postIdx) throws BaseException {
        Long memberIdByJwt;
        try {
            memberIdByJwt = jwtService.getUserIdx();
        } catch (Exception e) {
            throw new BaseException(EMPTY_JWT);
        }
        TotalPost totalpost = postRepository.findTotalPostById(postIdx);
        if (totalpost.getStatus().equals(PostStatus.INACTIVE) || totalpost.getStatus().equals(PostStatus.REPORTED)) {
            throw new BaseException(REPORTED_DELETED_POST);
        }
        try {
            Member member = postRepository.findMemberbyId(memberIdByJwt);
            Member postMember = postRepository.findMemberbyId(postIdx);
            //좋아요 생성
            TotalPostLike totalPostLike = TotalPostLike.likesTotalPost(totalpost, member);
            Long id = postRepository.save(totalPostLike);
            int likeCount = totalpost.getTotalPostLikes().size();
            // 인기 게시물 알림 보내주기


            if (totalpost.getTotalPostLikes().size() == 10) {
                sendTotalPostNotification(totalpost, postMember);
            }
            return new PostLikesTotalResponse(id, likeCount);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void sendTotalPostNotification(TotalPost totalpost, Member postMember) throws IOException {
        List<TotalPost> recentPost = postRepository.findTotalPostWithMemberLikeComment();
        String title = "전체 게시글";

        if (recentPost.contains(totalpost) == true){
            String body = "인기 게시물로 지정되었어요";
//          fcmService.sendMessageTo(postMember.getFcmToken(), title, body);
        }
        else {
                return;
        }

    }



    /**
     * 3.16 학교 게시물 좋아요 api
     */
    @Transactional
    public PostLikesUnivResponse likesUnivPost(Long postIdx) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        UnivPost univpost = postRepository.findUnivPostById(postIdx);
        if (univpost.getStatus().equals(PostStatus.INACTIVE) || univpost.getStatus().equals(PostStatus.REPORTED)) {
            throw new BaseException(REPORTED_DELETED_POST);
        }

        if (univpost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
        try {
            Member member = postRepository.findMemberbyId(memberIdByJwt);
            Member postMember = postRepository.findMemberbyId(postIdx);

            UnivPostLike univPostLike = UnivPostLike.likesUnivPost(univpost, member);
            Long id = postRepository.save(univPostLike);
            int likeCount = univpost.getUnivPostLikes().size();

            // 인기 게시물 알림 보내주기 조건:3일 동안 좋아요 10개

            if (univpost.getUnivPostLikes().size() == 10) {
                sendUnivPostNotification(univpost, postMember);
            }
            return new PostLikesUnivResponse(id, likeCount);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public void sendUnivPostNotification(UnivPost univpost, Member postMember) throws IOException {
        List<UnivPost> recentPost = postRepository.findAllWithMemberLikeCommentCount(postMember);
        String title = "학교 게시글";

        if (recentPost.contains(univpost) == true){
            String body = "인기 게시물로 지정되었어요";
//          fcmService.sendMessageTo(postMember.getFcmToken(), title, body);
        }
        else {
            return;
        }

    }


    /**
     * 3.17 통합 게시물 좋아요 취소 api
     */
    @Transactional
    public void unlikeTotal(Long likeIdx) throws BaseException {
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
    public void unlikeUniv(Long likeIdx) throws BaseException {
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
        if (totalpost.getStatus().equals(PostStatus.INACTIVE) || totalpost.getStatus().equals(PostStatus.REPORTED)) {
            throw new BaseException(REPORTED_DELETED_POST);
        }
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
        if (univpost.getStatus().equals(PostStatus.INACTIVE) || univpost.getStatus().equals(PostStatus.REPORTED)) {
            throw new BaseException(REPORTED_DELETED_POST);
        }
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


    /**
     * 통합 게시물 조회수
     */
    @Transactional
    public void updateView(Long postIdx) throws BaseException {
        TotalPost totalPost;
        totalPost = postRepository.findTotalPostById(postIdx);
        if (totalPost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
        try {
            totalPost.updateView();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 학교 게시물 조회수
     */
    @Transactional
    public void updateViewUniv(Long postIdx) throws BaseException {
        UnivPost univPost;
        univPost = postRepository.findUnivPostById(postIdx);
        if (univPost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
        try {
            univPost.updateView();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 전체 게시판 검색 기능
     */
    @Transactional
    public List<TotalPost> findAllSearch(String keyword)  throws BaseException {
        List<TotalPost> searchTotalPostLists = postRepository.searchTotalPostWithKeyword(keyword);
        if (searchTotalPostLists.size() == 0) {
            throw new BaseException(POST_NOT_EXIST);
        }
        return searchTotalPostLists;
    }

    /**
     * 학교 게시판 검색 기능
     */
    @Transactional
    public List<UnivPost> findUnivSearch(String keyword)  throws BaseException {
        List<UnivPost> searchUnivPostLists = postRepository.searchUnivPostWithKeyword(keyword);
        if (searchUnivPostLists.size() == 0) {
            throw new BaseException(POST_NOT_EXIST);
        }
        return searchUnivPostLists;
    }


}
