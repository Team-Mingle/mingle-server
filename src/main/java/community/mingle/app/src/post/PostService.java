package community.mingle.app.src.post;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.domain.*;
import community.mingle.app.src.domain.Total.*;
import community.mingle.app.src.domain.Univ.*;
import community.mingle.app.src.firebase.FirebaseCloudMessageService;
import community.mingle.app.src.member.MemberRepository;
import community.mingle.app.src.post.model.*;
import community.mingle.app.utils.JwtService;
import community.mingle.app.utils.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static community.mingle.app.config.BaseResponseStatus.*;
import static community.mingle.app.src.domain.PostStatus.DELETED;
import static community.mingle.app.src.domain.PostStatus.REPORTED;
import static java.lang.Long.parseLong;

@Service
@RequiredArgsConstructor
public class PostService {

    private final JwtService jwtService;
    private final PostRepository postRepository;

    private final S3Service s3Service;

    private final FirebaseCloudMessageService fcmService;
    private final MemberRepository memberRepository;


    /**
     * 3.1 학교 전체 리스트 API
     */
    public List<UnivPost> findPosts(int category, Long postId, Long memberId) throws BaseException {
        List<UnivPost> getUnivAll = postRepository.findPosts(category, postId, memberId);
        if (getUnivAll.size() == 0) {
            throw new BaseException(EMPTY_POSTS_LIST);
        }
        return getUnivAll;
    }


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
     * 신고된 게시물 처리
     */
    public String findReportedPostReason(Long postId, TableType tableType) {
        List<Report> reportedPostReason = postRepository.findReportedPostReason(postId, tableType);
        int mode = 0;
        int maxCount = 0;

        ArrayList<Integer> reportedTypeList = new ArrayList<>();
        if (reportedPostReason == null) {
            return null;
        } else {
            reportedPostReason.forEach(report -> reportedTypeList.add(report.getType()));
//            for (Report r : reportedPostReason) {
//                    reportedTypeList.add(report.getType());
//            }
            for (int type : reportedTypeList) {
                int count = Collections.frequency(reportedTypeList, type);
                if (count > maxCount) {
                    mode = type;
                    maxCount = count;
                }
            }
            List<ReportType> reportedTypeReason = postRepository.findReportedTypeReason(mode); //null 체크 추가
            if (reportedTypeReason == null) {

            }
            String reason = (reportedTypeReason == null) ? reportedTypeReason.get(0).getType() : "욕설/인신공격/혐오/비하"; //null check
            return reason;
//            return reportedTypeReason.get(0).getType();
        }
    }


