package community.mingle.app.src.item;

import community.mingle.app.config.BaseException;
import community.mingle.app.src.comment.CommentRepository;
import community.mingle.app.src.domain.*;
import community.mingle.app.src.firebase.FirebaseCloudMessageService;
import community.mingle.app.src.item.model.*;
import community.mingle.app.src.member.MemberRepository;
import community.mingle.app.src.post.PostRepository;
import community.mingle.app.src.post.model.CoCommentDTO;
import community.mingle.app.src.post.model.CommentResponse;
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

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static community.mingle.app.config.BaseResponseStatus.*;
import static community.mingle.app.config.BaseResponseStatus.CREATE_FAIL_POST;
import static community.mingle.app.src.domain.PostStatus.DELETED;
import static community.mingle.app.src.domain.PostStatus.REPORTED;

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

    private final FirebaseCloudMessageService fcmService;


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

    @Transactional
    public String createItemLike(Long itemId) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        if (itemRepository.findItemLike(itemId, memberIdByJwt) == null) {
            throw new BaseException(DUPLICATE_LIKE);
        }
        try {
            Member member = memberRepository.findMember(memberIdByJwt);
            Item item = itemRepository.findItemByIdAndMemberId(itemId, memberIdByJwt);
            if (item == null) {
                throw new BaseException(POST_NOT_EXIST);
            }
            ItemLike itemLike = ItemLike.likesItem(member, item);
            itemRepository.save(itemLike);
            return "중고거래 게시물 찜 성공";
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

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

    @Transactional
    public PostItemCommentResponse createItemComment(PostItemCommentRequest postItemCommentRequest) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();


        Item item = itemRepository.findItemById(postItemCommentRequest.getItemId());
        if (item == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
        /*
        1. 댓글을 달때: parent = null, mention = null.
        2. 처음 대댓글 달때 (b) : parent = a , mention = a.
        3. 대댓글에 대댓글 달때 (c) : parent = a, mention = b.

        에러 날 케이스
        1. 대댓글 달때: parent = null, mention = a.
        2. 대댓글 달때: parent = a, mention = null.
        3. 대댓글 달때: parent = 없는 id, mention = 없는 id.
        4. 대댓글 달떄: parent나 mention이 이게시물에 달린 댓글이 아닐때.

        -> 앱으로 통하는 통신만 가능하도록..?
        -> 여태껏 디비에 잘못들어간 에러들은 다 핸들링 해야할수도
        
         */

        // 잘못된 parentComment / mention Id
        List<ItemComment> itemCommentList = item.getItemCommentList();
        boolean parentFlag = false;
        boolean mentionFlag = false;

        if (postItemCommentRequest.getMentionId() == null && postItemCommentRequest.getParentCommentId() == null) {
        }
        else {
            for (ItemComment itemComment : itemCommentList) {
                if (Objects.equals(itemComment.getId(), postItemCommentRequest.getParentCommentId())) {
                    parentFlag = true;
                }
                if (Objects.equals(itemComment.getId(), postItemCommentRequest.getMentionId())) {
                    mentionFlag = true;
                }
                if (parentFlag == true && mentionFlag == true) {
                    break;
                }
            }
            if (parentFlag == false || mentionFlag == false) {
                throw new BaseException(FAILED_TO_CREATECOMMENT);
            }
        }


        try {
            Member member = commentRepository.findMemberbyId(memberIdByJwt);
            Long anonymousId;
            if (Objects.equals(member.getId(), item.getMember().getId())) { //댓쓴이가 author 일때
                anonymousId = Long.valueOf(0); //isAnonymous = true, but AnonymousNo is 0
            } else if (postItemCommentRequest.isAnonymous() == true) {
                anonymousId = itemRepository.findItemAnonymousId(item, memberIdByJwt);
            } else {
                anonymousId = Long.valueOf(0); // null -> 0 으로 수정
            }

            //댓글 생성
            ItemComment comment = ItemComment.createComment(item, member, postItemCommentRequest.getContent(), postItemCommentRequest.getParentCommentId(), postItemCommentRequest.getMentionId(), postItemCommentRequest.isAnonymous(), anonymousId);
            System.out.println(comment);
            itemRepository.saveItemComment(comment);
            sendItemPush(item, postItemCommentRequest, member, comment); //알림 저장 포함
            PostItemCommentResponse postItemCommentResponse = new PostItemCommentResponse(anonymousId, comment, item.getMember().getId());
            return postItemCommentResponse;

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
                return;
            }
            else {
                //이게 방금 살린거
                firebaseCloudMessageService.sendMessageTo(itemMember.getFcmToken(), messageTitle, "새로운 댓글이 달렸어요: " + postItemCommentRequest.getContent(), TableType.Item, item.getId());
                //알림 저장
                ItemNotification itemNotification = ItemNotification.saveItemNotification(item, itemMember,comment);
                itemRepository.saveItemNotification(itemNotification);
                if (itemMember.getTotalNotifications().size() +itemMember.getUnivNotifications().size()+itemMember.getItemNotifications().size()> 20) {
                    itemRepository.deleteItemNotification(itemMember.getItemNotifications().get(0).getId());
                }
            }
        } else if (postItemCommentRequest.getParentCommentId()!= null) {
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
                }else{
                    firebaseCloudMessageService.sendMessageTo(member.getFcmToken(), messageTitle, postItemCommentRequest.getContent(), TableType.Item, item.getId());
                    //알림 저장
                    ItemNotification itemNotification = ItemNotification.saveItemNotification(item, member,comment);
                    itemRepository.saveItemNotification(itemNotification);
                    if (itemMember.getTotalNotifications().size() +itemMember.getUnivNotifications().size()+itemMember.getItemNotifications().size()> 20) {
                        itemRepository.deleteItemNotification(itemMember.getItemNotifications().get(0).getId());
                    }
                }
            }
        }

    }

    public List<CommentResponse> getItemComments(Long itemId) throws BaseException {
        Item item = itemRepository.findItemById(itemId);
        if (item == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
        if (item.getStatus().equals(ItemStatus.REPORTED) || item.getStatus().equals(ItemStatus.DELETED)) {
            return new ArrayList<>();
        }
        Long memberIdByJwt = jwtService.getUserIdx();  // jwtService 의 메소드 안에서 throw 해줌 -> controller 로 넘어감
        try {
            //1. postId 의 댓글, 대댓글 리스트 각각 가져오기
            List<ItemComment> itemComments = itemRepository.findItemComments(itemId, memberIdByJwt); //댓글
            List<ItemComment> itemCoComments = itemRepository.findItemCoComments(itemId, memberIdByJwt); //대댓글
            //2. 댓글 + 대댓글 DTO 생성
            List<CommentResponse> univCommentResponseList = new ArrayList<>();
            //3. 댓글 리스트 돌면서 댓글 하나당 대댓글 리스트 넣어서 합쳐주기
            for (ItemComment c : itemComments) {
                //parentComment 하나당 해당하는 ItemComment 타입의 대댓글 찾아서 리스트 만들기
                List<ItemComment> CoCommentList = itemCoComments.stream()
                        .filter(cc -> c.getId().equals(cc.getParentCommentId()))
//                        .filter(cc -> cc.getStatus().equals(PostStatus.ACTIVE)) // 더 추가: 대댓글 active 인거만 가져오기
                        .collect(Collectors.toList());

                //11/25 추가: 삭제된 댓글 표시 안하기 - 대댓글 없는 댓글 그냥 삭제
                if ((c.getStatus() == PostStatus.INACTIVE ) && CoCommentList.size() == 0) {
                    continue;
                }

                //댓글 하나당 만들어진 대댓글 리스트를 대댓글 DTO 형태로 변환
                List<CoCommentDTO> coCommentDTO = CoCommentList.stream()
                        .filter(cc -> cc.getStatus().equals(PostStatus.ACTIVE) || cc.getStatus().equals(REPORTED) || cc.getStatus().equals(DELETED)) //11/25: 대댓글 삭제시 그냥 삭제. //2/20: 신고된 댓글 표시
                        .map(cc -> new CoCommentDTO(itemRepository.findItemCommentById(cc.getMentionId()), cc, memberIdByJwt, item.getMember().getId()))
                        .collect(Collectors.toList());
                /** 쿼리문 나감. 결론: for 문 안에서 쿼리문 대신 DTO 안에서 해결 */
                //boolean isLiked = postRepository.checkCommentIsLiked(c.getId(), memberIdByJwt);
                //4. 댓글 DTO 생성 후 최종 DTOList 에 넣어주기
                CommentResponse univCommentResponse = new CommentResponse(c, coCommentDTO, memberIdByJwt, item.getMember().getId());
                univCommentResponseList.add(univCommentResponse);
            }
            return univCommentResponseList;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String deleteItemComment(Long itemCommentId) throws BaseException {
        try {
            itemRepository.deleteItemComment(itemCommentId);
            return "삭제에 성공했습니다.";
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 6.3 거래 게시판 글 상세 api
     */
    public Item getItem (Long itemId) throws BaseException {
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


    /**
     * 6.6 판매 상태 변경 API
     */
    @Transactional
    public void modifyItemStatus(Long itemId, String itemStatus) throws BaseException {
        Item modifyItem = checkMemberAndItemIsValidAndByAuthor(itemId);
        modifyItem.modifyItemStatus(itemStatus);
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
