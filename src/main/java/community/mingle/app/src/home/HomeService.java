package community.mingle.app.src.home;

import community.mingle.app.config.BaseException;
import community.mingle.app.src.domain.Banner;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.home.model.CreateBannerRequest;
import community.mingle.app.src.home.model.CreateBannerResponse;
import community.mingle.app.src.home.model.HomePostResponse;
import community.mingle.app.utils.JwtService;
import community.mingle.app.utils.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static community.mingle.app.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class HomeService {
    private final JwtService jwtService;
    private final HomeRepository homeRepository;
    private final S3Service s3Service;


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

    @Transactional
    public CreateBannerResponse createBanner(CreateBannerRequest createBannerRequest) throws BaseException {
        Member member;
        Long memberIdByJwt = jwtService.getUserIdx();
        member = homeRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(USER_NOT_EXIST);
        }
        try {
            List<String> fileNameList = null;
            int finalId = 0;
            if (createBannerRequest.getMultipartFile() != null && !createBannerRequest.getMultipartFile().isEmpty()) {
                fileNameList = s3Service.uploadFile(createBannerRequest.getMultipartFile(), "banner");
                for (String fileName : fileNameList) { //Banner사진 여러개 올리기 가능? 일단 for loop 남겨둠.
                    Banner banner = Banner.createBanner(member, createBannerRequest, fileName, createBannerRequest.getLink());
                    int id = homeRepository.save(banner);
                    finalId = id; //id는 마지막 사진 id만 리턴 (연결된 테이블 따로 없이 바로 url을 배너에 저장하니까)
                }
            }
            return new CreateBannerResponse(finalId, fileNameList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(CREATE_FAIL_POST);
        }
    }

    /**
     * 5.2 홈 전체 배스트 게시판 API
     */
    public List<TotalPost> findAllTotalPostsWithMemberLikeComment() throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();  // jwtService 의 메소드 안에서 throw 해줌 -> controller 로 넘어감
        List<TotalPost> totalPosts = homeRepository.findAllTotalPostWithMemberLikeComment(memberIdByJwt);
        if (totalPosts.size() == 0) {
            throw new BaseException(EMPTY_BEST_POSTS);
        }
        return totalPosts;
    }

    /**
     * 5.3 홈 학교 베스트 게시판 API
     */
    public List<UnivPost> findAllUnivPostsWithMemberLikeCommentCount() throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();  // jwtService 의 메소드 안에서 throw 해줌 -> controller 로 넘어감
        Member member = homeRepository.findMemberbyId(memberIdByJwt);
        if (member == null) {
            throw new BaseException(DATABASE_ERROR); //무조건 찾아야하는데 못찾을경우 (이미 jwt 에서 검증이 되기때문)
        }
        List<UnivPost> univPosts = homeRepository.findAllUnivPostsWithMemberLikeCommentCount(member);
        if (univPosts.size() == 0) {
            throw new BaseException(EMPTY_BEST_POSTS);
        }
        return univPosts;
    }

    /**
     * 5.4 홈 전체 최신 게시글 api
     */
    public List<TotalPost> findTotalRecentPosts() throws BaseException {
        Long memberIdByJwt = jwtService.getUserIdx();
//        Member member = homeRepository.findMemberbyId(memberIdByJwt);
        List<TotalPost> totalPosts = homeRepository.findTotalRecentPosts(memberIdByJwt);
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

    public List<HomePostResponse> findAllUnitedPostsWithMemberLikeCommentCount(Long memberId) throws BaseException {
        Member member = homeRepository.findMemberbyId(memberId);
        if (member == null) {
            throw new BaseException(DATABASE_ERROR); //무조건 찾아야하는데 못찾을경우 (이미 jwt 에서 검증이 되기때문)
        }
        List<UnivPost> univPosts = homeRepository.findAllUnivPostsWithMemberLikeCommentCount(member);
        List<HomePostResponse> univBestPosts = univPosts.stream()
                .map(p -> new HomePostResponse(p, memberId))
                .collect(Collectors.toList());
        List<TotalPost> totalPosts = homeRepository.findAllTotalPostWithMemberLikeComment(memberId);
        List<HomePostResponse> totalBestPosts = totalPosts.stream()
                .map(m -> new HomePostResponse(m, memberId))
                .collect(Collectors.toList());

        List<HomePostResponse> homePostResponses = Stream.concat(univBestPosts.stream(), totalBestPosts.stream())
                .collect(Collectors.toList());
        List<HomePostResponse> sortedHomePostResponses = homePostResponses.stream().sorted(Comparator.comparing(HomePostResponse::getCreatedAtDateTime)
                .reversed()
        ).collect(Collectors.toList());

        return sortedHomePostResponses;
    }

}