    /**
     * 3.2 홍콩 배스트 게시판 API
     */
    public List<TotalPost> findTotalPostWithMemberLikeComment(Long postId, Long memberId) throws BaseException {
        Member member = memberRepository.findMember(memberId);
        List<TotalPost> totalPosts = postRepository.findTotalPostWithMemberLikeComment(postId, member);
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
    public PostListResponse findTotalPost(int category, Long postId, Long memberId) throws BaseException {
        Member member = memberRepository.findMember(memberId);
        boolean isAdmin = member.getRole().equals(UserRole.ADMIN);

        List<TotalPost> postList = isAdmin
                ? postRepository.findAdminTotalPosts(category, postId, member)
                : postRepository.findTotalPost(category, postId, member);
        if (postList.isEmpty()) throw new BaseException(EMPTY_POSTS_LIST);

        List<PostListDTO> result = postList.stream()
                .map(p -> isAdmin
                        ? new PostListDTO(p.getMember().getUniv().getCountry(), p, memberId)
                        : new PostListDTO(p, memberId))
                .collect(Collectors.toList());

        return new PostListResponse(result);
    }


    /**
     * 3.5 학교 게시판 리스트 API
     */
    public List<UnivPost> findUnivPost(int category, Long postId, int univId, Long memberId) throws BaseException {
        List<UnivPost> getUnivAll = postRepository.findUnivPost(category, postId, univId, memberId);
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

            if (createPostRequest.getMultipartFile() == null || createPostRequest.getMultipartFile().isEmpty()) { //postman으로 할 시 둘다 충족이 안됨. 앱으로 할때만
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

            if (createPostRequest.getMultipartFile() == null || createPostRequest.getMultipartFile().isEmpty()) {
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
     *
     * @return
     */
    public List<PostCategoryResponse> getPostCategory() throws BaseException {
        try {
            UserRole authority = UserRole.valueOf(jwtService.getUserAuthority());
            List<Category> postCategory = postRepository.getPostCategory();
            List<PostCategoryResponse> result = postCategory.stream()
                    .map(PostCategoryResponse::new)
                    .collect(Collectors.toList());
            if (authority.equals(UserRole.USER) || authority.equals(UserRole.FRESHMAN)) {
                result.remove(4); //학생회
                result.remove(3); //밍글소식
                result.remove(2); //진로
            }
            if (authority.equals(UserRole.KSA)) {
                result.remove(3); //밍글소식
                result.remove(2); //진로
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
//        if (totalPost.getStatus().equals(PostStatus.INACTIVE) || totalPost.getStatus().equals(PostStatus.REPORTED)) {
//            throw new BaseException(REPORTED_DELETED_POST);
//        }
        return totalPost;
    }

    @Transactional(readOnly = true)
    public PostResponse getTotalPost(TotalPost totalPost) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        boolean isMyPost = false;
        boolean isLiked = false;
        boolean isScraped = false;
        boolean isBlinded = false;
        try {
            if (Objects.equals(totalPost.getMember().getId(), memberIdByJwt)) {
                isMyPost = true;
            }
            if (postRepository.checkTotalIsLiked(totalPost.getId(), memberIdByJwt)) {
                isLiked = true;
            }
            if (postRepository.checkTotalIsScraped(totalPost.getId(), memberIdByJwt)) {
                isScraped = true;
            }
            if (postRepository.checkTotalPostIsBlinded(totalPost.getId(), memberIdByJwt)) {
                isBlinded = true;
            }
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
        /*** 게시물 신고 추가 */
        PostResponse totalPostResponse;
        if (totalPost.getStatus().equals(REPORTED)) { //reported 일때만 reason 찾기
            String reportedReason = findReportedPostReason(totalPost.getId(), TableType.TotalPost);
            totalPostResponse = new PostResponse(totalPost, isMyPost, isLiked, isScraped, isBlinded, reportedReason);
        } else if (totalPost.getStatus().equals(DELETED)) {
            totalPostResponse = new PostResponse(totalPost, isMyPost, isLiked, isScraped, isBlinded, "");
        } else {
            totalPostResponse = new PostResponse(totalPost, isMyPost, isLiked, isScraped, isBlinded);
        }
        return totalPostResponse;
    }

    /**
     * 3.9.2 통합 게시물 상세 - 댓글 API
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> getTotalCommentList(Long id) throws BaseException {
        TotalPost totalPost = postRepository.checkTotalPostDisabled(id);
        if (totalPost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
        if (totalPost.getStatus().equals(REPORTED) || totalPost.getStatus().equals(DELETED)) {
            return new ArrayList<>();
        }
//        if (totalPost.getStatus().equals(PostStatus.REPORTED) || totalPost.getStatus().equals(PostStatus.INACTIVE)) {
//            throw new BaseException(REPORTED_DELETED_POST);
//        }
        Long memberIdByJwt = jwtService.getUserIdx();
        try {
            List<TotalComment> totalCommentList = postRepository.getTotalComments(id, memberIdByJwt);
            List<TotalComment> totalCocommentList = postRepository.getTotalCocomments(id, memberIdByJwt);
            List<CommentResponse> totalCommentResponseList = new ArrayList<>();
            for (TotalComment tc : totalCommentList) {
                List<TotalComment> coComments = totalCocommentList.stream()
                        .filter(obj -> tc.getId().equals(obj.getParentCommentId()))
//                        .filter(cc -> cc.getStatus().equals(PostStatus.ACTIVE))
                        .collect(Collectors.toList());

                //11/25 추가: 삭제된 댓글 표시 안하기 - 대댓글 없는 댓글 그냥 삭제 // 2/20 추가: 유저가 직접 삭제한 댓글만 표시하지 않기
                if ((tc.getStatus() == PostStatus.INACTIVE) && coComments.size() == 0) {
                    continue;
                }
                List<CoCommentDTO> coCommentDtos = coComments.stream()
                        .filter(cc -> cc.getStatus().equals(PostStatus.ACTIVE) || cc.getStatus().equals(REPORTED) || cc.getStatus().equals(DELETED)) //11/25: 대댓글 삭제시 그냥 삭제. 2/20: 신고된 댓글 표시
                        .map(p -> new CoCommentDTO(p, postRepository.findTotalComment(p.getMentionId()), memberIdByJwt, totalPost.getMember().getId()))
                        .collect(Collectors.toList());

//            boolean isLiked = postRepository.checkCommentIsLiked(tc.getId(), memberIdByJwt);
                CommentResponse totalCommentResponse = new CommentResponse(tc, coCommentDtos, memberIdByJwt, totalPost.getMember().getId());
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
    public PostResponse getUnivPost(Long postId) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();  // jwtService 의 메소드 안에서 throw 해줌 -> controller 로 넘어감
        boolean isMyPost = false, isLiked = false, isScraped = false, isBlinded = false;
        UnivPost univPost = postRepository.findUnivPostById(postId);
        if (univPost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
//        if (univPost.getStatus().equals(PostStatus.INACTIVE) || univPost.getStatus().equals(PostStatus.REPORTED)) {
//            throw new BaseException(REPORTED_DELETED_POST);
//        }
        try {
            if (Objects.equals(univPost.getMember().getId(), memberIdByJwt)) {
                isMyPost = true;
            }
            if (postRepository.checkUnivPostIsLiked(postId, memberIdByJwt)) {
                isLiked = true;
            }
            if (postRepository.checkUnivPostIsScraped(postId, memberIdByJwt)) {
                isScraped = true;
            }
            if (postRepository.checkUnivPostIsBlinded(postId, memberIdByJwt)) {
                isBlinded = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
        /*** 게시물 신고 추가 */
        PostResponse univPostResponse;
        if (univPost.getStatus().equals(REPORTED)) {
            String reportedReason = findReportedPostReason(univPost.getId(), TableType.UnivPost);
            univPostResponse = new PostResponse(univPost, isMyPost, isLiked, isScraped, isBlinded, reportedReason);
        } else if (univPost.getStatus().equals(DELETED)) {
            univPostResponse = new PostResponse(univPost, isMyPost, isLiked, isScraped, isBlinded, "");
        } else {
            univPostResponse = new PostResponse(univPost, isMyPost, isLiked, isScraped, isBlinded);
        }
        return univPostResponse;
    }


    /**
     * 3.10.2 학교 게시물 상세 - 댓글 API
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> getUnivComments(Long postId) throws BaseException {
        UnivPost univPost = postRepository.checkUnivPostDisabled(postId);
        if (univPost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
        if (univPost.getStatus().equals(REPORTED) || univPost.getStatus().equals(DELETED)) {
            return new ArrayList<>();
        }
//        if (univPost.getStatus().equals(PostStatus.REPORTED) || univPost.getStatus().equals(PostStatus.INACTIVE)) {
//            throw new BaseException(REPORTED_DELETED_POST);
//        }
        Long memberIdByJwt = jwtService.getUserIdx();  // jwtService 의 메소드 안에서 throw 해줌 -> controller 로 넘어감
        Member member;
        member = postRepository.findMemberbyId(memberIdByJwt);
        try {
            //1. postId 의 댓글, 대댓글 리스트 각각 가져오기
            List<UnivComment> univComments = postRepository.getUnivComments(postId, memberIdByJwt); //댓글
            List<UnivComment> univCoComments = postRepository.getUnivCoComments(postId, memberIdByJwt); //대댓글
            //2. 댓글 + 대댓글 DTO 생성
            List<CommentResponse> univCommentResponseList = new ArrayList<>();
            //3. 댓글 리스트 돌면서 댓글 하나당 대댓글 리스트 넣어서 합쳐주기
            for (UnivComment c : univComments) {
                //parentComment 하나당 해당하는 UnivComment 타입의 대댓글 찾아서 리스트 만들기
                List<UnivComment> CoCommentList = univCoComments.stream()
                        .filter(cc -> c.getId().equals(cc.getParentCommentId()))
//                        .filter(cc -> cc.getStatus().equals(PostStatus.ACTIVE)) // 더 추가: 대댓글 active 인거만 가져오기
                        .collect(Collectors.toList());

                //11/25 추가: 삭제된 댓글 표시 안하기 - 대댓글 없는 댓글 그냥 삭제
                if ((c.getStatus() == PostStatus.INACTIVE) && CoCommentList.size() == 0) {
                    continue;
                }

                //댓글 하나당 만들어진 대댓글 리스트를 대댓글 DTO 형태로 변환
                List<CoCommentDTO> coCommentDTO = CoCommentList.stream()
                        .filter(cc -> cc.getStatus().equals(PostStatus.ACTIVE) || cc.getStatus().equals(REPORTED) || cc.getStatus().equals(DELETED)) //11/25: 대댓글 삭제시 그냥 삭제. //2/20: 신고된 댓글 표시
                        .map(cc -> new CoCommentDTO(postRepository.findUnivComment(cc.getMentionId()), cc, memberIdByJwt, univPost.getMember().getId()))
                        .collect(Collectors.toList());
                /** 쿼리문 나감. 결론: for 문 안에서 쿼리문 대신 DTO 안에서 해결 */
                //boolean isLiked = postRepository.checkCommentIsLiked(c.getId(), memberIdByJwt);
                //4. 댓글 DTO 생성 후 최종 DTOList 에 넣어주기
                CommentResponse univCommentResponse = new CommentResponse(c, coCommentDTO, memberIdByJwt, univPost.getMember().getId());
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

        if (!Objects.equals(memberIdByJwt, totalPost.getMember().getId())) { // 2/17 핫픽스
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
        if (!Objects.equals(memberIdByJwt, univPost.getMember().getId())) {
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
        if (!Objects.equals(memberIdByJwt, totalPost.getMember().getId())) {
            throw new BaseException(MODIFY_NOT_AUTHORIZED);
        }
        try {
            List<TotalComment> totalComments = postRepository.findAllTotalComment(id);
            if (totalComments != null) {
                for (TotalComment c : totalComments) {
                    c.deleteTotalComment();
                }
            }
            List<TotalPostImage> totalPostImages = postRepository.findAllTotalImage(id);
            if (totalPostImages != null && totalPost.getIsFileAttached()) {
                for (TotalPostImage pi : totalPostImages) {
                    pi.deleteTotalImage();

                    String imgUrl = pi.getImgUrl();
                    String fileName = imgUrl.substring(imgUrl.lastIndexOf("/total/") + 7);
                    s3Service.deleteFile(fileName, "total");
                }
            }
            totalPost.deleteTotalPost();

        } catch (BaseException b) {
            b.printStackTrace();
            throw new BaseException(DELETE_FAIL_IMAGE);
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

        if (!Objects.equals(memberIdByJwt, univPost.getMember().getId())) {
            throw new BaseException(MODIFY_NOT_AUTHORIZED);
        }

        try {
            List<UnivComment> univComments = postRepository.findAllUnivComment(id);
            if (univComments != null) {
                for (UnivComment c : univComments) {
                    c.deleteUnivComment(); //inactive
                }
            }

            List<UnivPostImage> univPostImages = postRepository.findAllUnivImage(id); //사진 삭제
            if (univPostImages != null && univPost.getIsFileAttached()) {
                for (UnivPostImage pi : univPostImages) {
                    pi.deleteUnivImage();

                    String imgUrl = pi.getImgUrl();
                    String fileName = imgUrl.substring(imgUrl.lastIndexOf("/univ/") + 6);
                    s3Service.deleteFile(fileName, "univ");
                }
            }
            univPost.deleteUnivPost();

        } catch (BaseException b) {
//            b.printStackTrace();
            throw new BaseException(DELETE_FAIL_IMAGE);
        } catch (Exception e) {
//            e.printStackTrace();
            throw new BaseException(DELETE_FAIL_POST);
        }
    }


    /**
     * 3.15 통합 게시물 좋아요 api + 인기 게시물 알림 기능
     */
    @Transactional
    public LikePostResponse likesTotalPost(Long postIdx) throws BaseException {
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
        TotalPostLike totalPostLike = TotalPostLike.likesTotalPost(totalpost, member);
        if (totalPostLike == null) {
            throw new BaseException(DUPLICATE_LIKE);
        } else {
            try {
                //좋아요 생성 - 위에서
                Long id = postRepository.save(totalPostLike);
                int likeCount = totalpost.getTotalPostLikes().size();
                // 인기 게시물 알림 보내주기
                if (totalpost.getTotalPostLikes().size() == 10) {
                    sendTotalPostNotification(totalpost, postMember);
                    //알림 저장
                    //문제 --> comment 가져오지 않는다
                    TotalNotification totalNotification = TotalNotification.saveTotalPostNotification(totalpost, postMember);
                    memberRepository.saveTotalNotification(totalNotification);
                    if (postMember.getTotalNotifications().size() > 20) {
                        postMember.getTotalNotifications().remove(0);
                    }
                }
                return new LikePostResponse(id, likeCount);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

    public void sendTotalPostNotification(TotalPost totalpost, Member postMember) throws IOException, BaseException {
        List<TotalPost> recentPost = postRepository.findAllBestTotalPost(totalpost.getId());
        if (recentPost == null) {
            throw new BaseException(DATABASE_ERROR);
        } else if (recentPost.contains(totalpost)) {
            String title = "전체 게시글";
            String body = "인기 게시물로 지정되었어요";
            fcmService.sendMessageTo(postMember.getFcmToken(), title, body, TableType.TotalPost, CategoryType.valueOf(totalpost.getCategory().getName()), totalpost.getId());
        }
    }


    /**
     * 3.16 학교 게시물 좋아요 api
     */
    @Transactional
    public LikePostResponse likesUnivPost(Long postIdx) throws BaseException {
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
        //좋아요 중복 방지
        UnivPostLike univPostLike = UnivPostLike.likesUnivPost(univpost, member);
        if (univPostLike == null) {
            throw new BaseException(DUPLICATE_LIKE);
        } else {
            try {
//            UnivPostLike univPostLike = UnivPostLike.likesUnivPost(univpost, member);
                Long id = postRepository.save(univPostLike);
                //이게 persist가 되자마자 바로 univpost에서 좋아요 갯수가 동기화 되는지 확인 필요
                int likeCount = univpost.getUnivPostLikes().size();
                // 인기 게시물 알림 보내주기 조건:좋아요 5개
                if (univpost.getUnivPostLikes().size() == 5) {
                    sendUnivPostNotification(univpost, postMember);
                    //알림 저장
                    UnivNotification univNotification = UnivNotification.saveUnivTotalNotification(univpost, postMember);
                    memberRepository.saveUnivNotification(univNotification);
                    if (postMember.getUnivNotifications().size() > 20) {
                        postMember.getUnivNotifications().remove(0);
                    }
                }
                return new LikePostResponse(id, likeCount);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }


    /**
     * 알림 보내기
     * //10/23 베스트 로직 바뀌면서 수정 필요
     */
    public void sendUnivPostNotification(UnivPost univpost, Member postMember) throws IOException, BaseException {
        List<UnivPost> recentPost = postRepository.findAllUnivBestPost(postMember, univpost.getId());
        if (recentPost == null) {
            throw new BaseException(DATABASE_ERROR);
        } else if (recentPost.contains(univpost)) {
            String title = "학교 게시글";
            String body = "인기 게시물로 지정되었어요";
            fcmService.sendMessageTo(postMember.getFcmToken(), title, body, TableType.UnivPost, CategoryType.valueOf(univpost.getCategory().getName()), univpost.getId());
        }
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
    public ScrapPostResponse scrapTotalPost(Long postIdx) throws BaseException {
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
        } else {
            try {
//            Member member = postRepository.findMemberbyId(memberIdByJwt);
//                TotalPostScrap totalPostScrap = TotalPostScrap.scrapTotalPost(totalpost, member);
                Long id = postRepository.save(totalPostScrap);
                int scrapCount = totalpost.getTotalPostScraps().size();
                return new ScrapPostResponse(id, scrapCount);
            } catch (Exception e) {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }


    /**
     * 3.20 학교 게시물 스크랩 api
     */
    @Transactional
    public ScrapPostResponse scrapUnivPost(Long postIdx) throws BaseException {
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
                return new ScrapPostResponse(id, scrapCount);
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
    public List<TotalPost> findAllSearch(String keyword, Long memberId) throws BaseException {
        Member member = memberRepository.findMember(memberId);
        List<TotalPost> searchTotalPostLists = postRepository.searchTotalPostWithKeyword(keyword, member);
        if (searchTotalPostLists.size() == 0) {
            throw new BaseException(POST_NOT_EXIST);
        }
        return searchTotalPostLists;
    }

    /**
     * 학교 게시판 검색 기능
     */
    @Transactional
    public List<UnivPost> findUnivSearch(int univId, String keyword, Long memberId) throws BaseException {
        List<UnivPost> searchUnivPostLists = postRepository.searchUnivPostWithKeyword(univId, keyword, memberId);

        if (searchUnivPostLists.size() == 0) {
            throw new BaseException(POST_NOT_EXIST);
        }
        return searchUnivPostLists;
    }


    /**
     * 2.25 전체 게시물 가리기
     */
    @Transactional
    public String blindTotalPost(Long postId) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        TotalPost totalpost = postRepository.findTotalPostById(postId);
        if (totalpost.getStatus().equals(PostStatus.INACTIVE) || totalpost.getStatus().equals(PostStatus.REPORTED)) {
            throw new BaseException(REPORTED_DELETED_POST);
        }
        if (totalpost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
        Member member = postRepository.findMemberbyId(memberIdByJwt);
        TotalBlind totalBlind = TotalBlind.blindTotalPost(totalpost, member);
        if (totalBlind == null) {
            throw new BaseException(DUPLICATE_BLIND);
        } else {
            try {
                Long id = postRepository.saveBlind(totalBlind);
                return "게시물을 가렸어요.";
            } catch (Exception e) {
                e.printStackTrace();
                throw new BaseException(DATABASE_ERROR);
            }
        }

    }

    @Transactional
    public String blindUnivPost(Long postId) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        UnivPost univpost = postRepository.findUnivPostById(postId);
        if (univpost.getStatus().equals(PostStatus.INACTIVE) || univpost.getStatus().equals(PostStatus.REPORTED)) {
            throw new BaseException(REPORTED_DELETED_POST);
        }
        if (univpost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
        Member member = postRepository.findMemberbyId(memberIdByJwt);
        UnivBlind univBlind = UnivBlind.blindUnivPost(univpost, member);
        if (univBlind == null) {
            throw new BaseException(DUPLICATE_BLIND);
        } else {
            try {
                Long id = postRepository.saveBlind(univBlind);
                return "게시물을 가렸어요.";
            } catch (Exception e) {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

    @Transactional
    public String blockMemberInTotalPost(Long postId) throws BaseException {
        TotalPost totalPostById = postRepository.findTotalPostById(postId);
        Long userIdx = jwtService.getUserIdx();
        Member member = postRepository.findMemberbyId(userIdx);
        try {
            BlockMember blockMember = BlockMember.CreateBlockMember(totalPostById.getMember(), member);
            postRepository.save(blockMember);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
        return "유저를 성공적으로 차단했습니다.";
    }

    @Transactional
    public String blockMemberInUnivPost(Long postId) throws BaseException {
        UnivPost univPostById = postRepository.findUnivPostById(postId);
        Long userIdx = jwtService.getUserIdx();
        Member member = postRepository.findMemberbyId(userIdx);
        try {
            BlockMember blockMember = BlockMember.CreateBlockMember(univPostById.getMember(), member);
            postRepository.save(blockMember);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
        return "유저를 성공적으로 차단했습니다.";
    }

    @Transactional
    public String unblindTotalPost(Long postId) throws BaseException {
        Long memberId = jwtService.getUserIdx();
        try {
            postRepository.deleteTotalBlind(memberId, postId);
            return "가리기를 취소했습니다.";
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public String unblindUnivPost(Long postId) throws BaseException {
        Long memberId = jwtService.getUserIdx();
        try {
            postRepository.deleteUnivBlind(memberId, postId);
            return "가리기를 취소했습니다.";
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<NotifiedContentResponse> listNotifiedTotalPost() {
        List<TotalPost> reportedTotalPostList = postRepository.getReportedTotalPostList();
        return reportedTotalPostList.stream()
                .map(totalPost -> new NotifiedContentResponse(totalPost.getMember().getId().toString(), totalPost.getMember().getNickname(), totalPost.getId().toString(), totalPost.getTitle()))
                .collect(Collectors.toList());
    }

    public List<NotifiedContentResponse> listNotifiedUnivPost() {
        List<UnivPost> reportedUnivPostList = postRepository.getReportedUnivPostList();
        return reportedUnivPostList.stream()
                .map(univPost -> new NotifiedContentResponse(univPost.getMember().getId().toString(), univPost.getMember().getNickname(), univPost.getId().toString(), univPost.getTitle()))
                .collect(Collectors.toList());
    }

    public List<NotifiedContentResponse> listNotifiedTotalComment() {
        List<TotalComment> reportedTotalCommentList = postRepository.getReportedTotalCommentList();
        return reportedTotalCommentList.stream()
                .map(totalComment -> new NotifiedContentResponse(totalComment.getMember().getId().toString(), totalComment.getMember().getNickname(), totalComment.getId().toString(), totalComment.getContent()))
                .collect(Collectors.toList());
    }

    public List<NotifiedContentResponse> listNotifiedUnivComment() {
        List<UnivComment> reportedUnivCommentList = postRepository.getReportedUnivCommentList();
        return reportedUnivCommentList.stream()
                .map(univComment -> new NotifiedContentResponse(univComment.getMember().getId().toString(), univComment.getMember().getNickname(), univComment.getId().toString(), univComment.getContent()))
                .collect(Collectors.toList());
    }

    //    public List<NotifiedMemberResponse> listNotifiedMember() {
//        List<Member> reportedMemberList = postRepository.getReportedMemberList();
//
//    }
    @Transactional
    public void executeTotalPost(String contentId) throws IOException {
        TotalPost totalPost = postRepository.findTotalPostById(parseLong(contentId));
        totalPost.modifyStatusAsReported();
        ReportNotification reportNotification = ReportNotification.saveReportNotification(totalPost.getMember().getId(), REPORTED, totalPost.getId(), BoardType.광장, NotificationType.게시물, CategoryType.valueOf(totalPost.getCategory().getName()));
        postRepository.saveReportNotification(reportNotification);
        String title = "광장 게시글 차단";
        String body = "다른 사용자들의 신고에 의해 글이 삭제되었습니다.";
        fcmService.sendMessageTo(totalPost.getMember().getFcmToken(), title, body, TableType.TotalPost, CategoryType.valueOf(totalPost.getCategory().getName()), totalPost.getId());
    }

    @Transactional
    public void executeUnivPost(String contentId) throws IOException {
        UnivPost univPost = postRepository.findUnivPostById(parseLong(contentId));
        univPost.modifyStatusAsReported();
        ReportNotification reportNotification = ReportNotification.saveReportNotification(univPost.getMember().getId(), REPORTED, univPost.getId(), BoardType.잔디밭, NotificationType.게시물, CategoryType.valueOf(univPost.getCategory().getName()));
        postRepository.saveReportNotification(reportNotification);
        String title = "잔디밭 게시글 차단";
        String body = "다른 사용자들의 신고에 의해 글이 삭제되었습니다.";
        fcmService.sendMessageTo(univPost.getMember().getFcmToken(), title, body, TableType.UnivPost, univPost.getId());
    }

    @Transactional
    public void executeTotalComment(String contentId) throws IOException {
        TotalComment totalComment = postRepository.findTotalCommentById(parseLong(contentId));
        totalComment.modifyStatusAsReported();
        ReportNotification reportNotification = ReportNotification.saveReportNotification(totalComment.getMember().getId(), REPORTED, totalComment.getId(), BoardType.광장, NotificationType.댓글, CategoryType.valueOf(totalComment.getTotalPost().getCategory().getName()));
        postRepository.saveReportNotification(reportNotification);
        String title = "광장 댓글 차단";
        String body = "다른 사용자들의 신고에 의해 글이 삭제되었습니다.";
        fcmService.sendMessageTo(totalComment.getMember().getFcmToken(), title, body, TableType.TotalComment, totalComment.getId());
    }

    @Transactional
    public void executeUnivComment(String contentId) throws IOException {
        UnivComment univComment = postRepository.findUnivCommentById(parseLong(contentId));
        univComment.modifyStatusAsReported();
        ReportNotification reportNotification = ReportNotification.saveReportNotification(univComment.getMember().getId(), REPORTED, univComment.getId(), BoardType.잔디밭, NotificationType.댓글, CategoryType.valueOf(univComment.getUnivPost().getCategory().getName()));
        postRepository.saveReportNotification(reportNotification);
        String title = "잔디밭 댓글 차단";
        String body = "다른 사용자들의 신고에 의해 글이 삭제되었습니다.";
        fcmService.sendMessageTo(univComment.getMember().getFcmToken(), title, body, TableType.UnivComment, univComment.getId());
    }

    @Transactional
    public void executeMember(Long memberId) throws IOException, BaseException {
        Member member = postRepository.findMemberbyId(memberId);
        member.modifyStatusAsReported();
        String title = "밍글 계정 사용 정지 알림";
        String body = "운영 정책 위반 및 유저 신고 누적으로 인해 계정 사용이 정지되었습니다. 자세한 문의사항이 있다면 이메일을 통해 문의바랍니다.";
        fcmService.sendMessageTo(member.getFcmToken(), title, body);
    }

    public List<TotalPost> findTotalPostsByIdAndMemberId(Long postId, Long memberId) throws BaseException {
        Member member = memberRepository.findMember(memberId);
        List<TotalPost> totalPostList = postRepository.findTotalPostsByIdAndMember(postId, member);
        if (totalPostList.size() == 0) {
            throw new BaseException(EMPTY_POSTS_LIST);
        }
        return totalPostList;
    }

    public List<UnivPost> findUnivPostsByIdAndMemberId(Long postId, int univId, Long memberId) throws BaseException {
        List<UnivPost> getUnivAll = postRepository.findUnivPostsByIdAndMemberId(postId, univId, memberId);
        if (getUnivAll.size() == 0) {
            throw new BaseException(EMPTY_POSTS_LIST);
        }
        return getUnivAll;
    }

    public List<PostListDTO> findUnitePostWithMemberLikeCount(Long totalPostId, Long univPostId, Long memberId) throws BaseException {
        Member member = postRepository.findMemberbyId(memberId);
        List<TotalPost> totalPosts = postRepository.findTotalPostWithMemberLikeComment(totalPostId, member);

        List<PostListDTO> totalPostDtos = totalPosts.stream()
                .map(m -> new PostListDTO(m, memberId))
                .collect(Collectors.toList());
        List<UnivPost> univPosts = postRepository.findAllWithMemberLikeCommentCount(member, univPostId);

        List<PostListDTO> univPostDtos = univPosts.stream()
                .map(p -> new PostListDTO(p, memberId))
                .collect(Collectors.toList());
        List<PostListDTO> postListDtos = Stream.concat(totalPostDtos.stream(), univPostDtos.stream())
                .sorted(Comparator.comparing(PostListDTO::getCreatedAtDateTime)
                        .reversed())
                .collect(Collectors.toList());
        if (postListDtos.size() == 0) {
            throw new BaseException(EMPTY_POSTS_LIST);
        }
        return postListDtos;
    }
}
