package community.mingle.app.src.post;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.UnivName;
import community.mingle.app.src.post.model.*;
import community.mingle.app.utils.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.app.config.BaseResponseStatus.TITLE_EMPTY_ERROR;

@Tag(name = "post", description = "게시판/게시물관련 API")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@ApiResponses(value = {
        @ApiResponse(responseCode = "403", description = "토큰을 입력해주세요.(앞에 'Bearer ' 포함)./  토큰을 입력해주세요. / 잘못된 토큰입니다. / 토큰이 만료되었습니다.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content(schema = @Schema(hidden = true)))
})
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final JwtService jwtService;


    /**
     * 3.1 학교 전체 글 리스트 API
     */
    @GetMapping("")
    @Operation(summary = "3.1 getUnivTotalPosts API", description = " 3.1 학교 전체 글 리스트 API")
    public BaseResponse<PostListResponse> getPosts(@RequestParam int category, @RequestParam Long postId) {
        try {
            Long memberId = jwtService.getUserIdx();
            List<UnivPost> univPosts = postService.findPosts(category, postId, memberId);
            List<PostListDTO> result = univPosts.stream()
                    .map(u -> new PostListDTO(u, memberId))
                    .collect(Collectors.toList());
            PostListResponse univPostListResponse = new PostListResponse("학교 전체", result);
            return new BaseResponse<>(univPostListResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 3.2 전체 배스트 게시판 API +
     */
    @GetMapping("/total/best")
    @Operation(summary = "3.2 getTotalBest Posts API", description = "3.2 광장 베스트 게시물 리스트 API")
    @ApiResponses({
            @ApiResponse(responseCode = "3030", description = "인기 게시물이 없어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<PostListResponse> getTotalBest(@RequestParam Long postId) {

        try { //JWT로 해당 유저인지 확인 필요
            Long memberId = jwtService.getUserIdx();
            List<TotalPost> totalPosts = postService.findTotalPostWithMemberLikeComment(postId, memberId);
            //신고 사유 String reason = postService.findReportedPostReason(postId); //신고 사유
            /**
             * 1. 모든 API 에서 reason을 다 찾은 후 아 이건 아닌듯
             * 2. stream 돌려서 reported 된 posts 들만 찾아서 따로 DTO 만듦. 그리고 날짜순으로 합치기 (..)
             */
//            List<PostListDTO> reportedPostResult = null;
//            List<TotalPost> reportedPosts = totalPosts.stream().filter(tp -> tp.getStatus().equals(PostStatus.REPORTED)).collect(Collectors.toList());
//            for (TotalPost reportedPost : reportedPosts) {
//                String reportedPostReason = postService.findReportedPostReason(reportedPost.getId());
//                reportedPostResult.add(new PostListDTO(reportedPost,memberId,reportedPostReason));
//            }
            List<PostListDTO> result = totalPosts.stream()
                    .map(m -> new PostListDTO(m, memberId))
                    .collect(Collectors.toList());
            PostListResponse bestTotalPostListResponse = new PostListResponse(result);
            return new BaseResponse<>(bestTotalPostListResponse);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.3 학교 베스트 게시판 API +
     */
    @GetMapping("/univ/best")
    @Operation(summary = "3.3 getUnivBest Posts API", description = "3.3 학교 베스트 게시물 리스트 API")
    @ApiResponses({
            @ApiResponse(responseCode = "3030", description = "인기 게시물이 없어요.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다..", content = @Content(schema = @Schema(hidden = true)))
    })
    public BaseResponse<PostListResponse> getUnivBest(@RequestParam Long postId) {
        try {
            Long memberIdByJwt = jwtService.getUserIdx();
            UnivName univ = postService.findUniv();
            String univName = univ.getUnivName().substring(0, 3);
            List<UnivPost> univPosts = postService.findAllWithMemberLikeCommentCount(postId);
            List<PostListDTO> result = univPosts.stream()
                    .map(p -> new PostListDTO(p, memberIdByJwt))
                    .collect(Collectors.toList());
            PostListResponse bestUnivPostListResponse = new PostListResponse(univName, result);
            return new BaseResponse<>(bestUnivPostListResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 3.4 전체 게시판 리스트 API (페이징)
     */
    @GetMapping("/total")
    @Operation(summary = "3.4 getTotalPosts API", description = "3.4 광장 게시판 게시물 리스트 API")
    @ApiResponses({
            @ApiResponse(responseCode = "3032", description = "해당 카테고리에 게시물이 없습니다.", content = @Content(schema = @Schema(hidden = true)))
    })
    public BaseResponse<PostListResponse> getTotalPosts(@RequestParam int category, @RequestParam Long postId) {
        try {
            Long memberId = jwtService.getUserIdx();
            List<TotalPost> totalPosts = postService.findTotalPost(category, postId, memberId);
            List<PostListDTO> result = totalPosts.stream()
                    .map(p -> new PostListDTO(p, memberId))
                    .collect(Collectors.toList());
            PostListResponse totalPostListResponse = new PostListResponse(result);
            return new BaseResponse<>(totalPostListResponse);
        } catch (BaseException e) {
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 3.5 학교 게시판 리스트 조회 API
     */
    @GetMapping("/univ")
    @Operation(summary = "3.5 getUnivPosts API", description = " 3.5 학교 게시판 게시물 리스트 API")
    public BaseResponse<PostListResponse> getUnivPosts(@RequestParam int category, @RequestParam Long postId) {
        try {
            UnivName univ = postService.findUniv();
            Long memberId = jwtService.getUserIdx();
            String univName = univ.getUnivName().substring(0, 3);
            List<UnivPost> univPosts = postService.findUnivPost(category, postId, univ.getId(), memberId);
            List<PostListDTO> result = univPosts.stream()
                    .map(u -> new PostListDTO(u, memberId))
                    .collect(Collectors.toList());
            PostListResponse univPostListResponse = new PostListResponse(univName, result);
            return new BaseResponse<>(univPostListResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 3.6 통합 게시물 작성 API
     */
    @Operation(summary = "3.6 createTotalPosts API", description = "3.6 통합 게시물 생성 API")
    @PostMapping("/total")
    @ApiResponses({
            @ApiResponse(responseCode = "3032", description = "유효하지 않은 카테고리 입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3033", description = "게시물 생성에 실패하였습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<CreatePostResponse> createTotalPost(@ModelAttribute CreatePostRequest createPostRequest) {
        try {
            return new BaseResponse<>(postService.createTotalPost(createPostRequest));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.7 학교 게시물 작성 API
     */
    @Operation(summary = "3.7 createUnivPosts API", description = "3.7 학교 게시물 생성 API")
    @PostMapping("/univ")
    @ApiResponses({
            @ApiResponse(responseCode = "3032", description = "유효하지 않은 카테고리 입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3033", description = "게시물 생성에 실패하였습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<CreatePostResponse> createUnivPost(@ModelAttribute CreatePostRequest createPostRequest) {
        try {
            System.out.println(createPostRequest.getMultipartFile());
            return new BaseResponse<>(postService.createUnivPost(createPostRequest));
        } catch (BaseException exception) {
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.8 게시물 카테고리 불러오기 API
     */
    @Operation(summary = "3.8 getPostCategory API", description = "3.8 게시물 카테고리 불러오기 API")
    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다..", content = @Content(schema = @Schema(hidden = true)))
    @GetMapping("/category")
    public BaseResponse<List<PostCategoryResponse>> getPostCategory() {
        try {
            List<PostCategoryResponse> categoryList = postService.getPostCategory();
            return new BaseResponse<>(categoryList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 3.9.1 통합 게시물 상세 - 게시물 API
     */
    @GetMapping("/total/{totalPostId}")
    @Operation(summary = "3.9.1 totalPostDetail API", description = "3.9.1 통합 게시물 상세 - 게시물 부분 API")
    @ApiResponses({
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3036", description = "삭제되거나 신고된 게시물 입니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<PostResponse> totalPostDetail(@PathVariable Long totalPostId) { //dto->response
        try {
            TotalPost totalPost = postService.getTotalPost(totalPostId);
            postService.updateView(totalPostId);
            PostResponse totalPostResponse = postService.getTotalPost(totalPost); //메소드이름 좀 맞추자
            return new BaseResponse<>(totalPostResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 3.9.2 통합 게시물 상세 - 댓글 API +
     */
    @GetMapping("/total/{totalPostId}/comment")
    @Operation(summary = "3.9.2 totalPostDetailComment API", description = "3.9.2 통합 게시물 상세 - 댓글 부분 API")
    @ApiResponses({
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3036", description = "삭제되거나 신고된 게시물 입니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<List<CommentResponse>> totalPostDetailComment(@PathVariable Long totalPostId) {

        try {
            List<CommentResponse> totalCommentResponseList = postService.getTotalCommentList(totalPostId);
            return new BaseResponse<>(totalCommentResponseList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 3.10.1 학교 게시물 상세 - 게시물 API
     */
    @GetMapping("/univ/{univPostId}")
    @Operation(summary = "3.10.1 getUnivPost API", description = "3.10 학교 게시물 상세 - 게시물 API")
    @ApiResponses({
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3036", description = "삭제되거나 신고된 게시물 입니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<PostResponse> getUnivPost(@PathVariable Long univPostId) {
        try {
//            UnivPost univPost = postService.getUnivPost(univPostId);
//            UnivPostDTO univPostDTO = new UnivPostDTO(univPost); //DTO 로 변환
            postService.updateViewUniv(univPostId);
            PostResponse univPostResponse = postService.getUnivPost(univPostId);
            return new BaseResponse<>(univPostResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 3.10.2 학교 게시물 상세 - 댓글 API
     */
    @GetMapping("/univ/{univPostId}/comment")
    @Operation(summary = "3.10.2 getUnivPostComment API", description = "3.10.2 학교 게시물 상세 - 댓글 API")
    @ApiResponses({
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3036", description = "삭제되거나 신고된 게시물 입니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<List<CommentResponse>> univPostComment(@PathVariable Long univPostId) { //dto -> response
        try {
            List<CommentResponse> univCommentResponseList = postService.getUnivComments(univPostId);
            return new BaseResponse<>(univCommentResponseList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 3.11 통합 게시물 수정 API
     */
    @Operation(summary = "3.11 patchTotalPosts API", description = "3.11 통합 게시물 수정 API")
    @PatchMapping("/total/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "2020", description = "회원 정보를 찾을 수 없습니다..", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3020", description = "게시물 수정을 실패했습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3021", description = "제목/본문을 입력해주세요.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3040", description = "게시물 수정 권한이 없습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> updateTotalPost(@PathVariable Long id, @RequestBody @Valid UpdatePostRequest updatePostRequest) {
        //empty일 경우 (title&content)
//        if (updatePostRequest.getTitle().length() == 0) {
//            return new BaseResponse<>(TITLE_EMPTY_ERROR);
//        }
        try {
            if (updatePostRequest.getTitle() == null || updatePostRequest.getContent() == null) {
                return new BaseResponse<>(TITLE_EMPTY_ERROR);
            }
        } catch (Exception e) {
            return new BaseResponse<>(TITLE_EMPTY_ERROR);
        }
        try {
            postService.updateTotalPost(id, updatePostRequest);
            String result = "게시물 수정에 성공하였습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.12 학교 게시물 수정 API
     */
    @Operation(summary = "3.12 patchUnivPosts API", description = "3.12 학교 게시물 수정 API")
    @PatchMapping("/univ/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "2020", description = "회원 정보를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3020", description = "게시물 수정을 실패했습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3021", description = "제목/본문을 입력해주세요.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3040", description = "게시물 수정 권한이 없습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> updateUnivPost(@PathVariable Long id, @RequestBody @Valid UpdatePostRequest updatePostRequest) {
        //empty일 경우 (title&content)
        try {
            if (updatePostRequest.getTitle() == null || updatePostRequest.getContent() == null) {
                return new BaseResponse<>(TITLE_EMPTY_ERROR);
            }
        } catch (Exception e) {
            return new BaseResponse<>(TITLE_EMPTY_ERROR);
        }
        try {
            postService.updateUnivPost(id, updatePostRequest);
            String result = "게시물 수정에 성공하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.13 통합 게시물 삭제 API
     */
    @Operation(summary = "3.13 deleteTotalPost API", description = "3.13 통합 게시물 삭제 API")
    @PatchMapping("/total/status/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "2020", description = "회원 정보를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3020", description = "게시물 수정을 실패했습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3021", description = "제목을 입력해주세요.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3025", description = "게시물 삭제를 실패했습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> deleteTotalPost(@PathVariable Long id) {
        try {
            postService.deleteTotalPost(id);
            String result = "게시물 삭제에 성공하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.14 학교 게시물 삭제 API
     */
    @Operation(summary = "3.14 deleteUnivPost API", description = "3.14 학교 게시물 삭제 API")
    @PatchMapping("/univ/status/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "2020", description = "회원 정보를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3020", description = "게시물 수정을 실패했습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3021", description = "제목을 입력해주세요.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3025", description = "게시물 삭제를 실패했습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> deleteUnivPost(@PathVariable Long id) {
        try {
            postService.deleteUnivPost(id);
            String result = "게시물 삭제에 성공하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.15 통합 게시물 좋아요 api + 인기 게시물 알림
     */
    @Operation(summary = "3.15  LikesTotalPost API", description = "3.15 통합 게시물 좋아요 api")
    @ApiResponses({
            @ApiResponse(responseCode = "3025", description = "게시물 삭제를 실패했습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3036", description = "삭제되거나 신고된 게시물 입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3060", description = "이미 좋아요를 눌렀어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping("/total/likes")
    public BaseResponse<LikePostResponse> likesTotalPost(@RequestParam Long postIdx) {
        try {
            return new BaseResponse<>(postService.likesTotalPost(postIdx));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.16 학교 게시물 좋아요 api + 인기 게시물 알림
     */
    @Operation(summary = "3.16 LikesUnivPost API", description = "3.16 학교 게시물 좋아요 api")
    @PostMapping("/univ/likes")
    @ApiResponses({
            @ApiResponse(responseCode = "3025", description = "게시물 삭제를 실패했습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3036", description = "삭제되거나 신고된 게시물 입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3060", description = "이미 좋아요를 눌렀어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<LikePostResponse> likesUnivPost(@RequestParam Long postIdx) {
        try {
            return new BaseResponse<>(postService.likesUnivPost(postIdx));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.17 통합 게시물 좋아요 취소 api
     */
    @Operation(summary = "3.17 UnlikeTotalPost API", description = "3.17 통합 게시물 좋아요 취소 api")
    @DeleteMapping("/total/unlike")
    @ApiResponses({
            @ApiResponse(responseCode = "3062", description = "이미 좋아요를 취소했어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> unlikeTotalPost(@RequestParam Long postId) {
        try {
            postService.unlikeTotal(postId);
            String result = "좋아요가 취소되었습니다";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.18 학교 게시물 좋아요 취소 api
     */
    @Operation(summary = "3.18 UnlikeUnivPost API", description = "3.18 학교 게시물 좋아요 취소 api")
    @DeleteMapping("/univ/unlike")
    @ApiResponses({
            @ApiResponse(responseCode = "3062", description = "이미 좋아요를 취소했어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> unlikeUnivPost(@RequestParam Long postId) {
        try {
            postService.unlikeUniv(postId);
            String result = "좋아요가 취소되었습니다";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.19 통합 게시물 스크랩 api
     */
    @Operation(summary = "3.19  scrapTotalPost API", description = "3.19 통합 게시물 스크랩 api")
    @PostMapping("/total/scrap")
    @ApiResponses({
            @ApiResponse(responseCode = "2020", description = "회원 정보를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3025", description = "게시물 삭제를 실패했습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3036", description = "삭제되거나 신고된 게시물 입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3061", description = "이미 스크랩을 눌렀어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<ScrapPostResponse> scrapTotalPost(@RequestParam Long postIdx) {
        try {
            return new BaseResponse<>(postService.scrapTotalPost(postIdx));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.20 학교 게시물 스크랩 api
     */
    @Operation(summary = "3.20  scrapUnivPost API", description = "3.20 학교 게시물 스크랩 api")
    @PostMapping("/univ/scrap")
    @ApiResponses({
            @ApiResponse(responseCode = "2020", description = "회원 정보를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3025", description = "게시물 삭제를 실패했습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3036", description = "삭제되거나 신고된 게시물 입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3061", description = "이미 스크랩을 눌렀어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<ScrapPostResponse> scrapUnivPost(@RequestParam Long postIdx) {
        try {
            return new BaseResponse<>(postService.scrapUnivPost(postIdx));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.21 통합 게시물 스크랩 취소 api
     */
    @Operation(summary = "UnscrapTotalPost API", description = "통합 게시물 스크랩 취소 api")
    @DeleteMapping("/total/deletescrap")
    @ApiResponses({
            @ApiResponse(responseCode = "3063", description = "이미 스크랩을 취소했어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> deleteScrapTotalPost(@RequestParam Long postId) {
        try {
            postService.deleteScrapTotal(postId);
            String result = "저장이 취소되었습니다";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());

        }
    }


    /**
     * 3.22 학교 게시물 스크랩 취소 api
     */
    @Operation(summary = "UnlikeTotalPost API", description = "통합 게시물 스크랩 취소 api")
    @DeleteMapping("/univ/deletescrap")
    @ApiResponses({
            @ApiResponse(responseCode = "3063", description = "이미 스크랩을 취소했어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> deleteScrapUnivPost(@RequestParam Long postId) {
        try {
            postService.deleteScrapUniv(postId);
            String result = "저장이 취소되었습니다";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.23 전체 게시판 검색 api +
     */
    @Operation(summary = "searchTotalPost API", description = "전체게시판 검색 api")
    @GetMapping("total/search")
    public BaseResponse<PostListResponse> searchTotalPost(@RequestParam(value = "keyword") String keyword) {
        try {
            Long memberId = jwtService.getUserIdx();
            List<TotalPost> totalPosts = postService.findAllSearch(keyword, memberId);
            List<PostListDTO> result = totalPosts.stream()
                    .map(tp -> new PostListDTO(tp, memberId))
                    .collect(Collectors.toList());
            PostListResponse searchTotalPostResponse = new PostListResponse("광장", result);
            return new BaseResponse<>(searchTotalPostResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 3.24 학교 게시판 검색 api +
     */
    @Operation(summary = "searchUnivPost API", description = "학교게시판 검색 api")
    @GetMapping("univ/search")
    public BaseResponse<PostListResponse> searchUnivPost(@RequestParam(value = "keyword") String keyword) {
        try {
            Long memberId = jwtService.getUserIdx();
            List<UnivPost> univPosts = postService.findUnivSearch(keyword, memberId);
            List<PostListDTO> result = univPosts.stream()
                    .map(up -> new PostListDTO(up, memberId))
                    .collect(Collectors.toList());
            PostListResponse searchUnivPostResponse = new PostListResponse("잔디밭", result);
            return new BaseResponse<>(searchUnivPostResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 3.25 게시물 가리기 전체 API
     */
    @Operation(summary = "3.25  blindTotalPost API", description = "3.25 통합 게시물 가리기 api")
    @ApiResponses({
            @ApiResponse(responseCode = "3025", description = "게시물 삭제를 실패했습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3036", description = "삭제되거나 신고된 게시물 입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3060", description = "이미 게시물을 가렸어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping("/total/blind")
    public BaseResponse<String> blindTotalPost(@RequestParam Long postId) {
        try {
            return new BaseResponse<>(postService.blindTotalPost(postId));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 3.26 게시물 가리기 학교 API
     */
    @Operation(summary = "3.26  blindUnivPost API", description = "3.26 학교 게시물 가리기 api")
    @ApiResponses({
            @ApiResponse(responseCode = "3025", description = "게시물 삭제를 실패했습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3036", description = "삭제되거나 신고된 게시물 입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3060", description = "이미 게시물을 가렸어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping("/univ/blind")
    public BaseResponse<String> blindUnivPost(@RequestParam Long postId) {
        try {
            return new BaseResponse<>(postService.blindUnivPost(postId));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 3.27 게시물 가리기 취소 전체 API
     */
    @Operation(summary = "3.27 UnblindTotalPost API", description = "3.27 전체 게시물 가리기 취소 api")
    @DeleteMapping("/total/deleteblind")
    public BaseResponse<String> unblindTotalPost(@RequestParam Long postId) {
        try {
            return new BaseResponse<>(postService.unblindTotalPost(postId));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 3.28 게시물 가리기 취소 학교 API
     */
    @Operation(summary = "3.28 UnblindUnivPost API", description = "3.28 학교 게시물 가리기 취소 api")
    @DeleteMapping("/univ/deleteblind")
    public BaseResponse<String> unblindUnivPost(@RequestParam Long postId) {
        try {
            return new BaseResponse<>(postService.unblindUnivPost(postId));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 3.29 유저 차단 전체 api
     */
    @Operation(summary = "3.29 userBlockTotal API", description = "3.29 유저 차단 전체 api")
    @PostMapping("/total/block")
    public BaseResponse<String> blockMemberInTotalPost(@RequestParam Long postId) throws BaseException {
        try {
            return new BaseResponse<>(postService.blockMemberInTotalPost(postId));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 3.30 유저 차단 학교 api
     */
    @Operation(summary = "3.30  userBlockUniv API", description = "3.30 유저 차단 학교 api")
    @PostMapping("/univ/block")
    public BaseResponse<String> blockMemberInUnivPost(@RequestParam Long postId) throws BaseException {
        try {
            return new BaseResponse<>(postService.blockMemberInUnivPost(postId));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 3.31 광장 전체글 api
     */
    @Operation(summary = "3.31 광장 전체글 api", description = "3.31 광장 전체글 api")
    @GetMapping("/total/posts")
    public BaseResponse<PostListResponse> getUnitedTotalPosts(@RequestParam Long postId) {
        try {
            Long memberId = jwtService.getUserIdx();
            List<TotalPost> totalPosts = postService.findTotalPostsByIdAndMemberId(postId, memberId);
            List<PostListDTO> result = totalPosts.stream()
                    .map(p -> new PostListDTO(p, memberId))
                    .collect(Collectors.toList());
            PostListResponse totalPostListResponse = new PostListResponse(result);
            return new BaseResponse<>(totalPostListResponse);
        } catch (BaseException e) {
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 3.32 잔디밭 전체글 api
     */
    @GetMapping("/univ/posts")
    @Operation(summary = "3.32 잔디밭 전체글 api", description = "3.32 잔디밭 전체글 api")
    public BaseResponse<PostListResponse> getUnitedUnivPosts(@RequestParam Long postId) {
        try {
            UnivName univ = postService.findUniv();
            Long memberId = jwtService.getUserIdx();
            String univName = univ.getUnivName().substring(0, 3);
            List<UnivPost> univPosts = postService.findUnivPostsByIdAndMemberId(postId, univ.getId(), memberId);
            List<PostListDTO> result = univPosts.stream()
                    .map(u -> new PostListDTO(u, memberId))
                    .collect(Collectors.toList());
            PostListResponse univPostListResponse = new PostListResponse(univName, result);
            return new BaseResponse<>(univPostListResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

}

