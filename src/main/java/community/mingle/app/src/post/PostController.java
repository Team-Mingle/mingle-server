package community.mingle.app.src.post;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.domain.Banner;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.post.model.*;
import community.mingle.app.utils.JwtService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class    PostController {

    private final PostService postService;
    private final JwtService jwtService;
    private final PostRepository postRepository;


    /**
     * 3.1 광고 배너 API
     */
    @GetMapping("/banner")
    @Operation(summary = "3.1 getBanner API", description = "3.1 홈 화면 배너 리스트 API")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
    public BaseResponse<List<GetBannerResponse>> getBanner(){
        try {
            List<Banner> banner = postService.findBanner();
            List<GetBannerResponse> result = banner.stream()
                    .map(m -> new GetBannerResponse(m))
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
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3030", description = "최근 3일간 올라온 베스트 게시물이 없습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    public BaseResponse<List<GetTotalBestPostsResponse>> getTotalBest() {
        try { //JWT로 해당 유저인지 확인 필요
            List<TotalPost> totalPosts = postService.findTotalPostWithMemberLikeComment();
            List<GetTotalBestPostsResponse> result = totalPosts.stream()
                    .map(m -> new GetTotalBestPostsResponse(m))
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
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3030", description = "최근 3일간 올라온 베스트 게시물이 없습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
    public BaseResponse<List<GetUnivBestResponse>> getUnivBest() {

        try {
            List<UnivPost> univPosts = postService.findAllWithMemberLikeCommentCount();
            List<GetUnivBestResponse> result = univPosts.stream()
                    .map(p -> new GetUnivBestResponse(p))
                    .collect(Collectors.toList());
            return new BaseResponse<>(result);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }



    /**
     * 3.4 전체 게시판 리스트 API
     */
    @GetMapping("/total")
    @Operation(summary = "3.4 getTotalPosts API", description = "3.4 광장 게시판 게시물 리스트 API")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3032", description = "해당 카테고리에 게시물이 없습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
    public BaseResponse<List<GetTotalPostsResponse>> getAll(@RequestParam int category) {
        try {
            List<TotalPost> totalPosts = postService.findTotalPost(category);
            List<GetTotalPostsResponse> result = totalPosts.stream()
                    .map(p -> new GetTotalPostsResponse(p))
                    .collect(Collectors.toList());
            return new BaseResponse<>(result);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 3.4 전체 게시물 리스트 by paging test
     * @param category
     * @param postId
     */
    @GetMapping("/total/paging")
    public BaseResponse<List<TotalPostListDTO>> getTotalPostsByPaging (@RequestParam int category, @RequestParam Long postId) {
        try {
            List<TotalPost> totalPosts = postService.findTotalPostByPaging(category, postId);
            List<TotalPostListDTO> result = totalPosts.stream()
                    .map(p -> new TotalPostListDTO(p))
                    .collect(Collectors.toList());
            return new BaseResponse<>(result);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }



    /**
     * 3.5 학교 게시판 리스트 조회 API
     */
    @GetMapping("/univ")
    @Operation(summary = "3.5 getUnivPosts API", description = " 3.5 학교 게시판 게시물 리스트 API")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER)
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3030", description = "최근 3일간 올라온 베스트 게시물이 없습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
    public BaseResponse<List<GetUnivPostsResponse>> getUnivAll(@RequestParam int category) {
        try {
            List<UnivPost> univPosts = postService.findUnivPost(category);
            List<GetUnivPostsResponse> result = univPosts.stream()
                    .map(u -> new GetUnivPostsResponse(u))
                    .collect(Collectors.toList());
            return new BaseResponse<>(result);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }



    /**
     * 3.6 통합 게시물 작성 API
     */
    @Operation(summary = "3.6 createTotalPosts API", description = "3.6 통합 게시물 생성 API")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @PostMapping("/total")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3032", description = "유효하지 않은 카테고리 입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3033", description = "게시물 생성에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    public BaseResponse<PostCreateResponse> createTotalPost (@RequestBody @Valid PostCreateRequest postCreateRequest){
        try{
            return new BaseResponse<>(postService.createTotalPost(postCreateRequest));
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }





    /**
     * 3.7 학교 게시물 작성 API
     */
    @Operation(summary = "3.7 createUnivPosts API", description = "3.7 학교 게시물 생성 API")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @PostMapping("/univ")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3032", description = "유효하지 않은 카테고리 입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3033", description = "게시물 생성에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    public BaseResponse<PostCreateResponse> createUnivPost (@RequestBody @Valid PostCreateRequest postCreateRequest){
        try{
            return new BaseResponse<>(postService.createUnivPost(postCreateRequest));
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.9.1 통합 게시물 상세 - 게시물 API
     */
    @GetMapping("/total/{totalPostId}")
    public BaseResponse<TotalPostDto> totalPostDetail(@PathVariable Long totalPostId) {
        try {
            TotalPost totalPost = postService.getTotalPost(totalPostId);

            TotalPostDto totalPostDto = postService.getTotalPostDto(totalPost);

            return new BaseResponse<>(totalPostDto);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }


    /**
     * 3.9.2 통합 게시물 상세 - 댓글 API
     * 댓글 지웠을 때 "삭제된 댓글입니다" 라고 나오는 기능 추가!!!!!
     */
    @GetMapping("/totalcomment/{totalPostId}")
    public BaseResponse<List<TotalCommentDto>> totalPostDetailComment(@PathVariable Long totalPostId) {

        try {
            List<TotalCommentDto> totalCommentDtoList = postService.getTotalCommentList(totalPostId);

            return new BaseResponse<>(totalCommentDtoList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }



    /**
     * 3.10.1 학교 게시물 상세 - 게시물 API
     */
    @GetMapping("/univ/{univPostId}/post")
    @Operation(summary = "3.10.1 getUnivPost API", description = "3.10 학교 게시물 상세 - 게시물 API")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    public BaseResponse<UnivPostDTO> getUnivPost(@PathVariable Long univPostId) {
        try {
//            UnivPost univPost = postService.getUnivPost(univPostId);
//            UnivPostDTO univPostDTO = new UnivPostDTO(univPost); //DTO 로 변환
            UnivPostDTO univPostDTO = postService.getUnivPost(univPostId);
            return new BaseResponse<>(univPostDTO);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    /**
     * 3.10.2 학교 게시물 상세 - 댓글 API
     */
    @GetMapping("/univ/{univPostId}/comment")
    @Operation(summary = "3.10.2 getUnivPostComment API", description = "3.10.2 학교 게시물 상세 - 댓글 API")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER)
    public BaseResponse<List<UnivCommentDTO>> univPostComment(@PathVariable Long univPostId) {
        try {
            List<UnivCommentDTO> univCommentDTOList = postService.getUnivComments(univPostId);
            return new BaseResponse<>(univCommentDTOList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }



    /**
     * 3.11 통합 게시물 수정 API
     */
    @Operation(summary = "3.11 patchTotalPosts API", description = "3.11 통합 게시물 수정 API")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @PatchMapping("/total/{id}")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3020", description = "게시물 수정을 실패했습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> updateTotalPost (@PathVariable Long id, @RequestBody @Valid PatchUpdatePostRequest patchUpdatePostRequest){
        //empty일 경우 (title&content)
        if (patchUpdatePostRequest.getTitle().length() == 0) {
            return new BaseResponse<>(TITLE_EMPTY_ERROR);
        }
        try{
            postService.updateTotalPost(id, patchUpdatePostRequest);
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
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @PatchMapping("/univ/{id}")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3020", description = "게시물 수정을 실패했습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> updateUnivPost (@PathVariable Long id, @RequestBody @Valid PatchUpdatePostRequest patchUpdatePostRequest){
        //empty일 경우 (title&content)
        if (patchUpdatePostRequest.getTitle().length() == 0) {
            return new BaseResponse<>(TITLE_EMPTY_ERROR);
        }
        try{
            postService.updateUnivPost(id, patchUpdatePostRequest);
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
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @PatchMapping("/total/status/{id}")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3025", description = "게시물 삭제를 실패했습니다.", content = @Content (schema = @Schema(hidden = true))),
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
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @PatchMapping("/univ/status/{id}")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3025", description = "게시물 삭제를 실패했습니다.", content = @Content (schema = @Schema(hidden = true))),
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
     * 3.15 통합 게시물 좋아요 api
     */
    @Operation(summary = "3.15  LikesTotalPost API", description = "3.15 통합 게시물 좋아요 api")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @PostMapping("/total/likes")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
    public BaseResponse<PostLikesTotalResponse> likesTotalPost (@RequestParam Long postIdx){
        try{
            return new BaseResponse<>(postService.likesTotalPost(postIdx));
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 3.16 학교 게시물 좋아요 api
     */
    @Operation(summary = "3.16  LikesUnivPost API", description = "3.16  학교 게시물 좋아요 api")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @PostMapping("/univ/likes")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
    public BaseResponse<PostLikesUnivResponse> likesUnivPost (@RequestParam Long postIdx){
        try{
            return new BaseResponse<>(postService.likesUnivPost(postIdx));
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 3.17 통합 게시물 좋아요 취소 api
     */
    @Operation(summary = "UnlikeTotalPost API", description = "통합 게시물 좋아요 취소 api")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @DeleteMapping("/total/unlike")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
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
    @Operation(summary = "UnlikeUnivPost API", description = "학교 게시물 좋아요 취소 api")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @DeleteMapping("/univ/unlike")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
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
    @Operation(summary = "3.17  scrapTotalPost API", description = "3.17 통합 게시물 스크랩 api")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @PostMapping("/total/scrap")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
    public BaseResponse<PostScrapTotalResponse> scrapTotalPost (@RequestParam Long postIdx){
        try{
            return new BaseResponse<>(postService.scrapTotalPost(postIdx));
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 3.20 학교 게시물 스크랩 api
     */
    @Operation(summary = "3.17  scrapTotalPost API", description = "3.17 통합 게시물 스크랩 api")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @PostMapping("/univ/scrap")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
    public BaseResponse<PostScrapUnivResponse> scrapUnivPost (@RequestParam Long postIdx){
        try{
            return new BaseResponse<>(postService.scrapUnivPost(postIdx));
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 3.21 통합 게시물 스크랩  취소 api
     */
    @Operation(summary = "UnscrapTotalPost API", description = "통합 게시물 스크랩 취소 api")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @DeleteMapping("/total/deleteScrap")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
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
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @DeleteMapping("/univ/deleteScrap")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
    public BaseResponse<String> deleteScrapUnivPost (@RequestParam Long scrapIdx){
        try{
            postService.deleteScrapUniv(scrapIdx);
            String result = "저장이 취소되었습니다";
            return new BaseResponse<>(result);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }



}

