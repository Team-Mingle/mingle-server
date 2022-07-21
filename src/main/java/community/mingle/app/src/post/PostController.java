package community.mingle.app.src.post;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.auth.authModel.GetUnivListResponse;
import community.mingle.app.src.domain.UnivName;
import community.mingle.app.src.post.model.GetUnivBestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

//    /**
//     * 2.3 학교 베스트 게시판 API
//     */
//    @GetMapping("/univ/best")
//    public BaseResponse<List<GetUnivBestResponse>> getUnivBest() {

        // JWT 에서 인덱스 추출
        //인덱스로 멤버 찾기
        //멤버의 univId 찾기
        //쿼리문: 동적쿼리? select * from Post fetch join Member,
        //Response: Post- title, content, createdAt, likeCount, commentCount,


//        try {
//            List<UnivName> findUnivNames = authService.findUniv();
//            List<GetUnivListResponse> result = findUnivNames.stream()
//                    .map(m -> new GetUnivListResponse(m))
//                    .collect(Collectors.toList());
//            return new BaseResponse<>(result);
//
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

}
