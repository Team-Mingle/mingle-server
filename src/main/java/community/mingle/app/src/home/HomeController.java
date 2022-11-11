package community.mingle.app.src.home;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.domain.Banner;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.home.model.HomeBestTotalPostResponse;
import community.mingle.app.src.home.model.HomeBestUnivPostResponse;
import community.mingle.app.src.home.model.BannerResponse;
import community.mingle.app.src.home.model.HomeRecentPostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "home", description = "홈 화면 베스트 API")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@ApiResponses(value = {
        @ApiResponse(responseCode = "403", description = "토큰을 입력해주세요.(앞에 'Bearer ' 포함)./  토큰을 입력해주세요. / 잘못된 토큰입니다. / 토큰이 만료되었습니다.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content(schema = @Schema(hidden = true)))
})
@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;


    /**
     * 3.1 광고 배너 API
     */
    @GetMapping("/banner")
    @Operation(summary = "5.1 getBanner API", description = "5.1 홈 화면 배너 리스트 API")
    public BaseResponse<List<BannerResponse>> getBanner() {
        try {
            List<Banner> banner = homeService.findBanner();
            List<BannerResponse> result = banner.stream()
                    .map(m -> new BannerResponse(m))
                    .collect(Collectors.toList());
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 5.2 홈 전체 배스트 게시판 API
     */
    @GetMapping("/total/best")
    @Operation(summary = "5.2 getHomeTotalBestPosts API", description = "5.2 홈화면 광장 베스트 게시물 API")
    @ApiResponses({
            @ApiResponse(responseCode = "3030", description = "인기 게시물이 없어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<List<HomeBestTotalPostResponse>> getTotalBest() {
        try { //JWT로 해당 유저인지 확인 필요
            List<TotalPost> totalPosts = homeService.findTotalPostWithMemberLikeComment();
            List<HomeBestTotalPostResponse> result = totalPosts.stream()
                    .map(m -> new HomeBestTotalPostResponse(m))
                    .collect(Collectors.toList());

            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 5.3 홈 학교 베스트 게시판 API
     */
    @GetMapping("/univ/best")
    @Operation(summary = "5.3 getHomeUnivBest Posts API", description = "5.3 홈화면 잔디밭 베스트 게시물 리스트 API")
    @ApiResponses({
            @ApiResponse(responseCode = "3030", description = "인기 게시물이 없어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<List<HomeBestUnivPostResponse>> getUnivBest() {
        try {
            List<UnivPost> univPosts = homeService.findAllWithMemberLikeCommentCount();
            List<HomeBestUnivPostResponse> result = univPosts.stream()
                    .map(p -> new HomeBestUnivPostResponse(p))
                    .collect(Collectors.toList());
            return new BaseResponse<>(result);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    @GetMapping("/total/recent")
    @Operation(summary = "5.4 getTotalRecentPosts API", description = "5.4 홈화면 광장 최신 게시글 API")
    @ApiResponses({
            @ApiResponse(responseCode = "3029", description = "최근 올라온 게시글이 없어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<List<HomeRecentPostResponse>> getTotalRecentPosts() {
        try {
            List<TotalPost> totalPosts = homeService.findTotalRecentPosts();
            List<HomeRecentPostResponse> result = totalPosts.stream()
                    .map(m -> new HomeRecentPostResponse(m))
                    .collect(Collectors.toList());
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    @GetMapping("/univ/recent")
    @Operation(summary = "5.5 getUnivRecentPosts API", description = "5.5 홈화면 잔디밭 최신 게시글 API")
    @ApiResponses({
            @ApiResponse(responseCode = "3029", description = "최근 올라온 게시글이 없어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<List<HomeRecentPostResponse>> getUnivRecentPosts() {
        try {
            List<UnivPost> univPosts = homeService.findUnivRecentPosts();
            List<HomeRecentPostResponse> result = univPosts.stream()
                    .map(m -> new HomeRecentPostResponse(m))
                    .collect(Collectors.toList());
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
