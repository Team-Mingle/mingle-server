package community.mingle.app.src.home;

import community.mingle.app.config.BaseException;
import community.mingle.app.src.domain.Banner;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.post.PostRepository;
import community.mingle.app.utils.JwtService;
import community.mingle.app.utils.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static community.mingle.app.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class HomeService {
    private final JwtService jwtService;
    private final HomeRepository homeRepository;



    /**
     * 5.1 광고 배너 API
     */
    public List<Banner> findBanner() throws BaseException {
        try {
            List<Banner> banner = homeRepository.findBanner();
            return banner;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 5.2 홈 전체 배스트 게시판 API
     */
    public List<TotalPost> findTotalPostWithMemberLikeComment() throws BaseException {
        List<TotalPost> totalPosts = homeRepository.findTotalPostWithMemberLikeComment();
        if (totalPosts.size() == 0) {
            throw new BaseException(EMPTY_BEST_POSTS);
        }
        return totalPosts;
    }

    /**
     * 5.3 홈 학교 베스트 게시판 API
     */
    public List<UnivPost> findAllWithMemberLikeCommentCount() throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();  // jwtService 의 메소드 안에서 throw 해줌 -> controller 로 넘어감
        Member member;
        member = homeRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(DATABASE_ERROR); //무조건 찾아야하는데 못찾을경우 (이미 jwt 에서 검증이 되기때문)
        }
        List<UnivPost> univPosts = homeRepository.findAllWithMemberLikeCommentCount(member);
        if (univPosts.size() == 0) {
            throw new BaseException(EMPTY_BEST_POSTS);
        }
        return univPosts;
    }

    /**
     * 5.4 홈 전체 최신 게시글 api
     */
    public List<TotalPost> findTotalRecentPosts() throws BaseException {
        List<TotalPost> totalPosts = homeRepository.findTotalRecentPosts();
        if (totalPosts.size() == 0) {
            throw new BaseException(EMPTY_RECENT_POSTS);
        }
        return totalPosts;
    }

    /**
     * 5.5 홈 학교 최신 게시글 api
     */
    public List<UnivPost> findUnivRecentPosts() throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
        Member member = homeRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(DATABASE_ERROR);
        }
        List<UnivPost> univPosts;
        try {
          univPosts = homeRepository.findUnivRecentPosts(member);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
        if (univPosts.size() == 0) {
            throw new BaseException(EMPTY_RECENT_POSTS);
        }
        return univPosts;
    }

}
