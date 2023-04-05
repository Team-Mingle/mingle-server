package community.mingle.app.src.item;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.domain.Item;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.item.model.ItemListResponse;
import community.mingle.app.src.post.model.PostListDTO;
import community.mingle.app.src.post.model.PostListResponse;
import community.mingle.app.utils.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public BaseResponse<ItemListResponse> getItemList (@RequestParam Long itemId) {
        try {
            Long memberId = jwtService.getUserIdx();
            ItemListResponse itemListResponse = itemService.findItems(itemId, memberId);
            return new BaseResponse<>(itemListResponse);
        } catch (BaseException e) {
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }
    }

}
