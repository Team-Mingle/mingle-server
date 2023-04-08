package community.mingle.app.src.item;

import community.mingle.app.config.BaseException;
import community.mingle.app.src.comment.CommentRepository;
import community.mingle.app.src.comment.model.PostTotalCommentResponse;
import community.mingle.app.src.domain.*;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalNotification;
import community.mingle.app.src.firebase.FirebaseCloudMessageService;
import community.mingle.app.src.item.model.*;
import community.mingle.app.src.member.MemberRepository;
import community.mingle.app.src.post.PostRepository;
import community.mingle.app.utils.JwtService;
import community.mingle.app.utils.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final MemberRepository memberRepository;
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
            ItemLike itemLike = new ItemLike(member, item);
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
            sendTotalPush(item, postItemCommentRequest, member, comment);
            //알림 저장
//            TotalNotification totalNotification = TotalNotification.saveTotalNotification(item, item.getMember(),comment);
//            if (item.getMember().getTotalNotifications().size() > 20) {
//                itemRepository.deleteTotalNotification(item.getMember().getTotalNotifications().get(0).getId());
////                List<TotalNotification> totalNotifications = item.getMember().getTotalNotifications();
////                totalNotifications.remove(0);
////                item.getMember().deleteTotalNotification(totalNotifications);
//
//            }
//            memberRepository.saveTotalNotification(totalNotification);


            PostTotalCommentResponse postTotalCommentResponse = new PostTotalCommentResponse(anonymousId, comment, item.getMember().getId());
            return postTotalCommentResponse;

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public void sendTotalPush(Item item, PostItemCommentRequest postItemCommentRequest, Member creatorMember, ItemComment comment) throws IOException {
        Member postMember = item.getMember();

        //이거 두개는 원래 죽어있는게 맞겠지? 아ㅏ그르네
//        Member parentMember = commentRepository.findTotalCommentById(postItemCommentRequest.getParentCommentId()).getMember(); //패런츠 커멘트가 없는 놈한테도 페런츠 커멘트 아이디를 가져오려고 함 (ㅁㅊ놈)
//        Member mentionMember = commentRepository.findTotalCommentById(postItemCommentRequest.getMentionId()).getMember();
        String messageTitle = "중고장터";
        if (postItemCommentRequest.getParentCommentId() == null) {
            if (postMember.getId() == creatorMember.getId()) {
                return;
            }
            else {
                //이게 방금 살린거
                firebaseCloudMessageService.sendMessageTo(postMember.getFcmToken(), messageTitle, "새로운 댓글이 달렸어요: " + postItemCommentRequest.getContent(), TableType.Item, item.getId());
                //알림 저장
                TotalNotification totalNotification = TotalNotification.saveTotalNotification(item, postMember,comment);
                memberRepository.saveTotalNotification(totalNotification);
                if (postMember.getTotalNotifications().size() +postMember.getUnivNotifications().size()> 20) {
                    commentRepository.deleteTotalNotification(postMember.getTotalNotifications().get(0).getId());
                }
            }
        } else if (postItemCommentRequest.getParentCommentId()!= null) {
            Member parentMember = commentRepository.findTotalCommentById(postItemCommentRequest.getParentCommentId()).getMember();
            Member mentionMember;
            if (postItemCommentRequest.getMentionId() == null) {
                mentionMember = null;
            } else {
                mentionMember = commentRepository.findTotalCommentById(postItemCommentRequest.getMentionId()).getMember();
            }
            Map<Member, String> map = new HashMap<>();
            map.put(postMember, "postMemberId");
            map.put(parentMember, "parentMemberId");
            map.put(mentionMember, "mentionMemberId");
            map.put(creatorMember, "creatorMemberId");
            for (Member member : map.keySet()) {
                if (map.get(member) == "creatorMemberId") {
                    continue;
                }else{
                    firebaseCloudMessageService.sendMessageTo(member.getFcmToken(), messageTitle, postItemCommentRequest.getContent(), TableType.Item, item.getId());
                    //알림 저장
                    TotalNotification totalNotification = TotalNotification.saveTotalNotification(item, member,comment);
                    memberRepository.saveTotalNotification(totalNotification);
                    if ((member.getTotalNotifications().size()+member.getUnivNotifications().size())> 20) {
                        commentRepository.deleteTotalNotification(member.getTotalNotifications().get(0).getId());
                    }
                }
            }
        }

    }
}
