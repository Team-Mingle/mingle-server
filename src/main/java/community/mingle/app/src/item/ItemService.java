package community.mingle.app.src.item;

import community.mingle.app.config.BaseException;
import community.mingle.app.src.comment.CommentRepository;
import community.mingle.app.src.domain.Currency;
import community.mingle.app.src.domain.*;
import community.mingle.app.src.firebase.FirebaseCloudMessageService;
import community.mingle.app.src.item.model.*;
import community.mingle.app.src.member.MemberRepository;
import community.mingle.app.src.post.PostRepository;
import community.mingle.app.src.post.PostService;
import community.mingle.app.src.post.model.CoCommentDTO;
import community.mingle.app.src.post.model.CommentResponse;
import community.mingle.app.utils.JwtService;
import community.mingle.app.utils.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static community.mingle.app.config.BaseResponseStatus.*;
import static community.mingle.app.src.domain.PostStatus.*;
import static community.mingle.app.utils.ValidationRegex.isRegexChatUrl;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final JwtService jwtService;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostService postService;
    private final S3Service s3Service;
    private final CommentRepository commentRepository;
    private final FirebaseCloudMessageService firebaseCloudMessageService;


    /**
     * 6.1 거래 게시판 리스트 조회 api
     */
    public ItemListResponse findItems(Long itemId, Long memberId) throws BaseException {
        Member member = memberRepository.findMember(memberId);
        List<Item> items = itemRepository.findItems(itemId, member);
        if (items.size() == 0) throw new BaseException(EMPTY_POSTS_LIST);
        List<ItemListDTO> itemListDTOList = items.stream()
                .map(item -> new ItemListDTO(item, memberId))
                .collect(Collectors.toList());
        return new ItemListResponse(itemListDTOList, "거래게시판");
    }

    /**
     * 6.2 거래 게시물 생성
     */
    @Transactional
    public CreateItemResponse createItemPost(CreateItemRequest createItemRequest) throws BaseException {
        if (!isRegexChatUrl(createItemRequest.getChatUrl())) {
            throw new BaseException(URL_FORMAT_ERROR);
        }
        Long memberIdByJwt = jwtService.getUserIdx();
        Member member = postRepository.findMemberbyId(memberIdByJwt);
        try {
            Item item = Item.createItemPost(member, createItemRequest);
            Long id = itemRepository.save(item);
            List<String> fileNameList;
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
                    itemRepository.deleteItem(id);
                    throw new BaseException(UPLOAD_FAIL_IMAGE);
                }
            }
            return new CreateItemResponse(id);
        } catch (Exception e) {
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
    public void updateView(Item item) {
        item.updateView();
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


    /**
     * 6.4 거래 게시물 수정 API
     */
    @Transactional
    public void modifyItemPost(Long itemId, ModifyItemPostRequest request) throws BaseException {
        Item item = checkMemberAndItemIsValidAndByAuthor(itemId);
        item.updateItemPost(request);
    }

    @Transactional
    public void modifyItemPostWithImage(Long itemId, ModifyItemPostRequest request) throws BaseException {
        Item item = checkMemberAndItemIsValidAndByAuthor(itemId);
        item.updateItemPost(request);
        List<ItemImg> itemImgList = item.getItemImgList();
        if ((request.getItemImageUrlsToDelete() == null || request.getItemImageUrlsToDelete().isEmpty()) && (request.getItemImagesToAdd() != null)) { // image to add only
            createItemImage(request, item);
        } else { //images to delete&add exists
//            //List<ItemImg> itemImgToDelete = imgUrlList.stream().filter(request.getItemImageUrlsToDelete()::contains).collect(Collectors.toList()); //ItemImg imgUrl 이랑 비교해야함..
            List<ItemImg> itemImgToDelete = itemImgList.stream().filter(itemImg -> request.getItemImageUrlsToDelete().contains(itemImg.getImgUrl())) //1. remove in ItemImg
                    .collect(Collectors.toList());
            for (ItemImg itemImg : itemImgToDelete) { //2. remove in s3
                itemImg.deleteItemImage();
                String imgUrl = itemImg.getImgUrl();
                String fileName = imgUrl.substring(imgUrl.lastIndexOf("/item/") + 6);
                s3Service.deleteFile(fileName, "item");
            }
            if (request.getItemImagesToAdd() != null) {
                createItemImage(request, item);//3. add if ItemImageUrlsToAdd exists
            }
        }
    }

    @Transactional
    void createItemImage(ModifyItemPostRequest request, Item item) throws BaseException {
        try {
            List<String> fileNameList = s3Service.uploadFile(request.getItemImagesToAdd(), "item");
            for (String fileName : fileNameList) {
                ItemImg itemImg = ItemImg.createItemImg(item, fileName);
                itemRepository.saveImg(itemImg);
            }
        } catch (Exception e) {
            throw new BaseException(UPLOAD_FAIL_IMAGE);
        }
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
                    String imgUrl = itemImg.getImgUrl();
                    String fileName = imgUrl.substring(imgUrl.lastIndexOf("/item/") + 6);
                    s3Service.deleteFile(fileName, "item");
                }
            }
            deleteItem.deleteItemPost();
        } catch (Exception e) {
            throw new BaseException(DELETE_FAIL_POST);
        }
    }


    /**
     * 6.6 판매 상태 변경 API
     */
    @Transactional
    public void modifyItemStatus(Long itemId, String itemStatus) throws BaseException {
        Item modifyItem = checkMemberAndItemIsValidAndByAuthor(itemId);
        modifyItem.modifyItemStatus(itemStatus);
    }


    /**
     * 6.7 거래 게시물 찜 api
     */
    @Transactional
    public String createItemLike(Long itemId) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        List<ItemLike> itemLike1 = itemRepository.findItemLike(itemId, memberIdByJwt);
        if (!itemRepository.findItemLike(itemId, memberIdByJwt).isEmpty()) {
            throw new BaseException(DUPLICATE_LIKE);
        }
        try {
            Member member = memberRepository.findMember(memberIdByJwt);
            Item item = itemRepository.findItemByIdAndMemberId(itemId, memberIdByJwt);
            if (item == null) throw new BaseException(POST_NOT_EXIST);
            ItemLike itemLike = ItemLike.likesItem(member, item);
            itemRepository.save(itemLike);
            return "중고거래 게시물 찜 성공";
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 6.8 거래 게시물 찜 취소 api
     */
    @Transactional
    public String itemUnlike(Long itemId) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        try {
            itemRepository.deleteItemLike(itemId, memberIdByJwt);
            return "중고거래 게시물 찜 삭제 성공";
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 6.9 거래 댓글 작성 api
     */
    @Transactional
    public PostItemCommentResponse createItemComment(PostItemCommentRequest postItemCommentRequest) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        Item item = itemRepository.findItemById(postItemCommentRequest.getItemId());
        ItemStatus status = item.getStatus();
        if (status.equals(ItemStatus.INACTIVE) || status.equals(ItemStatus.REPORTED) || status.equals(ItemStatus.NOTIFIED) || status.equals(ItemStatus.DELETED)) {
            throw new BaseException(REPORTED_DELETED_POST);
        }
        List<ItemComment> itemCommentList = item.getItemCommentList();
        boolean parentFlag = false;
        boolean mentionFlag = false;
        if (postItemCommentRequest.getMentionId() == null && postItemCommentRequest.getParentCommentId() == null) {
        } else {
            for (ItemComment itemComment : itemCommentList) {
                if (Objects.equals(itemComment.getId(), postItemCommentRequest.getParentCommentId()))
                    parentFlag = true;
                if (Objects.equals(itemComment.getId(), postItemCommentRequest.getMentionId()))
                    mentionFlag = true;
                if (parentFlag && mentionFlag)
                    break;
            }
            if (!parentFlag || !mentionFlag)
                throw new BaseException(FAILED_TO_CREATECOMMENT);
        }
        try {
            Member member = commentRepository.findMemberbyId(memberIdByJwt);
            Long anonymousId;
            if (Objects.equals(member.getId(), item.getMember().getId())) { //댓쓴이가 author 일때
                anonymousId = 0L; //isAnonymous = true, but AnonymousNo is 0
            } else if (postItemCommentRequest.isAnonymous()) {
                anonymousId = itemRepository.findItemAnonymousId(item, memberIdByJwt);
            } else {
                anonymousId = 0L; // null -> 0 으로 수정
            }
            ItemComment comment = ItemComment.createComment(item, member, postItemCommentRequest.getContent(), postItemCommentRequest.getParentCommentId(), postItemCommentRequest.getMentionId(), postItemCommentRequest.isAnonymous(), anonymousId);
            System.out.println(comment);
            itemRepository.saveItemComment(comment);
            sendItemPush(item, postItemCommentRequest, member, comment); //알림 저장 포함
            return new PostItemCommentResponse(anonymousId, comment, item.getMember().getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public void sendItemPush(Item item, PostItemCommentRequest postItemCommentRequest, Member creatorMember, ItemComment comment) throws IOException {
        Member itemMember = item.getMember();
        String messageTitle = "거래게시판";
        if (postItemCommentRequest.getParentCommentId() == null) {
            if (Objects.equals(itemMember.getId(), creatorMember.getId())) {
            } else {
                firebaseCloudMessageService.sendMessageTo(itemMember.getFcmToken(), messageTitle, "새로운 댓글이 달렸어요: " + postItemCommentRequest.getContent(), TableType.Item, item.getId());
                ItemNotification itemNotification = ItemNotification.saveItemNotification(item, itemMember, comment);
                itemRepository.saveItemNotification(itemNotification);
            }
        } else if (postItemCommentRequest.getParentCommentId() != null) {
            Member parentMember = itemRepository.findItemCommentById(postItemCommentRequest.getParentCommentId()).getMember();
            Member mentionMember;
            if (postItemCommentRequest.getMentionId() == null) {
                mentionMember = null;
            } else {
                mentionMember = itemRepository.findItemCommentById(postItemCommentRequest.getMentionId()).getMember();
            }
            Map<Member, String> map = new HashMap<>();
            map.put(itemMember, "postMemberId");
            map.put(parentMember, "parentMemberId");
            map.put(mentionMember, "mentionMemberId");
            map.put(creatorMember, "creatorMemberId");
            for (Member member : map.keySet()) {
                if (Objects.equals(map.get(member), "creatorMemberId")) {
                    continue;
                } else {
                    firebaseCloudMessageService.sendMessageTo(member.getFcmToken(), messageTitle, postItemCommentRequest.getContent(), TableType.Item, item.getId());
                    ItemNotification itemNotification = ItemNotification.saveItemNotification(item, member, comment);
                    itemRepository.saveItemNotification(itemNotification);
                }
            }
        }
    }


    /**
     * 6.10 거래 댓글 조회 api
     */
    public List<CommentResponse> getItemComments(Long itemId) throws BaseException {
        Item item = itemRepository.findItemById(itemId);
        if (item == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
        if (item.getStatus().equals(ItemStatus.REPORTED) || item.getStatus().equals(ItemStatus.DELETED)) {
            return new ArrayList<>();
        }
        Long memberIdByJwt = jwtService.getUserIdx();
        try {
            List<ItemComment> itemComments = itemRepository.findItemComments(itemId, memberIdByJwt); //댓글
            List<ItemComment> itemCoComments = itemRepository.findItemCoComments(itemId, memberIdByJwt); //대댓글
            List<CommentResponse> univCommentResponseList = new ArrayList<>();
            for (ItemComment c : itemComments) {
                List<ItemComment> CoCommentList = itemCoComments.stream()
                        .filter(cc -> c.getId().equals(cc.getParentCommentId()))
                        .collect(Collectors.toList());
                if ((c.getStatus() == PostStatus.INACTIVE) && CoCommentList.size() == 0) {
                    continue;
                }
                List<CoCommentDTO> coCommentDTO = CoCommentList.stream()
                        .filter(cc -> cc.getStatus().equals(PostStatus.ACTIVE) || cc.getStatus().equals(REPORTED) || cc.getStatus().equals(DELETED)) //11/25: 대댓글 삭제시 그냥 삭제. //2/20: 신고된 댓글 표시
                        .map(cc -> new CoCommentDTO(itemRepository.findItemCommentById(cc.getMentionId()), cc, memberIdByJwt, item.getMember().getId()))
                        .collect(Collectors.toList());
                CommentResponse univCommentResponse = new CommentResponse(c, coCommentDTO, memberIdByJwt, item.getMember().getId());
                univCommentResponseList.add(univCommentResponse);
            }
            return univCommentResponseList;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 6.11 거래 댓글 삭제 api
     */
    @Transactional
    public String deleteItemComment(Long itemCommentId) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        Member member = memberRepository.findMember(memberIdByJwt);
        if (member == null) {
            throw new BaseException(USER_NOT_EXIST);
        }
        ItemComment itemComment = itemRepository.findItemCommentById(itemCommentId);
        if (itemComment == null) {
            throw new BaseException(COMMENT_NOT_EXIST);
        }
        if (!Objects.equals(memberIdByJwt, itemComment.getMember().getId())) {
            throw new BaseException(MODIFY_NOT_AUTHORIZED);
        }
        try {
            itemComment.deleteItemComment();
            return "삭제에 성공했습니다.";
        } catch (Exception e) {
            throw new BaseException(DELETE_FAIL_COMMENT);
        }
    }


    private Item checkMemberAndItemIsValidAndByAuthor(Long itemId) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        postRepository.findMemberbyId(memberIdByJwt);
        Item item = itemRepository.findItemById(itemId);
        if (item == null)
            throw new BaseException(POST_NOT_EXIST);
        if (item.getStatus().equals(ItemStatus.REPORTED) || item.getStatus().equals(ItemStatus.DELETED) || item.getStatus().equals(ItemStatus.INACTIVE))
            throw new BaseException(REPORTED_DELETED_POST);
        if (!Objects.equals(memberIdByJwt, item.getMember().getId()))
            throw new BaseException(MODIFY_NOT_AUTHORIZED);
        return item;
    }

    public List<Item> findAllSearch(String keyword, Long memberId) throws BaseException {
        Member member = memberRepository.findMember(memberId);
        List<Item> searchItemLists = itemRepository.searchItemWithKeyword(keyword, member);
        if (searchItemLists.size() == 0) {
            throw new BaseException(POST_NOT_EXIST);
        }
        return searchItemLists;
    }

    @Transactional
    public String blindItem(Long itemId) throws BaseException {
        Long memberId = jwtService.getUserIdx();
        Item item = itemRepository.findItemById(itemId);
        if (item == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
        if (item.getStatus().equals(ItemStatus.INACTIVE) || item.getStatus().equals(ItemStatus.REPORTED) || item.getStatus().equals(ItemStatus.DELETED)) {
            throw new BaseException(REPORTED_DELETED_POST);
        }
        Member member = postRepository.findMemberbyId(memberId);
        ItemBlind itemBlind = ItemBlind.blindItem(item, member);
        if (itemBlind == null) {
            throw new BaseException(DUPLICATE_BLIND);
        } else {
            try {
                itemRepository.saveBlind(itemBlind);
                return "게시물을 가렸어요.";
            } catch (Exception e) {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

    @Transactional
    public String unblindItem(Long itemId) throws BaseException {
        Long memberId = jwtService.getUserIdx();
        try {
            itemRepository.deleteItemBlind(memberId, itemId);
            return "가리기를 취소했습니다.";
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 거래 댓글 좋아요 api
     */
    @Transactional
    public ItemCommentLikeResponse likeItemComment(Long commentId) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        ItemComment comment = itemRepository.findItemCommentById(commentId);
        if (comment == null) {
            throw new BaseException(COMMENT_NOT_EXIST);
        }
        if (comment.getStatus().equals(INACTIVE) || comment.getStatus().equals(REPORTED)) {
            throw new BaseException(REPORTED_DELETED_COMMENT);
        }
        Member member = memberRepository.findMember(memberIdByJwt);
        ItemCommentLike itemCommentLike = ItemCommentLike.likeItemComment(comment, member); //중복 좋아요일시 throw BaseException(DUPLICATE_LIKE)
        Long id = itemRepository.saveItemCommentLike(itemCommentLike);
        int likeCount = comment.getItemCommentLikes().size();
        return new ItemCommentLikeResponse(id, likeCount);
    }

    @Transactional
    public void unlikeItemComment(Long commentId) throws BaseException {
        Long userIdx = jwtService.getUserIdx();
        try {
            itemRepository.deleteLikeItem(commentId, userIdx);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<Currency> getCurrencyList(Long memberId) {
        Member member = memberRepository.findMember(memberId);
        Country country = member.getUniv().getCountry();
        return Arrays.stream(Currency.values()).filter(it -> it.getCountires().contains(country.getCountryName())).collect(Collectors.toList());
    }
}
