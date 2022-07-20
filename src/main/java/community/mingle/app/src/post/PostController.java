package community.mingle.app.src.post;


import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.auth.AuthController;
import community.mingle.app.src.auth.authModel.PatchUpdatePwdRequest;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.post.postModel.GetTotalBestPostsResponse;
import community.mingle.app.utils.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static community.mingle.app.config.BaseResponseStatus.*;
import static community.mingle.app.utils.ValidationRegex.isRegexPassword;

@Tag(name = "posts", description = "게시판/게시물관련 API")
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtService jwtService;

    /**
     * 2.1 광고 배너 API
     */


    /**
     * 2.2 홍콩 배스트 게시판 API
     */

//    @GetMapping("totalbests")
//    public BaseResponse<List<GetTotalBestPostsResponse>> totalBests() {
//        try { //JWT로 해당 유저인지 확인 필요
//
//            Member member = postService.totalBests();
//
//            return new BaseResponse<>(result);
//
//        } catch (BaseException exception) {
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }


    /**
     * 2.3 학교 베스트 게시판 API
     */


    /**
     * 2.4 광장 게시판 리스트 API
     */


    /**
     * 2.5 게시물 작성 API
     */
}
