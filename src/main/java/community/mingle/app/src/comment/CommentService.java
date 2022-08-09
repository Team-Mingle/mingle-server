package community.mingle.app.src.comment;


import community.mingle.app.config.BaseException;
import community.mingle.app.src.comment.model.PostCommentLikesTotalResponse;
import community.mingle.app.src.comment.model.PostCommentLikesUnivResponse;
import community.mingle.app.src.comment.model.PostTotalCommentRequest;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalCommentLike;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Total.TotalPostLike;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivCommentLike;
import community.mingle.app.src.post.model.PostLikesTotalResponse;
import community.mingle.app.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static community.mingle.app.config.BaseResponseStatus.DATABASE_ERROR;
import static community.mingle.app.config.BaseResponseStatus.EMPTY_JWT;

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
     * 3.05 통합 게시물 댓글 좋아요 api
     */
    @Transactional
    public PostCommentLikesTotalResponse likesTotalComment(Long commentIdx) throws BaseException{
        Long memberIdByJwt;
        try {
            memberIdByJwt = jwtService.getUserIdx();
        } catch (Exception e) {
            throw new BaseException(EMPTY_JWT);
        }
        try {
            TotalComment totalcomment =commentRepository.findTotalCommentbyId(commentIdx);
            Member member = commentRepository.findMemberbyId(memberIdByJwt);


            TotalCommentLike totalCommentLike = TotalCommentLike.likesTotalComment(totalcomment, member);
            Long id = commentRepository.save(totalCommentLike);
            int likeCount = totalcomment.getTotalCommentLikes().size();
            return new PostCommentLikesTotalResponse(id,likeCount);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 3.06 학교 게시물 댓글 좋아요 api
     */
    @Transactional
    public PostCommentLikesUnivResponse likesUnivComment(Long commentIdx) throws BaseException{
        Long memberIdByJwt;
        try {
            memberIdByJwt = jwtService.getUserIdx();
        } catch (Exception e) {
            throw new BaseException(EMPTY_JWT);
        }
        try {
            UnivComment univComment =commentRepository.findUnivCommentbyId(commentIdx);
            Member member = commentRepository.findMemberbyId(memberIdByJwt);


            UnivCommentLike univCommentLike = UnivCommentLike.likesUnivComment(univComment, member);
            Long id = commentRepository.save(univCommentLike);
            int likeCount = univComment.getUnivCommentLikes().size();
            return new PostCommentLikesUnivResponse(id,likeCount);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


    @Transactional
    public void unlikeTotalComment(Long commentIdx)  throws BaseException {

        try {
            commentRepository.deleteLikeTotal(commentIdx);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }


    @Transactional
    public void unlikeUnivComment(Long commentIdx)  throws BaseException {

        try {
            commentRepository.deleteLikeUniv(commentIdx);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
