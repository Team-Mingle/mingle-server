package community.mingle.app.src.member;


import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.domain.Total.TotalNotification;
import community.mingle.app.src.domain.Univ.UnivNotification;
import community.mingle.app.src.domain.UnivName;
import io.swagger.v3.oas.annotations.*;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.member.model.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Tag(name = "member", description = "유저 관련 API")
@ApiResponses(value = {
        @ApiResponse(responseCode = "403", description = "토큰을 입력해주세요.(앞에 'Bearer ' 포함)./  토큰을 입력해주세요. / 잘못된 토큰입니다. / 토큰이 만료되었습니다.", content = @Content (schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content (schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
})
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    /**
     * 2.1 닉네임 수정 API
     */
    @PatchMapping("/nickname")
    @Operation(summary = "2.1 modifyNickname API", description = "2.1 닉네임 수정 API")
    @ApiResponses({
            @ApiResponse(responseCode = "2017", description = "중복된 닉네임입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2020", description = "닉네임 수정에 실패하였습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> modifyNickname(@RequestBody PatchNicknameRequest patchNicknameRequest) {
        try {
            memberService.modifyNickname(patchNicknameRequest.getNickname());
            return new BaseResponse<>("닉네임 변경에 성공하였습니다.");

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 2.2 내가 쓴 글 조회 - 통합 api
     */
    @Operation(summary = "2.2 getMyTotalPosts API", description = "2.2 내가 쓴 전체 게시글 조회 API")
    @GetMapping("/posts/total")
    public BaseResponse<MyPagePostResponse> getMyTotalPosts (@RequestParam Long postId) {
        try {
            List<TotalPost> totalPosts = memberService.getTotalPosts(postId);
            List<MyPagePostDTO> result = totalPosts.stream()
                    .map(p -> new MyPagePostDTO(p))
                    .collect(Collectors.toList());
            MyPagePostResponse myPagePostResponse = new MyPagePostResponse(null, result);
            return new BaseResponse<>(myPagePostResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 2.3 내가 쓴 글 조회 - 학교 api
     */
    @Operation(summary = "2.3 getMyUnivPosts API", description = "2.3 내가 쓴 학교 게시글 조회 API")
    @GetMapping("/posts/univ")
    public BaseResponse<MyPagePostResponse> getMyUnivPosts (@RequestParam Long postId) {
        try {
            UnivName univ = memberService.findUniv();
            String univName = univ.getUnivName().substring(0,3);
            List<UnivPost> univPosts = memberService.getUnivPosts(postId);
            List<MyPagePostDTO> result = univPosts.stream()
                    .map(p -> new MyPagePostDTO(p))
                    .collect(Collectors.toList());
            MyPagePostResponse myPagePostResponse = new MyPagePostResponse(univName, result);
            return new BaseResponse<>(myPagePostResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 2.4 내가 쓴 댓글 조회 - 전체 api
     */
    @Operation(summary = "2.4 getMyTotalComments API", description = "2.4 내가 쓴 전체 댓글 조회 API")
    @GetMapping("/comments/total")
    public BaseResponse<MyPagePostResponse> getTotalComments(@RequestParam Long postId) {
        try {
            List<TotalPost> totalComments = memberService.getTotalComments(postId);
            List<MyPagePostDTO> result = totalComments.stream()
                    .map(p -> new MyPagePostDTO(p))
                    .collect(Collectors.toList());
            MyPagePostResponse myPagePostResponse = new MyPagePostResponse(null, result);
            return new BaseResponse<>(myPagePostResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 2.5 내가 쓴 댓글 조회 - 학교 api
     */
    @Operation(summary = "2.5 getMyUnivComments API", description = "2.4 내가 쓴 학교 댓글 조회 API")
    @GetMapping("/comments/univ")
    public BaseResponse<MyPagePostResponse> getUnivComments(@RequestParam Long postId) {
        try {
            UnivName univ = memberService.findUniv();
            String univName = univ.getUnivName().substring(0,3);
            List<UnivPost> univComments = memberService.getUnivComments(postId);
            List<MyPagePostDTO> result = univComments.stream()
                    .map(p -> new MyPagePostDTO(p))
                    .collect(Collectors.toList());
            MyPagePostResponse myPagePostResponse = new MyPagePostResponse(univName, result);
            return new BaseResponse<>(myPagePostResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 2.6 내가 스크랩 한 글 (전체) API
     */
    @GetMapping("/scraps/total")
    @Operation(summary = "2.6 getMyTotalScraps API", description = "2.6 내가 스크랩 한 전체 게시글 API")
    public BaseResponse<MyPagePostResponse> getTotalScraps(@RequestParam Long postId) {
        try {
            List<TotalPost> totalPosts = memberService.getTotalScraps(postId);
            List<MyPagePostDTO> result = totalPosts.stream()
                    .map(post -> new MyPagePostDTO(post))
                    .collect(Collectors.toList());
            MyPagePostResponse myPagePostResponse = new MyPagePostResponse(null, result);
            return new BaseResponse<>(myPagePostResponse);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 2.7 내가 스크랩 한 글 (대학) API
     * 에러: 없을시 Validation
     * postId 받는거 수정하기
     */
    @GetMapping("/scraps/univ")
    @Operation(summary = "2.7 getMyUnivScraps API", description = "2.7 내가 스크랩 한 학교 게시글 API")
    public BaseResponse<MyPagePostResponse> getUnivScraps(@RequestParam Long postId) {
        try {
            UnivName univ = memberService.findUniv();
            String univName = univ.getUnivName().substring(0,3);
            List<UnivPost> univPosts = memberService.getUnivScraps(postId);
            List<MyPagePostDTO> result = univPosts.stream()
                    .map(post -> new MyPagePostDTO(post))
                    .collect(Collectors.toList());
            MyPagePostResponse myPagePostResponse = new MyPagePostResponse(univName, result);
            return new BaseResponse<>(myPagePostResponse);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 2.9 유저 삭제 API
     */
    @PatchMapping("/delete")
    @Operation(summary = "2.9  deleteMember API", description = "2.9 유저 삭제 API")
    @ApiResponse(responseCode = "2020", description = "회원 정보를 찾을 수 없습니다.", content = @Content (schema = @Schema(hidden = true)))
    public BaseResponse<String> deleteMember() {
        try {
            memberService.deleteMember();
            String result = "유저가 삭제되었습니다";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }


    /**
     * 2.10 신고 API
     */
    @PostMapping("/report")
    @Operation(summary = "2.10  createReport API", description = "2.10 신고 API")
    public BaseResponse<ReportDTO> createReport(@RequestBody ReportRequest reportRequest) {
        try {
            Member reportedMember = memberService.findReportedMember(reportRequest);
            ReportDTO reportDTO = memberService.createReport(reportRequest, reportedMember);
            memberService.checkReportedMember(reportedMember);
            memberService.checkReportedPost(reportRequest);
            return new BaseResponse<>(reportDTO);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 알림 리스트 보여주기 API
     **/
    @GetMapping("/notification")
    @Operation(summary = " getNotification API", description = " 알림창 리스트 API")
    public BaseResponse<List<NotificationDTOResult>> getNotification() {
        try {

            List<TotalNotification> totalNotificationList = memberService.getTotalNotifications();
            List<UnivNotification> univNotificationList = memberService.getUnivNotifications();

//            /**
//             * 수정
//             */
//            List<MyNotification> final_result = Stream.concat(result.stream(), result_2.stream())
//                    .collect(Collectors.toList());
//


            List<NotificationDTO> result_1 = totalNotificationList.stream()
                    .map(t-> new NotificationDTO(t))
                    .collect(Collectors.toList());

            List<NotificationDTO> result_2 = univNotificationList.stream()
                    .map(n-> new NotificationDTO(n))
                    .collect(Collectors.toList());

            List<NotificationDTO> final_result = Stream.concat(result_1.stream(), result_2.stream())
                    .collect(Collectors.toList());

            List<NotificationDTO> notifications = memberService.sortNotifications(final_result);
            List<NotificationDTOResult> result = notifications.stream()
                    .map(n-> new NotificationDTOResult(n))
                    .collect(Collectors.toList());

            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }




    /**
     * 알림 읽었을 때  API
     **/
    @PatchMapping("/notification/notificationId")
    @Operation(summary = " readNotification API", description = " 알림 읽음 여부 API")
    public BaseResponse<String> readNotification(@RequestBody NotificationRequest notificationRequest) {
        try {
            memberService.readNotification(notificationRequest);
            String result = "알림을 확인하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));

        }

    }




}
