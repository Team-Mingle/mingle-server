package community.mingle.app.src.comment;


import community.mingle.app.config.BaseException;
import community.mingle.app.src.comment.model.PostTotalCommentRequest;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static community.mingle.app.config.BaseResponseStatus.*;
import static community.mingle.app.config.BaseResponseStatus.DELETE_FAIL_POST;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final JwtService jwtService;
    private final CommentRepository commentRepository;

    /**
     * 전체게시판 댓글 작성 api
     * @return commentId
     */
    @Transactional
    public Long createComment(PostTotalCommentRequest postTotalCommentRequest) throws BaseException {

        //jwt userIdx 추출
        Long memberIdByJwt;
        try {
            memberIdByJwt = jwtService.getUserIdx();
        } catch (Exception e) {
            throw new BaseException(EMPTY_JWT);
        }

        try {
            TotalPost post = commentRepository.findTotalPostbyId(postTotalCommentRequest.getPostId());
            Member member = commentRepository.findMemberbyId(memberIdByJwt);

            Long anonymousId;

            if (postTotalCommentRequest.isAnonymous() == true) {
                anonymousId = commentRepository.findAnonymousId(post, memberIdByJwt);
            }
            else {
                anonymousId = null;
            }

            //댓글 생성
            TotalComment comment = TotalComment.createComment(post, member, postTotalCommentRequest.getContent(), postTotalCommentRequest.getParentCommentId(), postTotalCommentRequest.isAnonymous(), anonymousId);

            TotalComment savedComment = commentRepository.save(comment);
            Long id = savedComment.getId();

            return id;

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 4.07 통합 게시물 댓글 삭제 API
     */
    @Transactional
    public void deleteTotalComment (Long id) throws BaseException{
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
     * 4.08 학교 게시물 댓글 삭제 API
     */
    @Transactional
    public void deleteUnivComment (Long id) throws BaseException{
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
