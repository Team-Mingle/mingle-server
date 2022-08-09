package community.mingle.app.src.comment;


import community.mingle.app.config.BaseException;
import community.mingle.app.src.comment.model.PostCommentRequest;
import community.mingle.app.src.comment.model.PostTotalCommentRequest;
import community.mingle.app.src.comment.model.PostUnivCommentRequest;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivPost;
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
    public Long createTotalComment(PostTotalCommentRequest postTotalCommentRequest) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();

        try {
            TotalPost post = commentRepository.findTotalPostbyId(postTotalCommentRequest.getPostId());
            Member member = commentRepository.findMemberbyId(memberIdByJwt);

            Long anonymousId;

            if (postTotalCommentRequest.isAnonymous() == true) {
                anonymousId = commentRepository.findTotalAnonymousId(post, memberIdByJwt);
            }
            else {
                anonymousId = null;
            }

            //댓글 생성
            TotalComment comment = TotalComment.createComment(post, member, postTotalCommentRequest.getContent(), postTotalCommentRequest.getParentCommentId(), postTotalCommentRequest.isAnonymous(), anonymousId);

            TotalComment savedComment = commentRepository.saveTotalComment(comment);
            Long id = savedComment.getId();

            return id;

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public Long createUnivComment(PostUnivCommentRequest request) throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();

        UnivPost univPost = commentRepository.findUnivPostById(request.getPostId());
        Member member = commentRepository.findMemberbyId(memberIdByJwt);

        Long anonymousId;

        if (request.isAnonymous() == true) {
            anonymousId = commentRepository.findUnivAnonymousId(univPost, memberIdByJwt);
            System.out.println("true");
        }
        else {
            System.out.println("false");
            anonymousId = null;
        }

        //댓글 생성
        UnivComment comment = UnivComment.createComment(univPost, member, request.getContent(), request.getParentCommentId(), request.isAnonymous(), anonymousId);

//        UnivComment savedComment = commentRepository.saveUnivComment(comment);
//        Long id = savedComment.getId();

        commentRepository.saveUnivComment(comment);
        return comment.getId();

    }


}
