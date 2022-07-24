package community.mingle.app.src.comment;


import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.auth.model.PostSignupResponse;
import community.mingle.app.src.comment.model.PostTotalCommentRequest;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.UnivName;
import community.mingle.app.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

}
