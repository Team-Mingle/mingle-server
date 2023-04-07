package community.mingle.app.src.item;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.config.BaseResponseStatus;
import community.mingle.app.src.domain.Item;
import community.mingle.app.src.item.model.CreateItemRequest;
import community.mingle.app.src.item.model.ItemListResponse;
import community.mingle.app.src.item.model.ItemResponse;
import community.mingle.app.src.item.model.ModifyItemPostRequest;
import community.mingle.app.utils.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "item", description = "중고거래 API")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@ApiResponses(value = {
        @ApiResponse(responseCode = "403", description = "토큰을 입력해주세요.(앞에 'Bearer ' 포함)./  토큰을 입력해주세요. / 잘못된 토큰입니다. / 토큰이 만료되었습니다.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(schema = @Schema(hidden = true))),
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
    public BaseResponse<String> createItemPost(@ModelAttribute CreateItemRequest createItemRequest) {
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
     * 사진 수정 논의 필요
     */
    @PatchMapping("{itemId}")
    @Operation(summary = "6.4 modifyItemPost API", description = "6.4 거래 게시물 수정 API")
    public BaseResponse<String> modifyItemPost(@PathVariable Long itemId, @RequestBody ModifyItemPostRequest request) {
        if (request.getTitle() == null || request.getContent() == null || request.getPrice() == null || request.getChatUrl() == null || request.getLocation() == null)
            return new BaseResponse<>(BaseResponseStatus.FIELD_EMPTY_ERROR);
        try {
            itemService.modifyItemPost(itemId, request);
            return new BaseResponse<>("거래 게시물 수정 성공");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 6.5 거래 게시물 삭제 API
     */
    @PatchMapping("/status/{itemId}")
    @Operation(summary = "6.5 deleteItemPost API", description = "6.5 거래 게시물 삭제 API")
    public BaseResponse<String> deleteItemPost(@PathVariable Long itemId) {
        try {
            itemService.deleteItemPost(itemId);
            return new BaseResponse<>("거래 게시물 삭제 성공");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

}
