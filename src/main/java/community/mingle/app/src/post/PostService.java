package community.mingle.app.src.post;

import community.mingle.app.config.BaseException;
import community.mingle.app.src.domain.*;
import community.mingle.app.src.domain.Total.*;
import community.mingle.app.src.domain.Univ.*;
import community.mingle.app.src.firebase.FirebaseCloudMessageService;
import community.mingle.app.src.post.model.UpdatePostRequest;
import community.mingle.app.src.post.model.CreatePostRequest;
import community.mingle.app.src.post.model.CreatePostResponse;
import community.mingle.app.src.post.model.*;
import community.mingle.app.utils.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.utils.JwtService;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
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
     * 3.2 홍콩 배스트 게시판 API
     */
    public List<TotalPost> findTotalPostWithMemberLikeComment(Long postId) throws BaseException {
        List<TotalPost> totalPosts = postRepository.findTotalPostWithMemberLikeComment(postId);
        if (totalPosts.size() == 0) {
            throw new BaseException(EMPTY_BEST_POSTS);
        }
        return totalPosts;
    }


    /**
     * 3.3 학교 베스트 게시판 API
     */
    public List<UnivPost> findAllWithMemberLikeCommentCount(Long postId) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();  // jwtService 의 메소드 안에서 throw 해줌 -> controller 로 넘어감
        Member member;
        member = postRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(DATABASE_ERROR); //무조건 찾아야하는데 못찾을경우 (이미 jwt 에서 검증이 되기때문)
        }
        List<UnivPost> univPosts = postRepository.findAllWithMemberLikeCommentCount(member, postId);
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
    public CreatePostResponse createTotalPost(CreatePostRequest createPostRequest) throws BaseException {
        Member member;
        Category category;
        Long memberIdByJwt = jwtService.getUserIdx();
        member = postRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(USER_NOT_EXIST);
        }

        category = postRepository.findCategoryById(createPostRequest.getCategoryId());
        if (category == null) {
            throw new BaseException(INVALID_POST_CATEGORY);
        }

        try {
            TotalPost totalPost = TotalPost.createTotalPost(member, category, createPostRequest);
            Long id = postRepository.save(totalPost); // <- 이미지 파일 생성 실패시 글만 세이브되는 상황 방지는 못하지만 postId가 필요하기때문에 여기 있어야함.
            List<String> fileNameList = null;

            if (createPostRequest.getMultipartFile()==null || createPostRequest.getMultipartFile().isEmpty()) { //postman으로 할 시 둘다 충족이 안됨. 앱으로 할때만
                TotalPostImage totalPostImage = TotalPostImage.createTotalPost(totalPost, null); //그래서 isFileAttached = true로 받아들여서 사진생성하려함
                postRepository.save(totalPostImage); //그래서 에러남 ㅠ
            } else {
                try {
                    fileNameList = s3Service.uploadFile(createPostRequest.getMultipartFile(), "total");
                    for (String fileName : fileNameList) {
                        TotalPostImage totalPostImage = TotalPostImage.createTotalPost(totalPost, fileName);
                        postRepository.save(totalPostImage);
                    }
                } catch (Exception e) { // 이미지 파일 생성 실패시 글만 세이브되는 상황 방지 (postImgUrl being null) - 위에서 세이브 된 글을 아예 지움
                    postRepository.deleteTotalPost(id);
                    e.printStackTrace();
                    throw new BaseException(UPLOAD_FAIL_IMAGE);
                }
            }
            return new CreatePostResponse(id, fileNameList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(CREATE_FAIL_POST);
        }
    }

    /**
     * 3.7 학교 게시물 작성 API
     */
    @Transactional
    public CreatePostResponse createUnivPost(CreatePostRequest createPostRequest) throws BaseException {
        Member member;
        Category category;
        Long memberIdByJwt = jwtService.getUserIdx();
        member = postRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(USER_NOT_EXIST);
        }

        category = postRepository.findCategoryById(createPostRequest.getCategoryId());
        if (category == null) {
            throw new BaseException(INVALID_POST_CATEGORY);
        }

        try {
            UnivPost univPost = UnivPost.createUnivPost(member, category, createPostRequest);
            Long id = postRepository.save(univPost);
            List<String> fileNameList = null;

            if (createPostRequest.getMultipartFile()== null || createPostRequest.getMultipartFile().isEmpty()) {
                UnivPostImage univPostImage = UnivPostImage.createTotalPost(univPost, null);
                postRepository.save(univPostImage);
            } else {
                try {
                    fileNameList = s3Service.uploadFile(createPostRequest.getMultipartFile(), "univ");
                    for (String fileName : fileNameList) {
                        UnivPostImage univPostImage = UnivPostImage.createTotalPost(univPost, fileName);
                        postRepository.save(univPostImage);
                    }
                } catch (Exception e) {
                    postRepository.deleteUnivPost(id);
                    throw new BaseException(UPLOAD_FAIL_IMAGE);
                }
            }
            return new CreatePostResponse(id, fileNameList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(CREATE_FAIL_POST);
        }
    }



    /**
     * 3.8 카테고리
     * @return
     */
    public List<PostCategoryResponse> getPostCategory() throws BaseException {
        try {
            String authority = jwtService.getUserAuthority();
            List<Category> postCategory = postRepository.getPostCategory();
            List<PostCategoryResponse> result = postCategory.stream()
                    .map(m -> new PostCategoryResponse(m))
                    .collect(Collectors.toList());
            if (authority.equals("USER")) {
                result.remove(4); //학생회
                result.remove(3); //밍글소식
            }
            if (authority.equals("KSA")) {
                result.remove(3);
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
        TotalPost totalPost = postRepository.findTotalPostById(id);
        if (totalPost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
        if (totalPost.getStatus().equals(PostStatus.INACTIVE) || totalPost.getStatus().equals(PostStatus.REPORTED)) {
            throw new BaseException(REPORTED_DELETED_POST);
        }
        return totalPost;
    }

    @Transactional(readOnly = true)
    public TotalPostResponse getTotalPostDto(TotalPost totalPost) throws BaseException {
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
        TotalPostResponse totalPostResponse = new TotalPostResponse(totalPost, isMyPost, isLiked, isScraped);

        return totalPostResponse;
    }

    /**
     * 3.9.2 통합 게시물 상세 - 댓글 API
     */
    @Transactional(readOnly = true)
    public List<TotalCommentResponse> getTotalCommentList(Long id) throws BaseException {
        TotalPost totalPost = postRepository.checkTotalPostDisabled(id);
        if (totalPost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
        if (totalPost.getStatus().equals(PostStatus.REPORTED) || totalPost.getStatus().equals(PostStatus.INACTIVE)) {
            throw new BaseException(REPORTED_DELETED_POST);
        }
        Long memberIdByJwt = jwtService.getUserIdx();
        try {
            List<TotalComment> totalCommentList = postRepository.getTotalComments(id);
            List<TotalComment> totalCocommentList = postRepository.getTotalCocomments(id);
            List<TotalCommentResponse> totalCommentResponseList = new ArrayList<>();
            for (TotalComment tc : totalCommentList) {
                List<TotalComment> coComments = totalCocommentList.stream()
                        .filter(obj -> tc.getId().equals(obj.getParentCommentId()))
                        .collect(Collectors.toList());
                List<TotalCoCommentDTO> coCommentDtos = coComments.stream()
                        .map(p -> new TotalCoCommentDTO(p, postRepository.findTotalComment(p.getMentionId()), memberIdByJwt))
                        .collect(Collectors.toList());

//            boolean isLiked = postRepository.checkCommentIsLiked(tc.getId(), memberIdByJwt);
                TotalCommentResponse totalCommentResponse = new TotalCommentResponse(tc, coCommentDtos, memberIdByJwt);
                totalCommentResponseList.add(totalCommentResponse);
            }
            return totalCommentResponseList;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 3.10.1 학교 게시물 상세 - 게시물 API
     */
    @Transactional(readOnly = true)
    public UnivPostResponse getUnivPost(Long postId) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();  // jwtService 의 메소드 안에서 throw 해줌 -> controller 로 넘어감
        Member member;
        member = postRepository.findMemberbyId(memberIdByJwt);
        boolean isMyPost = false, isLiked = false, isScraped = false;
        UnivPost univPost = postRepository.findUnivPostById(postId);
        if (univPost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
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
            UnivPostResponse univPostResponse = new UnivPostResponse(univPost, isMyPost, isLiked, isScraped);
            return univPostResponse;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 3.10.2 학교 게시물 상세 - 댓글 API
     */
    @Transactional(readOnly = true)
    public List<UnivCommentResponse> getUnivComments(Long postId) throws BaseException {
        UnivPost univPost = postRepository.checkUnivPostDisabled(postId);
        if (univPost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
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
            List<UnivCommentResponse> univCommentResponseList = new ArrayList<>();
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
                UnivCommentResponse univCommentResponse = new UnivCommentResponse(c, coCommentDTO, memberIdByJwt);
                univCommentResponseList.add(univCommentResponse);
            }
            return univCommentResponseList;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 3.11 통합 게시물 수정 API
     */
    @Transactional
    public void updateTotalPost(Long id, UpdatePostRequest updatePostRequest) throws BaseException {
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
            totalPost.updateTotalPost(updatePostRequest);
        } catch (Exception e) {
            throw new BaseException(MODIFY_FAIL_POST);
        }
    }


    /**
     * 3.12 학교 게시물 수정 API
     */
    @Transactional
    public void updateUnivPost(Long id, UpdatePostRequest updatePostRequest) throws BaseException {
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
            univPost.updateUnivPost(updatePostRequest);
        } catch (Exception e) {
            throw new BaseException(MODIFY_FAIL_POST);
        }
    }


    /**
     * 3.13 통합 게시물 삭제 API
     * 확인 필요 10/23
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
    public LikeTotalPostResponse likesTotalPost(Long postIdx) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        TotalPost totalpost = postRepository.findTotalPostById(postIdx);
        if (totalpost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
        if (totalpost.getStatus().equals(PostStatus.INACTIVE) || totalpost.getStatus().equals(PostStatus.REPORTED)) {
            throw new BaseException(REPORTED_DELETED_POST);
        }

        Member member = postRepository.findMemberbyId(memberIdByJwt);
        Member postMember = totalpost.getMember(); //유저 삭제 시 게시물 삭제 넣을 시 추후에 삭제가능 이 아니라 알림위해 남겨놓기
//        Member postMember = postRepository.findMemberbyPostId(postIdx);
//        if (member == null) { //해야할까?
//            throw new BaseException(USER_NOT_EXIST);
//        }

//        //좋아요 중복 방지 - 1
//        List<TotalPostLike> totalPostLikeList = member.getTotalPostLikes();
//        for (TotalPostLike totalPostLike : totalPostLikeList) {
//            if (totalPostLike.getTotalPost().getId() == postIdx) {
//                throw new BaseException(DUPLICATE_LIKE);
//            }
//        }
//        //좋아요 중복 방지 - 2
//        boolean checkTotalIsLiked = postRepository.checkTotalIsLiked(postIdx, member.getId());
//        if (checkTotalIsLiked) {
//            throw new BaseException(DUPLICATE_LIKE);
//        }

        TotalPostLike totalPostLike = TotalPostLike.likesTotalPost(totalpost, member);
        if (totalPostLike == null) {
            throw new BaseException(DUPLICATE_LIKE);
        }
        else {
            try {
                //좋아요 생성 - 위에서
                Long id = postRepository.save(totalPostLike);
                int likeCount = totalpost.getTotalPostLikes().size();
                // 인기 게시물 알림 보내주기
                if (totalpost.getTotalPostLikes().size() == 10) {
                    sendTotalPostNotification(totalpost, postMember);
                }
                return new LikeTotalPostResponse(id, likeCount);

            } catch (Exception e) {
                e.printStackTrace();
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

    public void sendTotalPostNotification(TotalPost totalpost, Member postMember) throws IOException {
//        List<TotalPost> recentPost = postRepository.findTotalPostWithMemberLikeComment();
//        String title = "전체 게시글";
//
//        if (recentPost.contains(totalpost) == true){
//            String body = "인기 게시물로 지정되었어요";
////          fcmService.sendMessageTo(postMember.getFcmToken(), title, body);
//        }
//        else {
//                return;
//        }

    }



    /**
     * 3.16 학교 게시물 좋아요 api
     */
    @Transactional
    public LikeUnivPostResponse likesUnivPost(Long postIdx) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        UnivPost univpost = postRepository.findUnivPostById(postIdx);
        if (univpost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
        if (univpost.getStatus().equals(PostStatus.INACTIVE) || univpost.getStatus().equals(PostStatus.REPORTED)) {
            throw new BaseException(REPORTED_DELETED_POST);
        }
        Member member = postRepository.findMemberbyId(memberIdByJwt);
//        Member postMember = postRepository.findMemberbyPostId(postIdx); ??????왜한거지
        Member postMember = univpost.getMember();
        if (member == null || postMember == null) { //해야할까?
            throw new BaseException(USER_NOT_EXIST);
        }

        UnivPostLike univPostLike = UnivPostLike.likesUnivPost(univpost, member);
        if (univPostLike == null) {
            throw new BaseException(DUPLICATE_LIKE);
        }
        else {
            try {
//            UnivPostLike univPostLike = UnivPostLike.likesUnivPost(univpost, member);
                Long id = postRepository.save(univPostLike);
                int likeCount = univpost.getUnivPostLikes().size();

                // 인기 게시물 알림 보내주기 조건:3일 동안 좋아요 10개

                if (univpost.getUnivPostLikes().size() == 10) {
                    sendUnivPostNotification(univpost, postMember);
                }
                return new LikeUnivPostResponse(id, likeCount);
            } catch (Exception e) {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }


    /**
     * 알림 보내기
     * //10/23 베스트 로직 바뀌면서 수정 필요
     */
    public void sendUnivPostNotification(UnivPost univpost, Member postMember) throws IOException {
        //List<UnivPost> recentPost = postRepository.findAllWithMemberLikeCommentCount(postMember);
        String title = "학교 게시글";

//        if (recentPost.contains(univpost) == true){
//            String body = "인기 게시물로 지정되었어요";
////          fcmService.sendMessageTo(postMember.getFcmToken(), title, body);
//        }
//        else {
//            return;
//        }

    }


    /**
     * 3.17 통합 게시물 좋아요 취소 api
     */
    @Transactional
    public void unlikeTotal(Long postId) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        try {
            //totalPostLike.deleteLike(likeIdx);
            postRepository.deleteTotalLike(postId, memberIdByJwt);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DELETED_LIKE);
        }
    }


    /**
     * 3.18 학교 게시물 좋아요 취소 api
     */
    @Transactional
    public void unlikeUniv(Long postId) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        try {
            postRepository.deleteUnivLike(postId, memberIdByJwt);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DELETED_LIKE);
        }
    }


    /**
     * 3.19 통합 게시물 스크랩 api
     */
    @Transactional
    public ScrapTotalPostResponse scrapTotalPost(Long postIdx) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        TotalPost totalpost = postRepository.findTotalPostById(postIdx);
        if (totalpost.getStatus().equals(PostStatus.INACTIVE) || totalpost.getStatus().equals(PostStatus.REPORTED)) {
            throw new BaseException(REPORTED_DELETED_POST);
        }
        if (totalpost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }

        Member member = postRepository.findMemberbyId(memberIdByJwt);
        Member postMember = totalpost.getMember();//유저삭제 api 만들시 없애기
        if (member == null || postMember == null) { //해야할까?
            throw new BaseException(USER_NOT_EXIST);
        }

        TotalPostScrap totalPostScrap = TotalPostScrap.scrapTotalPost(totalpost, member);
        if (totalPostScrap == null) {
            throw new BaseException(DUPLICATE_SCRAP);
        }
        else {
            try {
//            Member member = postRepository.findMemberbyId(memberIdByJwt);
//                TotalPostScrap totalPostScrap = TotalPostScrap.scrapTotalPost(totalpost, member);
                Long id = postRepository.save(totalPostScrap);
                int scrapCount = totalpost.getTotalPostScraps().size();
                return new ScrapTotalPostResponse(id, scrapCount);
            } catch (Exception e) {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }


    /**
     * 3.20 학교 게시물 스크랩 api
     */
    @Transactional
    public ScrapUnivPostResponse scrapUnivPost(Long postIdx) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        UnivPost univpost = postRepository.findUnivPostById(postIdx);
        if (univpost.getStatus().equals(PostStatus.INACTIVE) || univpost.getStatus().equals(PostStatus.REPORTED)) {
            throw new BaseException(REPORTED_DELETED_POST);
        }
        if (univpost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }

        Member member = postRepository.findMemberbyId(memberIdByJwt);
        Member postMember = univpost.getMember();
        if (member == null || postMember == null) { //해야할까?
            throw new BaseException(USER_NOT_EXIST);
        }

        UnivPostScrap univPostScrap = UnivPostScrap.scrapUnivPost(univpost, member);
        if (univPostScrap == null) {
            throw new BaseException(DUPLICATE_SCRAP);
        } else {
            try {
//            Member member = postRepository.findMemberbyId(memberIdByJwt);
//                UnivPostScrap univPostScrap = UnivPostScrap.scrapUnivPost(univpost, member);
                Long id = postRepository.save(univPostScrap);
                int scrapCount = univpost.getUnivPostScraps().size();
                return new ScrapUnivPostResponse(id, scrapCount);
            } catch (Exception e) {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }


    /**
     * 3.21 통합 게시물 스크랩 취소 api
     */
    @Transactional
    public void deleteScrapTotal(Long postId) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        try {
            postRepository.deleteTotalScrap(postId, memberIdByJwt);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DELETED_SCRAP);
        }
    }


    /**
     * 3.22 학교 게시물 스크랩 취소 api
     */
    @Transactional
    public void deleteScrapUniv(Long postId) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        try {
            postRepository.deleteUnivScrap(postId, memberIdByJwt);
        } catch (Exception e) {
            throw new BaseException(DELETED_SCRAP);
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
