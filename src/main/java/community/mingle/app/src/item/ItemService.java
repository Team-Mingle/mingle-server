package community.mingle.app.src.item;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponseStatus;
import community.mingle.app.src.domain.Item;
import community.mingle.app.src.domain.ItemImg;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.Univ.UnivPostImage;
import community.mingle.app.src.item.model.CreateItemRequest;
import community.mingle.app.src.item.model.ItemListDTO;
import community.mingle.app.src.item.model.ItemListResponse;
import community.mingle.app.src.post.PostRepository;
import community.mingle.app.src.post.model.CreatePostResponse;
import community.mingle.app.utils.JwtService;
import community.mingle.app.utils.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.app.config.BaseResponseStatus.*;
import static community.mingle.app.config.BaseResponseStatus.CREATE_FAIL_POST;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final JwtService jwtService;
    private final PostRepository postRepository;
    private final S3Service s3Service;

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

    @Transactional
    public String createItemPost(CreateItemRequest createItemRequest) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        Member member = postRepository.findMemberbyId(memberIdByJwt);
        if (member == null) throw new BaseException(USER_NOT_EXIST);

        try {
            Item item = Item.createItemPost(member, createItemRequest);
            Long id = itemRepository.save(item);
            List<String> fileNameList = null;

            if (createItemRequest.getMultipartFile() == null || createItemRequest.getMultipartFile().isEmpty()) {
                throw new BaseException(IMG_UPLOAD_REQUIRED);
            } else {
                try {
                    fileNameList = s3Service.uploadFile(createItemRequest.getMultipartFile(), "item");
                    for (String fileName : fileNameList) {
                        ItemImg itemImg = ItemImg.createItemImg(item, fileName);
                        itemRepository.saveImg(itemImg);
                    }
                } catch (Exception e) {
                    postRepository.deleteUnivPost(id);
                    throw new BaseException(UPLOAD_FAIL_IMAGE);
                }
            }
            return "중고거래 게시물 작성 성공";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(CREATE_FAIL_POST);
        }
    }
}