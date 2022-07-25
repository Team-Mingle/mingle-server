package community.mingle.app.src.post;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.post.model.GetTotalBestPostsResponse;
import community.mingle.app.utils.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import community.mingle.app.src.post.model.*;


import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "posts", description = "게시판/게시물관련 API")
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtService jwtService;
    private final PostRepository postRepository;

    /**
     * 2.1 광고 배너 API
     */


    /**
     * 2.2 홍콩 배스트 게시판 API
     */
    @Operation(summary = "2.2 getTotalBest Posts API", description = "2.2 광장 베스트 게시물 리스트 API")
//    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER)
    @GetMapping("/total/best")
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
     * 2.3 학교 베스트 게시판 API
     */
    @Operation(summary = "2.3 getUnivBest Posts API", description = "2.3 학교 베스트 게시물 리스트 API")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @GetMapping("/univ/best")
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

//     JWT 에서 인덱스 추출
//    인덱스로 멤버 찾기
//    멤버의 univId 찾기
//    쿼리문: 동적쿼리? select * from Post fetch join Member,
//    Response: Post- title, content, createdAt, likeCount, commentCount,


    /**
     * 2.4 광장 게시판 리스트 API
     */
    @Operation(summary = "2.4 getTotal Posts API", description = "2.4 광장 게시판 게시물 리스트 API")
    @GetMapping("/total/all")
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
 * 2.5 게시물 작성 API
 */


}