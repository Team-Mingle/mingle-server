package community.mingle.app.src.item;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.config.BaseResponseStatus;
import community.mingle.app.src.comment.model.PostCommentLikesTotalResponse;
import community.mingle.app.src.domain.Item;
import community.mingle.app.src.item.model.*;
import community.mingle.app.src.post.model.*;
import community.mingle.app.utils.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.app.config.BaseResponseStatus.URL_FORMAT_ERROR;
import static community.mingle.app.utils.ValidationRegex.isRegexChatUrl;

@Tag(name = "item", description = "중고거래 API")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@ApiResponses(value = {
        @ApiResponse(responseCode = "403", description = "토큰을 입력해주세요.(앞에 'Bearer ' 포함)./  토큰을 입력해주세요. / 잘못된 토큰입니다. / 토큰이 만료되었습니다.", content = @Content(schema = @Schema(hidden = true))),
//        @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content(schema = @Schema(hidden = true)))
})
@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final JwtService jwtService;

    /**
     * 6.1 거래 게시판 리스트 조회 api
     */
    @GetMapping("/list")
    @Operation(summary = "6.1 getItemList API", description = "6.1 거래 게시판 리스트 조회 API")
    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(schema = @Schema(implementation = ItemListResponse.class)))
    @ApiResponse(responseCode = "3032", description = "해당 카테고리에 게시물이 없습니다.", content = @Content(schema = @Schema(hidden = true)))
    public BaseResponse<ItemListResponse> getItemList(@RequestParam Long itemId) {
        try {
            Long memberId = jwtService.getUserIdx();
            ItemListResponse itemListResponse = itemService.findItems(itemId, memberId);
            return new BaseResponse<>(itemListResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 6.2 거래 게시판 글 작성 api
     */
    @PostMapping("")
    @Operation(summary = "6.2 createItemPost API", description = "6.2 거래 게시판 글 작성 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(schema = @Schema(implementation = CreateItemResponse.class))),
            @ApiResponse(responseCode = "3033", description = "게시물 생성에 실패하였습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3070", description = "이미지 업로드에 실패했습니다,", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3075", description = "최소 1개 이상의 물건 사진을 올려주세요.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<CreateItemResponse> createItemPost(@ModelAttribute CreateItemRequest createItemRequest) {
        try {
            return new BaseResponse<>(itemService.createItemPost(createItemRequest));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 6.3 거래 게시판 글 상세 api
     */
    @GetMapping("{itemId}")
    @Operation(summary = "6.3 getItemPostDetail API", description = "6.3 거래 게시판 글 상세 API")
    @ApiResponses({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(schema = @Schema(implementation = ItemResponse.class))),
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3036", description = "삭제되거나 신고된 게시물 입니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<ItemResponse> getItemPostDetail(@PathVariable Long itemId) {
        try {
            Item item = itemService.getItem(itemId);
            itemService.updateView(item);
            ItemResponse itemPostDetailResponse = itemService.getItemPostDetail(item);
            return new BaseResponse<>(itemPostDetailResponse);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 6.4 거래 게시물 수정 api
     */
    @PatchMapping("{itemId}")
    @Operation(summary = "6.4 modifyItemPost API", description = "6.4 거래 게시물 수정 API")
    @ApiResponses({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "2004", description = "필수 항목을 입력해주세요.", content = @Content(schema = @Schema(hidden = true)))
    })
    public BaseResponse<String> modifyItemPost(@PathVariable Long itemId, @ModelAttribute ModifyItemPostRequest request) throws BaseException {
        if (request.getTitle() == null || request.getContent() == null || request.getPrice() == null || request.getChatUrl() == null || request.getLocation() == null)
            return new BaseResponse<>(BaseResponseStatus.FIELD_EMPTY_ERROR);
        if (!isRegexChatUrl(request.getChatUrl())) {
            throw new BaseException(URL_FORMAT_ERROR);
        }
        if (request.getItemImagesToAdd() == null && request.getItemImageUrlsToDelete() == null) {
            try {
                itemService.modifyItemPost(itemId, request);
                return new BaseResponse<>("거래 게시물 수정 성공");
            } catch (BaseException e) {
                return new BaseResponse<>(e.getStatus());
            }
        } else { //사진 같이 수정하는거
            try {
                itemService.modifyItemPostWithImage(itemId, request);
                return new BaseResponse<>("거래 게시물 /사진 수정 성공");
            } catch (BaseException e) {
                return new BaseResponse<>(e.getStatus());
            }
        }
    }

    /**
     * 6.5 거래 게시물 삭제 API
     */
    @PatchMapping("/status/{itemId}")
    @Operation(summary = "6.5 deleteItemPost API", description = "6.5 거래 게시물 삭제 API")
    @ApiResponses({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "3025", description = "게시물 삭제를 실패했습니다.", content = @Content(schema = @Schema(hidden = true)))
    })
    public BaseResponse<String> deleteItemPost(@PathVariable Long itemId) {
        try {
            itemService.deleteItemPost(itemId);
            return new BaseResponse<>("거래 게시물 삭제 성공");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 6.6 판매 상태 변경 API
     */
    @PatchMapping("/item-status/{itemId}")
    @Operation(summary = "6.6 modifyItemStatus API", description = "6.6 판매 상태 변경 API")
    @ApiResponses({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "2060", description = "유효하지 않은 itemStatus 입니다.", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3036", description = "삭제되거나 신고된 게시물 입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3040", description = "게시물 수정 권한이 없습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> modifyItemStatus(@PathVariable Long itemId, @RequestParam String itemStatus) {
        try {
            itemService.modifyItemStatus(itemId, itemStatus);
            return new BaseResponse<>("판매 상태 변경 완료");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 6.7 거래 게시물 찜 api
     */
    @PostMapping("/like")
    @Operation(summary = "6.7 createItemLike API", description = "6.7 거래 게시물 찜")
    @ApiResponses({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3060", description = "이미 좋아요를 눌렀어요.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> createItemLike(@RequestParam Long itemId) {
        try {
            return new BaseResponse<>(itemService.createItemLike(itemId));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 6.8 거래 게시물 찜 취소 api
     */
    @DeleteMapping("/unlike")
    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(schema = @Schema(implementation = String.class)))
    @Operation(summary = "6.8 itemUnlike API", description = "6.8 거래 게시물 찜 취소")
    public BaseResponse<String> itemUnlike(@RequestParam Long itemId) {
        try {
            return new BaseResponse<>(itemService.itemUnlike(itemId));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 6.9 거래 댓글 작성 api
     */
    @PostMapping("/comment")
    @Operation(summary = "6.9 comment post API", description = "6.9 거래 댓글 작성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(schema = @Schema(implementation = PostItemCommentResponse.class))),
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4040", description = "잘못된 parentCommentId / mentionId 입니다.", content = @Content(schema = @Schema(hidden = true)))
    })
    public BaseResponse<PostItemCommentResponse> createItemComment(@RequestBody @Valid PostItemCommentRequest postItemCommentRequest) throws BaseException {
        try {
            PostItemCommentResponse result = itemService.createItemComment(postItemCommentRequest);
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 6.10 거래 댓글 조회 api
     */
    @GetMapping("/comment/{itemId}")
    @Operation(summary = "6.10 comment get API", description = "6.10 거래 댓글 조회")
    @ApiResponses({
//            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(schema = @Schema(implementation = CommentResponse.class), array = @ArraySchema())),
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentResponse.class)))),
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<List<CommentResponse>> itemComment(@PathVariable Long itemId) {
        try {
            List<CommentResponse> itemCommentResponseList = itemService.getItemComments(itemId);
            return new BaseResponse<>(itemCommentResponseList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 6.11 거래 댓글 삭제 api
     */
    @PatchMapping("comment/{itemCommentId}")
    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(schema = @Schema(implementation = String.class)))
    @Operation(summary = "6.11 comment delete API", description = "6.11 거래 댓글 삭제")
    public BaseResponse<String> deleteItemComment(@PathVariable Long itemCommentId) {
        try {
            String deleteItemComment = itemService.deleteItemComment(itemCommentId);
            return new BaseResponse<>(deleteItemComment);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 6.12 거래 검색 api
     */
    @Operation(summary = "6.12 search item API", description = "중고거래 게시판 검색")
    @GetMapping("search")
    public BaseResponse<ItemListResponse> searchItem(@RequestParam(value = "keyword") String keyword) {
        try {
            Long memberId = jwtService.getUserIdx();
            List<Item> items = itemService.findAllSearch(keyword, memberId);
            List<ItemListDTO> result = items.stream()
                    .map(i -> new ItemListDTO(i, memberId))
                    .collect(Collectors.toList());
            ItemListResponse searchItemResponse = new ItemListResponse(result, "거래 게시판");
            return new BaseResponse<>(searchItemResponse);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 6.13 거래 가리기 api
     */
    @Operation(summary = "6.13 blindItem API", description = "6.13 중고장터 게시물 가리기 api")
    @ApiResponses({
            @ApiResponse(responseCode = "3025", description = "게시물 삭제를 실패했습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3036", description = "삭제되거나 신고된 게시물 입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3060", description = "이미 게시물을 가렸어요.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
    @PostMapping("/blind")
    public BaseResponse<String> blindItem(@RequestParam Long itemId) {
        try {
            return new BaseResponse<>(itemService.blindItem(itemId));
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 6.14 거래 가리기 취소 api
     */
    @Operation(summary = "6.14 unBlindItem API", description = "6.14 중고장터 게시물 가리기 취소 api")
    @ApiResponses({
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
    @DeleteMapping("deleteblind")
    public BaseResponse<String> unblindItem(@RequestParam Long itemId) {
        try {
            return new BaseResponse<>(itemService.unblindItem(itemId));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 6.15 거래 게시물 댓글 좋아요 api
     */
    @Operation(summary = "6.15 likeItemComment API", description =  "6.15 중고장터 게시물 댓글 좋아요 API")
    @ApiResponses ({
            @ApiResponse(responseCode = "4035", description = "댓글이 존재하지 않습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4050", description = "삭제되거나 신고된 댓글 입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3060", description = "이미 좋아요를 눌렀어요.", content = @Content (schema = @Schema(hidden = true))),
    })
    @PostMapping("comment/like")
    public BaseResponse<ItemCommentLikeResponse> likeItemComment(@RequestParam Long commentId) {
        try {
            return new BaseResponse<>(itemService.likeItemComment(commentId));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 6.16 거래 게시물 댓글 좋아요 취소 api
     */
    @Operation(summary = "6.16 unlikeItemComment API", description =  "6.16 중고장터 게시물 댓글 좋아요 취소 API")
    @DeleteMapping("comment/like/unlike")
    public BaseResponse<String> unlikeItemComment (@RequestParam Long commentId) throws BaseException {
        try {
            itemService.unlikeItemComment(commentId);
            String result = "좋아요가 취소되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
