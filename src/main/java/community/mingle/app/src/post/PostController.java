

package community.mingle.app.src.post;


import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.domain.Banner;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.post.model.GetTotalBestPostsResponse;
import community.mingle.app.utils.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import community.mingle.app.src.post.model.*;


import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.app.config.BaseResponseStatus.*;

@Tag(name = "post", description = "게시판/게시물관련 API")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

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
    @Operation(summary = "3.4 getTotal Posts API", description = "3.4 광장 게시판 게시물 리스트 API")
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

}
