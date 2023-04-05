package community.mingle.app.src.item;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponseStatus;
import community.mingle.app.src.domain.Item;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.item.model.ItemListDTO;
import community.mingle.app.src.item.model.ItemListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.app.config.BaseResponseStatus.EMPTY_POSTS_LIST;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    /**
     * 6.1 거래 게시판 리스트 조회 api
     */
    public ItemListResponse findItems(Long itemId, Long memberId) throws BaseException {
        List<Item> items = itemRepository.findItems(itemId, memberId);
        if (items.size() == 0) throw new BaseException(EMPTY_POSTS_LIST);
        List<ItemListDTO> itemListDTOList = items.stream()
                .map(item -> new ItemListDTO(item))
                .collect(Collectors.toList());
        return new ItemListResponse(itemListDTOList);
    }
}
