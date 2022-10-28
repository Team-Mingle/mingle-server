package community.mingle.app.src.comment;


import community.mingle.app.config.BaseException;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.comment.model.*;
import community.mingle.app.src.domain.Total.*;
import community.mingle.app.src.domain.Univ.*;
import community.mingle.app.src.firebase.FirebaseCloudMessageService;
import community.mingle.app.utils.JwtService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static community.mingle.app.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final JwtService jwtService;
    private final CommentRepository commentRepository;

    private final FirebaseCloudMessageService firebaseCloudMessageService;

    private final FirebaseCloudMessageService fcmService;



    /**
     * 4.1 전체게시판 댓글 작성 api
     * @return commentId
     */
    @Transactional

    public Long createTotalComment(PostTotalCommentRequest postTotalCommentRequest) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();


        TotalPost post = commentRepository.findTotalPostbyId(postTotalCommentRequest.getPostId());
        if (post == null) {
            throw new BaseException(POST_NOT_EXIST);
        }

        try {
            Member member = commentRepository.findMemberbyId(memberIdByJwt);
            Long anonymousId;

            if (postTotalCommentRequest.isAnonymous() == true) {
                anonymousId = commentRepository.findTotalAnonymousId(post, memberIdByJwt);
            } else {
                anonymousId = null;
            }

            //댓글 생성
            TotalComment comment = TotalComment.createComment(post, member, postTotalCommentRequest.getContent(), postTotalCommentRequest.getParentCommentId(), postTotalCommentRequest.getMentionId(), postTotalCommentRequest.isAnonymous(), anonymousId);

            TotalComment savedComment = commentRepository.saveTotalComment(comment);
            Long id = savedComment.getId();

            sendTotalPush(post, postTotalCommentRequest, member);
            return id;

        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public void sendTotalPush(TotalPost post, PostTotalCommentRequest postTotalCommentRequest, Member creatorMember) throws IOException {
        Member postMember = post.getMember();
        Member parentMember = commentRepository.findTotalCommentById(postTotalCommentRequest.getParentCommentId()).getMember();
        Member mentionMember = commentRepository.findTotalCommentById(postTotalCommentRequest.getMentionId()).getMember();
        String messageTitle = "광장";
        if (parentMember == null) {
            if (postMember.getId() == creatorMember.getId()) {
                return;
            }
            else {
                firebaseCloudMessageService.sendMessageTo(postMember.getFcmToken(), messageTitle, "새로운 댓글이 달렸어요" + postTotalCommentRequest.getContent());
            }
        } else if (parentMember != null) {
            Map<Member, String> map = new HashMap<>();
            map.put(postMember, "postMemberId");
            map.put(parentMember, "parentMemberId");
            map.put(mentionMember, "mentionMemberId");
            map.put(creatorMember, "creatorMemberId");
            for (Member member : map.keySet()) {
                if (map.get(member) == "creatorMemberId") {
                    continue;
                }else{
                    firebaseCloudMessageService.sendMessageTo(member.getFcmToken(), messageTitle, postTotalCommentRequest.getContent());
                }
            }
        }

    }

    /**
     * 4.2 학교 댓글 작성 api
     */
    @Transactional
    public Long createUnivComment(PostUnivCommentRequest request) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx(); //쓴사람
        UnivPost univPost = commentRepository.findUnivPostById(request.getPostId());
        if (univPost == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
        try {
            Member member = commentRepository.findMemberbyId(memberIdByJwt);
            Long anonymousId;
            if (request.isAnonymous() == true) {
                anonymousId = commentRepository.findUnivAnonymousId(univPost, memberIdByJwt);
                System.out.println("true");
            } else {
                System.out.println("false");
                anonymousId = null;
            }
            //댓글 생성
            UnivComment comment = UnivComment.createComment(univPost, member, request.getContent(), request.getParentCommentId(), request.getMentionId(), request.isAnonymous(), anonymousId);
            commentRepository.saveUnivComment(comment);
            sendUnivNotification(univPost, member, request); //알림 전송

            return comment.getId();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public void sendUnivNotification(UnivPost univPost, Member commentWriter, PostUnivCommentRequest request) throws IOException {
        String title = "잔디밭";
        Member postWriter = univPost.getMember(); //1. 게시물 작성자 id

        //댓글일 시
        if (request.getParentCommentId() == null) {
            if (postWriter.getId() == commentWriter.getId()) {
                return;
            } else {
                String body = "새로운 댓글이 달렸어요: " + request.getContent();
//                fcmService.sendMessageTo(postWriter.getFcmToken(), title, body);
            }
        }

        //대댓글일 시
        else {
            Member parentWriter = commentRepository.findUnivCommentById(request.getParentCommentId()).getMember(); //2. parentComment 작성자 Id
            //Member mentionWriter = commentRepository.findUnivCommentById(request.getMentionId()).getMember(); //3. mention 당한사람 Id
            Member mentionWriter;
            UnivComment mentionComment = commentRepository.findUnivCommentById(request.getMentionId());
            if (mentionComment == null) {
                mentionWriter = null;
            } else {
                mentionWriter = mentionComment.getMember();
            }

            Map<Member, String> map = new HashMap<>(); //중복제거
            map.put(postWriter, "postWriter"); // 78
            map.put(parentWriter, "parentWriter"); //  79
            map.put(mentionWriter, "mentionWriter"); // 79
            map.put(commentWriter, "commentWriter"); //  80

            map.remove(commentWriter);

            for (Member member : map.keySet()) {
                System.out.println(map.get(member));
            }

            for (Member member : map.keySet()) {
                String token = member.getFcmToken();
                String body = "새로운 대댓글이 달렸어요: " + request.getContent();
                System.out.println(body);
//                fcmService.sendMessageTo(token, title, body);
            }
        }
    }



     /**
      * 4.3 통합 게시물 댓글 좋아요 api
      */
     @Transactional
     public PostCommentLikesTotalResponse likesTotalComment(Long commentIdx) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        try {
            TotalComment totalcomment = commentRepository.findTotalCommentById(commentIdx);
            Member member = commentRepository.findMemberbyId(memberIdByJwt);


            TotalCommentLike totalCommentLike = TotalCommentLike.likesTotalComment(totalcomment, member);
            Long id = commentRepository.save(totalCommentLike);
            int likeCount = totalcomment.getTotalCommentLikes().size();
            return new PostCommentLikesTotalResponse(id, likeCount);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 4.4 학교 게시물 댓글 좋아요 api
     */
    @Transactional
    public PostCommentLikesUnivResponse likesUnivComment(Long commentIdx) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        try {
            UnivComment univComment = commentRepository.findUnivCommentById(commentIdx);
            Member member = commentRepository.findMemberbyId(memberIdByJwt);


            UnivCommentLike univCommentLike = UnivCommentLike.likesUnivComment(univComment, member);
            Long id = commentRepository.save(univCommentLike);
            int likeCount = univComment.getUnivCommentLikes().size();
            return new PostCommentLikesUnivResponse(id, likeCount);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 4.5 통합게시물 좋아요 취소
     */
    @Transactional
    public void unlikeTotalComment(Long commentIdx) throws BaseException {
        try {
            commentRepository.deleteLikeTotal(commentIdx);
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

        try {
            commentRepository.deleteLikeUniv(commentIdx);
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
