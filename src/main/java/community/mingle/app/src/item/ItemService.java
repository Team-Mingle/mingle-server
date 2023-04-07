package community.mingle.app.src.item;

import community.mingle.app.config.BaseException;
import community.mingle.app.src.domain.*;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPostImage;
import community.mingle.app.src.item.model.*;
import community.mingle.app.src.post.PostRepository;
import community.mingle.app.src.post.PostService;
import community.mingle.app.utils.JwtService;
import community.mingle.app.utils.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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

    private final PostService postService;
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

    /**
     * 6.3 거래 게시판 글 상세 api
     */
    public Item getItem(Long itemId) throws BaseException {
        Item item = itemRepository.findItemById(itemId);
        if (item == null)
            throw new BaseException(POST_NOT_EXIST);
        return item;
    }
    @Transactional
    public ItemResponse getItemPostDetail(Item item) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();  // jwtService 의 메소드 안에서 throw 해줌 -> controller 로 넘어감
        boolean isMyPost = false, isLiked = false, isScraped = false, isBlinded = false;
        Long itemId = item.getId();
        try {
            if (Objects.equals(item.getMember().getId(), memberIdByJwt))
                isMyPost = true;
            if (itemRepository.checkItemIsLiked(itemId, memberIdByJwt))
                isLiked = true;
            if (itemRepository.checkItemIsBlinded(itemId, memberIdByJwt))
                isBlinded = true;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

        /*** 신고된 게시물 처리 */
        ItemResponse itemResponse;
        if (item.getStatus().equals(ItemStatus.REPORTED)) {
            String reportedReason = postService.findReportedPostReason(item.getId(), TableType.Item);
            itemResponse = new ItemResponse(item, isMyPost, isLiked, isBlinded, reportedReason);
        } else if (item.getStatus().equals(ItemStatus.DELETED)) {
            itemResponse = new ItemResponse(item, isMyPost, isLiked, isBlinded, "");
        } else { //정상 게시물
            itemResponse = new ItemResponse(item, isMyPost, isLiked, isBlinded);
        }
        return itemResponse;
    }
    @Transactional
    public void updateView(Item item) {
        item.updateView();
    }


    /**
     * 6.4 거래 게시물 수정 API
     */
    @Transactional
    public void modifyItemPost(Long itemId, ModifyItemPostRequest request) throws BaseException {
        Item item = checkMemberAndItemIsValidAndByAuthor(itemId);
        item.updateItemPost(request);
    }


    /**
     * 6.5 거래 게시물 삭제 API
     */
    @Transactional
    public void deleteItemPost(Long itemId) throws BaseException {
        Item deleteItem = checkMemberAndItemIsValidAndByAuthor(itemId);
        try {
            List<ItemComment> itemCommentList = deleteItem.getItemCommentList();
            if (itemCommentList != null) {
                for (ItemComment itemComment : itemCommentList) {
                    itemComment.deleteItemComment();
                }
            }
            List<ItemImg> itemImgList = deleteItem.getItemImgList();
            if (itemImgList != null) {
                for (ItemImg itemImg : itemImgList) {
                    itemImg.deleteItemImage();
                    /** s3 사진 삭제 추가 **/
                    //String imgUrl = itemImg.getImgUrl();
                    //String fileName = imgUrl.substring(imgUrl.lastIndexOf(".com/item/") + 11);
                    //s3Service.deleteFile(fileName, "item");
                }
            }
            deleteItem.deleteItemPost();
        } catch (Exception e) {
            throw new BaseException(DELETE_FAIL_POST);
        }
    }




    private Item checkMemberAndItemIsValidAndByAuthor(Long itemId) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        postRepository.findMemberbyId(memberIdByJwt);
        Item item = itemRepository.findItemById(itemId);
        if (item == null)
            throw new BaseException(POST_NOT_EXIST);
        if (item.getStatus().equals(ItemStatus.REPORTED) || item.getStatus().equals(ItemStatus.DELETED))
            throw new BaseException(REPORTED_DELETED_POST);
        if (!Objects.equals(memberIdByJwt, item.getMember().getId()))
            throw new BaseException(MODIFY_NOT_AUTHORIZED);
        return item;
    }
}
