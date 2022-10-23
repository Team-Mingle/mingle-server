package community.mingle.app.src.home;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.home.model.HomeBestTotalPostResponse;
import community.mingle.app.src.home.model.HomeBestUnivPostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    /**
     * 5.1 홈 전체 배스트 게시판 API
     */
    @GetMapping("/total/best")
    @Operation(summary = "3.2 getTotalBest Posts API", description = "3.2 광장 베스트 게시물 리스트 API")
    @ApiResponses({
            @ApiResponse(responseCode = "3030", description = "최근 3일간 올라온 베스트 게시물이 없습니다.", content = @Content(schema = @Schema(hidden = true))),
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
     * 5.2 홈 학교 베스트 게시판 API
     */
    @GetMapping("/univ/best")
    @Operation(summary = "3.3 getUnivBest Posts API", description = "3.3 학교 베스트 게시물 리스트 API")
    @ApiResponses ({
            @ApiResponse(responseCode = "3030", description = "최근 3일간 올라온 베스트 게시물이 없습니다.", content = @Content (schema = @Schema(hidden = true))),
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

}
