package community.mingle.app.src.post;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.domain.Banner;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.UnivName;
import community.mingle.app.src.post.model.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.app.config.BaseResponseStatus.*;

@Tag(name = "post", description = "게시판/게시물관련 API")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@ApiResponses(value = {
        @ApiResponse(responseCode = "403", description = "토큰을 입력해주세요.(앞에 'Bearer ' 포함)./  토큰을 입력해주세요. / 잘못된 토큰입니다. / 토큰이 만료되었습니다.", content = @Content (schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content (schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
})
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;


    /**
     * 3.1 광고 배너 API
     */
    @GetMapping("/banner")
    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다..", content = @Content (schema = @Schema(hidden = true)))
    @Operation(summary = "3.1 getBanner API", description = "3.1 홈 화면 배너 리스트 API")
    public BaseResponse<List<BannerResponse>> getBanner(){
        try {
            List<Banner> banner = postService.findBanner();
            List<BannerResponse> result = banner.stream()
                    .map(m -> new BannerResponse(m))
                    .collect(Collectors.toList());
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 3.2 전체 배스트 게시판 API
     */
    @GetMapping("/total/best")
    @Operation(summary = "3.2 getTotalBest Posts API", description = "3.2 광장 베스트 게시물 리스트 API")
    @ApiResponses ({
            @ApiResponse(responseCode = "3030", description = "최근 3일간 올라온 베스트 게시물이 없습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    public BaseResponse<List<BestTotalPostResponse>> getTotalBest() {
        try { //JWT로 해당 유저인지 확인 필요
            List<TotalPost> totalPosts = postService.findTotalPostWithMemberLikeComment();
            List<BestTotalPostResponse> result = totalPosts.stream()
                    .map(m -> new BestTotalPostResponse(m))
                    .collect(Collectors.toList());

            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 3.3 학교 베스트 게시판 API
     */
    @GetMapping("/univ/best")
    @Operation(summary = "3.3 getUnivBest Posts API", description = "3.3 학교 베스트 게시물 리스트 API")
    @ApiResponses ({
            @ApiResponse(responseCode = "3030", description = "최근 3일간 올라온 베스트 게시물이 없습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    public BaseResponse<List<BestUnivPostResponse>> getUnivBest() {
        try {
            List<UnivPost> univPosts = postService.findAllWithMemberLikeCommentCount();
            List<BestUnivPostResponse> result = univPosts.stream()
                    .map(p -> new BestUnivPostResponse(p))
                    .collect(Collectors.toList());
            return new BaseResponse<>(result);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }



    /**
     * 3.4 전체 게시판 리스트 API (페이징)
     */
    @GetMapping("/total")
    @Operation(summary = "3.4 getTotalPosts API", description = "3.4 광장 게시판 게시물 리스트 API")
    @ApiResponses ({
            @ApiResponse(responseCode = "3032", description = "해당 카테고리에 게시물이 없습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
    public BaseResponse<TotalPostListResponse> getTotalPosts (@RequestParam int category, @RequestParam Long postId) {
        try {
            List<TotalPost> totalPosts = postService.findTotalPost(category, postId);
            List<TotalPostListDTO> result = totalPosts.stream()
                    .map(p -> new TotalPostListDTO(p))
                    .collect(Collectors.toList());
            TotalPostListResponse totalPostListResponse = new TotalPostListResponse(null, result);
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
    public BaseResponse<UnivPostListResponse> getUnivPosts (@RequestParam int category,  @RequestParam Long postId) {
        try {
            UnivName univ = postService.findUniv();
            String univName = univ.getUnivName().substring(0,3);
            List<UnivPost> univPosts = postService.findUnivPost(category, postId, univ.getId());
            List<UnivPostListDTO> result = univPosts.stream()
                    .map(u -> new UnivPostListDTO(u))
                    .collect(Collectors.toList());
            UnivPostListResponse univPostListResponse = new UnivPostListResponse(univName, result);
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
    @ApiResponses ({
            @ApiResponse(responseCode = "3032", description = "유효하지 않은 카테고리 입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3033", description = "게시물 생성에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    public BaseResponse<CreatePostResponse> createTotalPost (@ModelAttribute CreatePostRequest createPostRequest){
        try{
            return new BaseResponse<>(postService.createTotalPost(createPostRequest));
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
    } }




    /**
     * 3.7 학교 게시물 작성 API
     */
    @Operation(summary = "3.7 createUnivPosts API", description = "3.7 학교 게시물 생성 API")
    @PostMapping("/univ")
    @ApiResponses ({
            @ApiResponse(responseCode = "3032", description = "유효하지 않은 카테고리 입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3033", description = "게시물 생성에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    public BaseResponse<CreatePostResponse> createUnivPost (@ModelAttribute CreatePostRequest createPostRequest){
        try{
            return new BaseResponse<>(postService.createUnivPost(createPostRequest));
        }catch (BaseException exception){
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.8 게시물 카테고리 불러오기 API
     */
    @Operation(summary = "3.8 getPostCategory API", description = "3.8 게시물 카테고리 불러오기 API")
    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다..", content = @Content (schema = @Schema(hidden = true)))
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
    public BaseResponse<TotalPostResponse> totalPostDetail(@PathVariable Long totalPostId) { //dto->response
        try {
            TotalPost totalPost = postService.getTotalPost(totalPostId);
            postService.updateView(totalPostId);
            TotalPostResponse totalPostResponse = postService.getTotalPostDto(totalPost); //메소드이름 좀 맞추자
            return new BaseResponse<>(totalPostResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 3.9.2 통합 게시물 상세 - 댓글 API
     */
    @GetMapping("/total/{totalPostId}/comment")
    @Operation(summary = "3.9.2 totalPostDetailComment API", description = "3.9.2 통합 게시물 상세 - 댓글 부분 API")
    public BaseResponse<List<TotalCommentResponse>> totalPostDetailComment(@PathVariable Long totalPostId) {

        try {
            List<TotalCommentResponse> totalCommentResponseList = postService.getTotalCommentList(totalPostId);
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
    public BaseResponse<UnivPostResponse> getUnivPost(@PathVariable Long univPostId) {
        try {
//            UnivPost univPost = postService.getUnivPost(univPostId);
//            UnivPostDTO univPostDTO = new UnivPostDTO(univPost); //DTO 로 변환
            postService.updateViewUniv(univPostId);
            UnivPostResponse univPostResponse = postService.getUnivPost(univPostId);
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
    public BaseResponse<List<UnivCommentResponse>> univPostComment(@PathVariable Long univPostId) { //dto -> response
        try {
            List<UnivCommentResponse> univCommentResponseList = postService.getUnivComments(univPostId);
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
    @ApiResponses ({
            @ApiResponse(responseCode = "2020", description = "회원 정보를 찾을 수 없습니다..",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3020", description = "게시물 수정을 실패했습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3021", description = "제목을 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3040", description = "게시물 수정 권한이 없습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> updateTotalPost (@PathVariable Long id, @RequestBody @Valid UpdatePostRequest updatePostRequest){
        //empty일 경우 (title&content)
        if (updatePostRequest.getTitle().length() == 0) {
            return new BaseResponse<>(TITLE_EMPTY_ERROR);
        }
        try{
            postService.updateTotalPost(id, updatePostRequest);
            String result = "게시물 수정에 성공하였습니다.";
            return new BaseResponse<>(result);

        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.12 학교 게시물 수정 API
     */
    @Operation(summary = "3.12 patchUnivPosts API", description = "3.12 학교 게시물 수정 API")
    @PatchMapping("/univ/{id}")
    @ApiResponses ({
            @ApiResponse(responseCode = "2020", description = "회원 정보를 찾을 수 없습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3020", description = "게시물 수정을 실패했습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3021", description = "제목을 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3040", description = "게시물 수정 권한이 없습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> updateUnivPost (@PathVariable Long id, @RequestBody @Valid UpdatePostRequest updatePostRequest){
        //empty일 경우 (title&content)
        if (updatePostRequest.getTitle().length() == 0) {
            return new BaseResponse<>(TITLE_EMPTY_ERROR);
        }
        try{
            postService.updateUnivPost(id, updatePostRequest);
            String result = "게시물 수정에 성공하였습니다.";
            return new BaseResponse<>(result);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.13 통합 게시물 삭제 API
     */
    @Operation(summary = "3.13 deleteTotalPost API", description = "3.13 통합 게시물 삭제 API")
    @PatchMapping("/total/status/{id}")
    @ApiResponses ({
            @ApiResponse(responseCode = "2020", description = "회원 정보를 찾을 수 없습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3020", description = "게시물 수정을 실패했습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3021", description = "제목을 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3025", description = "게시물 삭제를 실패했습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> deleteTotalPost (@PathVariable Long id){
        try{
            postService.deleteTotalPost(id);
            String result = "게시물 삭제에 성공하였습니다.";
            return new BaseResponse<>(result);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.14 학교 게시물 삭제 API
     */
    @Operation(summary = "3.14 deleteUnivPost API", description = "3.14 학교 게시물 삭제 API")
    @PatchMapping("/univ/status/{id}")
    @ApiResponses ({
            @ApiResponse(responseCode = "2020", description = "회원 정보를 찾을 수 없습니다..",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3020", description = "게시물 수정을 실패했습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3021", description = "제목을 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3025", description = "게시물 삭제를 실패했습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> deleteUnivPost (@PathVariable Long id){
        try{
            postService.deleteUnivPost(id);
            String result = "게시물 삭제에 성공하였습니다.";
            return new BaseResponse<>(result);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 3.15 통합 게시물 좋아요 api + 인기 게시물 알림
     */
    @Operation(summary = "3.15  LikesTotalPost API", description = "3.15 통합 게시물 좋아요 api")
    @PostMapping("/total/likes")
    public BaseResponse<LikeTotalPostResponse> likesTotalPost (@RequestParam Long postIdx){
        try{
            return new BaseResponse<>(postService.likesTotalPost(postIdx));
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 3.16 학교 게시물 좋아요 api + 인기 게시물 알림
     */
    @Operation(summary = "3.16 LikesUnivPost API", description = "3.16 학교 게시물 좋아요 api")
    @PostMapping("/univ/likes")
    public BaseResponse<LikeUnivPostResponse> likesUnivPost (@RequestParam Long postIdx){
        try{
            return new BaseResponse<>(postService.likesUnivPost(postIdx));
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 3.17 통합 게시물 좋아요 취소 api
     */
    @Operation(summary = "3.17 UnlikeTotalPost API", description = "3.17 통합 게시물 좋아요 취소 api")
    @DeleteMapping("/total/unlike")
    public BaseResponse<String> unlikeTotalPost (@RequestParam Long likeIdx){
        try{
            postService.unlikeTotal(likeIdx);
            String result = "좋아요가 취소되었습니다";
            return new BaseResponse<>(result);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 3.18 학교 게시물 좋아요 취소 api
     */
    @Operation(summary = "3.18 UnlikeUnivPost API", description = "3.18 학교 게시물 좋아요 취소 api")
    @DeleteMapping("/univ/unlike")
    public BaseResponse<String> unlikeUnivPost (@RequestParam Long likeIdx){
        try{
            postService.unlikeUniv(likeIdx);
            String result = "좋아요가 취소되었습니다";
            return new BaseResponse<>(result);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 3.19 통합 게시물 스크랩 api
     */
    @Operation(summary = "3.19  scrapTotalPost API", description = "3.19 통합 게시물 스크랩 api")
    @PostMapping("/total/scrap")
    @ApiResponses ({
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    public BaseResponse<ScrapTotalPostResponse> scrapTotalPost (@RequestParam Long postIdx){
        try{
            return new BaseResponse<>(postService.scrapTotalPost(postIdx));
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 3.20 학교 게시물 스크랩 api
     */
    @Operation(summary = "3.20  scrapUnivPost API", description = "3.20 학교 게시물 스크랩 api")
    @PostMapping("/univ/scrap")
    @ApiResponses ({
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    public BaseResponse<ScrapUnivPostResponse> scrapUnivPost (@RequestParam Long postIdx){
        try{
            return new BaseResponse<>(postService.scrapUnivPost(postIdx));
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.21 통합 게시물 스크랩 취소 api
     */
    @Operation(summary = "UnscrapTotalPost API", description = "통합 게시물 스크랩 취소 api")
    @DeleteMapping("/total/deleteScrap")
    public BaseResponse<String> deleteScrapTotalPost (@RequestParam Long scrapIdx) {
        try {
            postService.deleteScrapTotal(scrapIdx);
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
    @DeleteMapping("/univ/deleteScrap")
    public BaseResponse<String> deleteScrapUnivPost (@RequestParam Long scrapIdx){
        try{
            postService.deleteScrapUniv(scrapIdx);
            String result = "저장이 취소되었습니다";
            return new BaseResponse<>(result);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.23 전체 게시판 검색 api
     */
    @Operation(summary = "searchTotalPost API", description = "전체게시판 검색 api")
    @GetMapping("total/search")
    public BaseResponse<List<SearchTotalPostResponse>> searchTotalPost(@RequestParam(value="keyword") String keyword) {
        try {
            List<TotalPost> totalPosts = postService.findAllSearch(keyword);
            List<SearchTotalPostResponse> result = totalPosts.stream()
                    .map(tp -> new SearchTotalPostResponse(tp))
                    .collect(Collectors.toList());
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }



    /**
     * 3.24 학교 게시판 검색 api
     */
    @Operation(summary = "searchUnivPost API", description = "학교게시판 검색 api")
    @GetMapping("univ/search")
    public BaseResponse<List<SearchUnivPostResponse>> searchUnivPost(@RequestParam(value="keyword") String keyword) {
        try {
            List<UnivPost> univPosts = postService.findUnivSearch(keyword);
            List<SearchUnivPostResponse> result = univPosts.stream()
                    .map(up -> new SearchUnivPostResponse(up))
                    .collect(Collectors.toList());
            return new BaseResponse<>(result);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }




}

