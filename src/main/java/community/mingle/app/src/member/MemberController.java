package community.mingle.app.src.member;


import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.config.BaseResponseStatus;
import community.mingle.app.src.auth.model.PostLoginResponse;
import community.mingle.app.src.domain.*;
import community.mingle.app.src.domain.Total.TotalNotification;
import community.mingle.app.src.domain.Univ.UnivNotification;
import community.mingle.app.src.item.model.ItemListResponse;
import community.mingle.app.src.item.model.ItemResponse;
import community.mingle.app.utils.JwtService;
import io.swagger.v3.oas.annotations.*;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.member.model.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static community.mingle.app.config.BaseResponseStatus.*;

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
    private final JwtService jwtService;



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
     * 2.2 내가 쓴 글 조회 - 통합 api +
     */
    @Operation(summary = "2.2 getMyTotalPosts API", description = "2.2 내가 쓴 전체 게시글 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "3034", description = "게시글이 없어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/posts/total")
    public BaseResponse<MyPagePostResponse> getMyTotalPosts (@RequestParam Long postId) {
        try {
            List<TotalPost> totalPosts = memberService.getTotalPosts(postId);
            Long memberIdByJwt = jwtService.getUserIdx();
            if (totalPosts.isEmpty()) {
                throw new BaseException(BaseResponseStatus.EMPTY_MYPOST_LIST);
            }
            List<MyPagePostDTO> result = totalPosts.stream()
                    .map(p -> new MyPagePostDTO(p, memberIdByJwt))
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
    @ApiResponses({
            @ApiResponse(responseCode = "3034", description = "게시글이 없어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/posts/univ")
    public BaseResponse<MyPagePostResponse> getMyUnivPosts (@RequestParam Long postId) {
        try {
            UnivName univ = memberService.findUniv();
            String univName = univ.getUnivName().substring(0,3);
            List<UnivPost> univPosts = memberService.getUnivPosts(postId);
            Long memberIdByJwt = jwtService.getUserIdx();
            if (univPosts.isEmpty()) {
                throw new BaseException(BaseResponseStatus.EMPTY_MYPOST_LIST);
            }
            List<MyPagePostDTO> result = univPosts.stream()
                    .map(p -> new MyPagePostDTO(p, memberIdByJwt))
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
    @ApiResponses({
            @ApiResponse(responseCode = "3034", description = "게시글이 없어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/comments/total")
    public BaseResponse<MyPagePostResponse> getTotalComments(@RequestParam Long postId) {
        try {
            List<TotalPost> totalComments = memberService.getTotalComments(postId);
            Long memberIdByJwt = jwtService.getUserIdx();
            if (totalComments.isEmpty()) {
                throw new BaseException(BaseResponseStatus.EMPTY_MYPOST_LIST);
            }
            List<MyPagePostDTO> result = totalComments.stream()
                    .map(p -> new MyPagePostDTO(p, memberIdByJwt))
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
    @ApiResponses({
            @ApiResponse(responseCode = "3034", description = "게시글이 없어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/comments/univ")
    public BaseResponse<MyPagePostResponse> getUnivComments(@RequestParam Long postId) {
        try {
            Long memberIdByJwt = jwtService.getUserIdx();
            UnivName univ = memberService.findUniv();
            String univName = univ.getUnivName().substring(0,3);
            List<UnivPost> univComments = memberService.getUnivComments(postId);
            if (univComments.isEmpty()) {
                throw new BaseException(BaseResponseStatus.EMPTY_MYPOST_LIST);
            }
            List<MyPagePostDTO> result = univComments.stream()
                    .map(p -> new MyPagePostDTO(p, memberIdByJwt))
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
    @ApiResponses({
            @ApiResponse(responseCode = "3034", description = "게시글이 없어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    @Operation(summary = "2.6 getMyTotalScraps API", description = "2.6 내가 스크랩 한 전체 게시글 API")
    public BaseResponse<MyPagePostResponse> getTotalScraps(@RequestParam Long postId) {
        try {
            Long memberIdByJwt = jwtService.getUserIdx();
            List<TotalPost> totalPosts = memberService.getTotalScraps(postId);
            if (totalPosts.isEmpty()) {
                throw new BaseException(BaseResponseStatus.EMPTY_MYPOST_LIST);
            }
            List<MyPagePostDTO> result = totalPosts.stream()
                    .map(post -> new MyPagePostDTO(post, memberIdByJwt))
                    .collect(Collectors.toList());
            MyPagePostResponse myPagePostResponse = new MyPagePostResponse(null, result);
            return new BaseResponse<>(myPagePostResponse);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 2.7 내가 스크랩 한 글 (대학) API
     */
    @GetMapping("/scraps/univ")
    @ApiResponses({
            @ApiResponse(responseCode = "3034", description = "게시글이 없어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    @Operation(summary = "2.7 getMyUnivScraps API", description = "2.7 내가 스크랩 한 학교 게시글 API")
    public BaseResponse<MyPagePostResponse> getUnivScraps(@RequestParam Long postId) {
        try {
            Long memberIdByJwt = jwtService.getUserIdx();
            UnivName univ = memberService.findUniv();
            String univName = univ.getUnivName().substring(0,3);
            List<UnivPost> univPosts = memberService.getUnivScraps(postId);
            if (univPosts.isEmpty()) {
                throw new BaseException(BaseResponseStatus.EMPTY_MYPOST_LIST);
            }
            List<MyPagePostDTO> result = univPosts.stream()
                    .map(post -> new MyPagePostDTO(post, memberIdByJwt))
                    .collect(Collectors.toList());
            MyPagePostResponse myPagePostResponse = new MyPagePostResponse(univName, result);
            return new BaseResponse<>(myPagePostResponse);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 2.8 내가 좋아요 한 글 (대학) API
     */
    @GetMapping("/likes/total")
    @ApiResponses({
            @ApiResponse(responseCode = "3034", description = "게시글이 없어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    @Operation(summary = "2.8 getMyTotalLikePosts API", description = "2.8 내가 좋아요한 전체 게시글 API")
    public BaseResponse<MyPagePostResponse> getTotalLikes(@RequestParam Long postId) {
        try {
            Long memberIdByJwt = jwtService.getUserIdx();
            List<TotalPost> totalPosts = memberService.getTotalLikes(postId);
            if (totalPosts.isEmpty()) {
                throw new BaseException(BaseResponseStatus.EMPTY_MYPOST_LIST);
            }
            List<MyPagePostDTO> result = totalPosts.stream()
                    .map(post -> new MyPagePostDTO(post, memberIdByJwt))
                    .collect(Collectors.toList());
            MyPagePostResponse myPagePostResponse = new MyPagePostResponse(null, result);
            return new BaseResponse<>(myPagePostResponse);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 2.9 내가 좋아요 한 글 (대학) API
     */
    @GetMapping("/likes/univ")
    @ApiResponses({
            @ApiResponse(responseCode = "3034", description = "게시글이 없어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    @Operation(summary = "2.9 getMyUnivLikePosts API", description = "2.9 내가 좋아요한 학교 게시글 API")
    public BaseResponse<MyPagePostResponse> getUnivLikes(@RequestParam Long postId) {
        try {
            Long memberIdByJwt = jwtService.getUserIdx();
            UnivName univ = memberService.findUniv();
            String univName = univ.getUnivName().substring(0,3);
            List<UnivPost> univPosts = memberService.getUnivLikes(postId);
            List<MyPagePostDTO> result = univPosts.stream()
                    .map(post -> new MyPagePostDTO(post, memberIdByJwt))
                    .collect(Collectors.toList());
            MyPagePostResponse myPagePostResponse = new MyPagePostResponse(univName, result);
            return new BaseResponse<>(myPagePostResponse);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 2.10 유저 탈퇴 API
     */
    @PatchMapping("/delete")
    @Operation(summary = "2.10  deleteMember API", description = "2.10 유저 탈퇴 API")
    @ApiResponses({
            @ApiResponse(responseCode = "2010", description = "이메일을 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2014", description = "비밀번호를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2020", description = "회원 정보를 찾을 수 없습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3011", description = "존재하지 않는 이메일이거나 비밀번호가 틀렸습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3016", description = "입력하신 정보가 사용자 정보와 맞지 않습니다..", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4011", description = "비밀번호 암호화에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4012", description = "이메일 암호화에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> deleteMember(@RequestBody DeleteMemberRequest deleteMemberRequest) {
        try {
            if (deleteMemberRequest.getEmail().isEmpty()) {
                return new BaseResponse<>(EMAIL_EMPTY_ERROR);
            }
            if (deleteMemberRequest.getPwd().isEmpty()) {
                return new BaseResponse<>(PASSWORD_EMPTY_ERROR);
            }
            memberService.deleteMember(deleteMemberRequest);
            return new BaseResponse<>("탈퇴에 성공했습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }



    /**
     * 2.11 신고 API
     */
    @PostMapping("/report")
    @Operation(summary = "2.11 createReport API", description = "2.11 신고 API")
    public BaseResponse<ReportDTO> createReport(@RequestBody ReportRequest reportRequest) {
        try {
            Member reportedMember = memberService.findReportedMember(reportRequest);
            ReportDTO reportDTO = memberService.createReport(reportRequest, reportedMember);
//            memberService.checkReportedMember(reportedMember);
//            memberService.checkReportedPost(reportRequest);
            return new BaseResponse<>(reportDTO);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
    * 2.12 알림 리스트 보여주기 API (new)
    **/
    @GetMapping("/notification")
    public BaseResponse<List<NotificationDTOResult>> getNotification() {
        try {
            List<NotificationDTO> notificationsSorted = memberService.get20NotificationsSorted();
            List<NotificationDTOResult> result = notificationsSorted.stream()
                    .map(n -> new NotificationDTOResult(n))
                    .collect(Collectors.toList());
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 2.12 알림 리스트 보여주기 API (Old)
     **/
//    @GetMapping("/notification")
//    @Operation(summary = " 2.12 getNotification API", description = " 2.12 알림창 리스트 API")
//    public BaseResponse<List<NotificationDTOResult>> getNotification() {
//        try {
//            List<TotalNotification> totalNotificationList = memberService.getTotalNotifications();
//            List<UnivNotification> univNotificationList = memberService.getUnivNotifications();
//            List<ReportNotification> reportNotificationList = memberService.getReportNotifications();
//            List<ItemNotification> itemNotificationList = memberService.getItemNotifications();
//
//            List<NotificationDTO> result_1 = totalNotificationList.stream()
//                    .map(t-> new NotificationDTO(t))
//                    .collect(Collectors.toList());
//
//            List<NotificationDTO> result_2 = univNotificationList.stream()
//                    .map(n-> new NotificationDTO(n))
//                    .collect(Collectors.toList());
//
//            List<NotificationDTO> result_3 = reportNotificationList.stream()
//                    .map(r-> new NotificationDTO(r))
//                    .collect(Collectors.toList());
//
//            List<NotificationDTO> result_4 = itemNotificationList.stream()
//                    .map(i -> new NotificationDTO(i))
//                    .collect(Collectors.toList());
//
//            List<NotificationDTO> result_5 = Stream.concat(result_1.stream(), result_2.stream())
//                    .collect(Collectors.toList());
//            List<NotificationDTO> result_6 = Stream.concat(result_3.stream(), result_4.stream())
//                    .collect(Collectors.toList());
//
//            List<NotificationDTO> final_result = Stream.concat(result_5.stream(), result_6.stream())
//                    .collect(Collectors.toList());
//
//            List<NotificationDTO> notifications = memberService.sortNotifications(final_result);
//            List<NotificationDTOResult> result = notifications.stream()
//                    .map(n-> new NotificationDTOResult(n))
//                    .collect(Collectors.toList());
//
//            return new BaseResponse<>(result);
//        } catch (BaseException exception) {
////            exception.printStackTrace();
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }


    /**
     * 2.13 알림 읽기 API
     **/
    @PatchMapping("/notification")
    @Operation(summary = " 2.13 readNotification API", description = " 2.13 알림 읽음 여부 API")
    public BaseResponse<String> readNotification(@RequestBody NotificationRequest notificationRequest) {
        try {
            memberService.readNotification(notificationRequest);
            String result = "알림을 확인하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 2.14 로그아웃 api
     */
    @Operation(summary = "2.14 logout api", description = "2.14 logout api")
    @PostMapping("logout")
    public BaseResponse<String> logout(){
        try {
            memberService.logout();
            return new BaseResponse<>("로그아웃에 성공하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 2.15 밍글소식 푸시알림 API
     */
    @PostMapping("/send-notification")
    public BaseResponse<String> sendPushNotificationToAll( @RequestBody SendPushNotificationRequest request) {
        try {
            memberService.sendPushNotificationToAll(request);
            return new BaseResponse<>("푸시알림 보내기 완료");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 2.16 내가 찜한 거래 게시물 조회 api
     */
    @Operation(summary = "2.16 내가 찜한 거래 게시물 (찜한내역) 조회 API", description = "2.16 getMyPageItemLikeList api")
    @ApiResponses({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(schema = @Schema(implementation = ItemListResponse.class))),
            @ApiResponse(responseCode = "3034", description = "게시글이 없어요.", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/items/like")
    public BaseResponse<ItemListResponse> getMyPageItemLikeList(@RequestParam Long itemId) {
        try {
            Long memberId = jwtService.getUserIdx();
            ItemListResponse myPageItemLikeResponse = memberService.findLikeItems(itemId, memberId);
            return new BaseResponse<>(myPageItemLikeResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 2.17 내가 쓴 거래 게시물 조회 API
     */
    @Operation(summary = "2.17 내가 쓴 거래 게시물 (판매내역) 조회 API", description = "2.17 getMyItemList api")
    @ApiResponses({
    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(schema = @Schema(implementation = ItemListResponse.class))),
    @ApiResponse(responseCode = "3034", description = "게시글이 없어요.", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/items")
    public BaseResponse<ItemListResponse> getMyItemList(@RequestParam Long itemId, @RequestParam String itemStatus) {
        try {
            Long memberId = jwtService.getUserIdx();
            ItemListResponse myItemListResponse = memberService.findMyItems(itemId, memberId, itemStatus);
            return new BaseResponse<>(myItemListResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
