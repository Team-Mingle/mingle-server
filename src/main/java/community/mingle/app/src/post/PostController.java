

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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import community.mingle.app.src.post.model.*;


import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.")
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
     * 3.2 홍콩 배스트 게시판 API
     */
    @GetMapping("/total/best")
    @Operation(summary = "3.2 getTotalBest Posts API", description = "3.2 광장 베스트 게시물 리스트 API")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
            @ApiResponse(responseCode = "3030", description = "최근 3일간 올라온 베스트 게시물이 없습니다."),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.")
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
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요."),
            @ApiResponse(responseCode = "3030", description = "최근 3일간 올라온 베스트 게시물이 없습니다."),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.")
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
     * 3.4 광장 게시판 리스트 API
     */
    @GetMapping("/total")
    @Operation(summary = "3.4 getTotal Posts API", description = "3.4 광장 게시판 게시물 리스트 API")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.")
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
     * 3.5 전체 게시물 작성 API
     */
    @PostMapping("/univ")
    @Operation(summary = "3.4 getTotal Posts API", description = "3.4 광장 게시판 게시물 리스트 API")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요."),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<PostCreateResponse> createPost (@RequestBody @Valid PostCreateRequest postCreateRequest){
        try{
            return new BaseResponse<>(postService.createPost(postCreateRequest));
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


}
