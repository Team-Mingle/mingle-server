package community.mingle.app.src.comment;


import community.mingle.app.config.BaseException;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.comment.model.*;
import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.TableType;
import community.mingle.app.src.domain.Total.*;
import community.mingle.app.src.domain.Univ.*;
import community.mingle.app.src.firebase.FirebaseCloudMessageService;
import community.mingle.app.src.member.MemberRepository;
import community.mingle.app.utils.JwtService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static community.mingle.app.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final JwtService jwtService;
    private final CommentRepository commentRepository;

    private final MemberRepository memberRepository;

    private final FirebaseCloudMessageService firebaseCloudMessageService;

    private final FirebaseCloudMessageService fcmService;



    /**
     * 4.1 전체게시판 댓글 작성 api
     * @return commentId
     */
    @Transactional

    public PostTotalCommentResponse createTotalComment(PostTotalCommentRequest postTotalCommentRequest) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();


        TotalPost post = commentRepository.findTotalPostbyId(postTotalCommentRequest.getPostId());
        if (post == null) {
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
        List<TotalComment> totalPostComments = post.getTotalPostComments();
        boolean parentFlag = false;
        boolean mentionFlag = false;

        if (postTotalCommentRequest.getMentionId() == null && postTotalCommentRequest.getParentCommentId() == null) {
        }
        else {
            for (TotalComment totalPostComment : totalPostComments) {
                if (Objects.equals(totalPostComment.getId(), postTotalCommentRequest.getParentCommentId())) {
                    parentFlag = true;
                }
                if (Objects.equals(totalPostComment.getId(), postTotalCommentRequest.getMentionId())) {
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
            if (Objects.equals(member.getId(), post.getMember().getId())) { //댓쓴이가 author 일때
                anonymousId = Long.valueOf(0); //isAnonymous = true, but AnonymousNo is 0
            } else if (postTotalCommentRequest.isAnonymous() == true) {
                anonymousId = commentRepository.findTotalAnonymousId(post, memberIdByJwt);
            } else {
                anonymousId = Long.valueOf(0); // null -> 0 으로 수정
            }

            //댓글 생성
            TotalComment comment = TotalComment.createComment(post, member, postTotalCommentRequest.getContent(), postTotalCommentRequest.getParentCommentId(), postTotalCommentRequest.getMentionId(), postTotalCommentRequest.isAnonymous(), anonymousId);
            System.out.println(comment);
            commentRepository.saveTotalComment(comment);
            sendTotalPush(post, postTotalCommentRequest, member, comment);
            //알림 저장
//            TotalNotification totalNotification = TotalNotification.saveTotalNotification(post, post.getMember(),comment);
//            if (post.getMember().getTotalNotifications().size() > 20) {
//                commentRepository.deleteTotalNotification(post.getMember().getTotalNotifications().get(0).getId());
////                List<TotalNotification> totalNotifications = post.getMember().getTotalNotifications();
////                totalNotifications.remove(0);
////                post.getMember().deleteTotalNotification(totalNotifications);
//
//            }
//            memberRepository.saveTotalNotification(totalNotification);


            PostTotalCommentResponse postTotalCommentResponse = new PostTotalCommentResponse(anonymousId, comment, post.getMember().getId());
            return postTotalCommentResponse;

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public void sendTotalPush(TotalPost post, PostTotalCommentRequest postTotalCommentRequest, Member creatorMember, TotalComment comment) throws IOException {
        Member postMember = post.getMember();

        //이거 두개는 원래 죽어있는게 맞겠지? 아ㅏ그르네
//        Member parentMember = commentRepository.findTotalCommentById(postTotalCommentRequest.getParentCommentId()).getMember(); //패런츠 커멘트가 없는 놈한테도 페런츠 커멘트 아이디를 가져오려고 함 (ㅁㅊ놈)
//        Member mentionMember = commentRepository.findTotalCommentById(postTotalCommentRequest.getMentionId()).getMember();
        String messageTitle = "광장";
        if (postTotalCommentRequest.getParentCommentId() == null) {
            if (postMember.getId() == creatorMember.getId()) {
                return;
            }
            else {
                //이게 방금 살린거
                firebaseCloudMessageService.sendMessageTo(postMember.getFcmToken(), messageTitle, "새로운 댓글이 달렸어요: " + postTotalCommentRequest.getContent(), TableType.TotalPost, post.getId());
                //알림 저장
                TotalNotification totalNotification = TotalNotification.saveTotalNotification(post, postMember,comment);
                memberRepository.saveTotalNotification(totalNotification);
                if (postMember.getTotalNotifications().size() > 20) {
                    commentRepository.deleteTotalNotification(postMember.getTotalNotifications().get(0).getId());
                }
            }
        } else if (postTotalCommentRequest.getParentCommentId()!= null) {
            Member parentMember = commentRepository.findTotalCommentById(postTotalCommentRequest.getParentCommentId()).getMember();
            Member mentionMember;
            if (postTotalCommentRequest.getMentionId() == null) {
                mentionMember = null;
            } else {
                mentionMember = commentRepository.findTotalCommentById(postTotalCommentRequest.getMentionId()).getMember();
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
                    firebaseCloudMessageService.sendMessageTo(member.getFcmToken(), messageTitle, postTotalCommentRequest.getContent(), TableType.TotalPost, post.getId());
                    //알림 저장
                    TotalNotification totalNotification = TotalNotification.saveTotalNotification(post, member,comment);
                    memberRepository.saveTotalNotification(totalNotification);
                    if (member.getTotalNotifications().size() > 20) {
                        commentRepository.deleteTotalNotification(member.getTotalNotifications().get(0).getId());
                    }
                }
            }
        }

    }

    /**
     * 4.2 학교 댓글 작성 api
     */
    @Transactional
    public PostUnivCommentResponse createUnivComment(PostUnivCommentRequest request) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx(); //쓴사람
        UnivPost univPost = commentRepository.findUnivPostById(request.getPostId());
        if (univPost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }

        //check parentCommentId, mentionId validity
        List<UnivComment> univComments = univPost.getUnivComments();
        boolean parentFlag = false;
        boolean mentionFlag = false;
        if (request.getMentionId() == null && request.getParentCommentId() == null) {
        }
        //새로운 댓글의 부모댓글이나 맨션 댓글 id가 제대로 들어왔는지 확인하는 로직
        else {
            for (UnivComment univComment : univComments) {
                if (Objects.equals(univComment.getId(), request.getParentCommentId())){
                    parentFlag = true;
                }
                if (Objects.equals(univComment.getId(), request.getMentionId())) {
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
            if (Objects.equals(member.getId(), univPost.getMember().getId())) { //댓쓴이가 author 일때
                anonymousId = Long.valueOf(0); //isAnonymous = true, but AnonymousNo is 0
            }
            else if (request.isAnonymous() == true) {
            anonymousId = commentRepository.findUnivAnonymousId(univPost, memberIdByJwt);
            System.out.println("true");
        } else {
            System.out.println("false");
            anonymousId = Long.valueOf(0); // null -> 0 으로 수정
        }
            //댓글 생성
            UnivComment comment = UnivComment.createComment(univPost, member, request.getContent(), request.getParentCommentId(), request.getMentionId(), request.isAnonymous(), anonymousId);
            System.out.println(request.isAnonymous());
            commentRepository.saveUnivComment(comment);
            sendUnivNotification(univPost, member, request, comment); //알림 전송
            System.out.println(comment.getId());


            PostUnivCommentResponse postUnivCommentResponse = new PostUnivCommentResponse(anonymousId, comment, univPost.getMember().getId());
            return postUnivCommentResponse;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public void sendUnivNotification(UnivPost univPost, Member commentWriter, PostUnivCommentRequest request, UnivComment comment) throws IOException {
        String title = "잔디밭";
        Member postWriter = univPost.getMember(); //1. 게시물 작성자 id

        //댓글일 시
        if (request.getParentCommentId() == null) {
            if (postWriter.getId() == commentWriter.getId()) {
                return;
            } else {
                String body = "새로운 댓글이 달렸어요: " + request.getContent();
                fcmService.sendMessageTo(postWriter.getFcmToken(), title, body, TableType.UnivPost, univPost.getId());
                UnivNotification univNotification = UnivNotification.saveUnivNotification(univPost, postWriter, comment);
                memberRepository.saveUnivNotification(univNotification);
                if (postWriter.getUnivNotifications().size() > 20) {
                    commentRepository.deleteUnivNotification(postWriter.getUnivNotifications().get(0).getId());
                }
            }
        }


        //대댓글일 시
        else {
            Member parentWriter = commentRepository.findUnivCommentById(request.getParentCommentId()).getMember(); //2. parentComment 작성자 Id
            //Member mentionWriter = commentRepository.findUnivCommentById(request.getMentionId()).getMember(); //3. mention 당한사람 Id
            Member mentionWriter;

            if (request.getMentionId() == null) {
                mentionWriter = null;
            } else {
                UnivComment mentionComment = commentRepository.findUnivCommentById(request.getMentionId());
                mentionWriter = mentionComment.getMember();
            }

            Map<Member, String> map = new HashMap<>(); //중복제거
            map.put(postWriter, "postWriter"); // 78
            map.put(parentWriter, "parentWriter"); //  79
            map.put(mentionWriter, "mentionWriter"); // 79
            map.put(commentWriter, "commentWriter"); //  80

            map.remove(commentWriter);


            for (Member member : map.keySet()) {
                String token = member.getFcmToken();
                String body = "새로운 대댓글이 달렸어요: " + request.getContent();
                System.out.println(body);
                fcmService.sendMessageTo(token, title, body, TableType.UnivPost, univPost.getId());
                //알림 저장
                UnivNotification univNotification = UnivNotification.saveUnivNotification(univPost, member, comment);
                memberRepository.saveUnivNotification(univNotification);
                if (member.getUnivNotifications().size() > 20) {
                    commentRepository.deleteUnivNotification(member.getUnivNotifications().get(0).getId());
                }
            }


        }
    }



     /**
      * 4.3 통합 게시물 댓글 좋아요 api
      */
     @Transactional
     public PostCommentLikesTotalResponse likesTotalComment(Long commentIdx) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();

         TotalComment totalcomment = commentRepository.findTotalCommentById(commentIdx);
         if (totalcomment == null) {
             throw new BaseException(COMMENT_NOT_EXIST);
         }
         if (totalcomment.getStatus().equals(PostStatus.INACTIVE) || totalcomment.getStatus().equals(PostStatus.REPORTED)) {
             throw new BaseException(REPORTED_DELETED_COMMENT);
         }
         Member member = commentRepository.findMemberbyId(memberIdByJwt);

         TotalCommentLike totalCommentLike = TotalCommentLike.likesTotalComment(totalcomment, member);
         if (totalCommentLike == null) {
             throw new BaseException(DUPLICATE_LIKE);
         }
         else {
             try {
//            TotalCommentLike totalCommentLike = TotalCommentLike.likesTotalComment(totalcomment, member);
                 Long id = commentRepository.save(totalCommentLike);
                 int likeCount = totalcomment.getTotalCommentLikes().size();
                 return new PostCommentLikesTotalResponse(id, likeCount);

             } catch (Exception e) {
                 e.printStackTrace();
                 throw new BaseException(DATABASE_ERROR);
             }
         }
    }


    /**
     * 4.4 학교 게시물 댓글 좋아요 api
     */
    @Transactional
    public PostCommentLikesUnivResponse likesUnivComment(Long commentIdx) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        UnivComment univComment = commentRepository.findUnivCommentById(commentIdx);
        if (univComment == null) {
            throw new BaseException(COMMENT_NOT_EXIST);
        }
        if (univComment.getStatus().equals(PostStatus.INACTIVE) || univComment.getStatus().equals(PostStatus.REPORTED)) {
            throw new BaseException(REPORTED_DELETED_COMMENT);
        }
        Member member = commentRepository.findMemberbyId(memberIdByJwt);
        UnivCommentLike univCommentLike = UnivCommentLike.likesUnivComment(univComment, member);
        if (univCommentLike == null) {
            throw new BaseException(DUPLICATE_LIKE);
        }
        else {
            try {
                Long id = commentRepository.save(univCommentLike);
                int likeCount = univComment.getUnivCommentLikes().size();
                return new PostCommentLikesUnivResponse(id, likeCount);

            } catch (Exception e) {
                e.printStackTrace();
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }


    /**
     * 4.5 통합게시물 좋아요 취소
     */
    @Transactional
    public void unlikeTotalComment(Long commentIdx) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        try {
            commentRepository.deleteLikeTotal(commentIdx, memberIdByJwt);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 4.6 대학 게시물 좋아요 취소
     */
    @Transactional
    public void unlikeUnivComment(Long commentIdx) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        try {
            commentRepository.deleteLikeUniv(commentIdx,memberIdByJwt);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }


    /**
     * 4.7 통합 게시물 댓글 삭제 API
     */
    @Transactional
    public void deleteTotalComment(Long id) throws BaseException {
        Member member;
        TotalComment totalComment;
        Long memberIdByJwt = jwtService.getUserIdx();
        member = commentRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(USER_NOT_EXIST);
        }


        totalComment = commentRepository.findTotalCommentById(id);
        if (totalComment == null) {
            throw new BaseException(COMMENT_NOT_EXIST);
        }

        if (memberIdByJwt != totalComment.getMember().getId()) {
            throw new BaseException(MODIFY_NOT_AUTHORIZED);
        }
        try {
            totalComment.deleteTotalComment();
        } catch (Exception e) {
            throw new BaseException(DELETE_FAIL_COMMENT);
        }
    }


    /**
     * 4.8 학교 게시물 댓글 삭제 API
     */
    @Transactional
    public void deleteUnivComment(Long id) throws BaseException {
        Member member;
        UnivComment univComment;
        Long memberIdByJwt = jwtService.getUserIdx();
        member = commentRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(USER_NOT_EXIST);
        }

        try {
            univComment = commentRepository.findUnivCommentById(id);
        } catch (Exception e) {
            throw new BaseException(COMMENT_NOT_EXIST);
        }

        if (memberIdByJwt != univComment.getMember().getId()) {
            throw new BaseException(MODIFY_NOT_AUTHORIZED);
        }
        try {
            univComment.deleteUnivComment();
        } catch (Exception e) {
            throw new BaseException(DELETE_FAIL_COMMENT);
        }
    }


}
