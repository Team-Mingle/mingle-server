package community.mingle.app.src.post;

import community.mingle.app.src.post.model.NotifiedContentRequest;
import community.mingle.app.src.post.model.NotifiedContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BackOfficeController {

    private final PostService postService;

    /**
     * 신고 백오피스 리스트 api
     */

    @RequestMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/notified-totalpost")
    public String notifiedTotalPosts(@ModelAttribute("notifiedTotalPostForm") NotifiedContentRequest notifiedContentRequest, Model model) {
        List<NotifiedContentResponse> listNotifiedTotalPost = postService.listNotifiedTotalPost();
        model.addAttribute("listNotifiedTotalPost", listNotifiedTotalPost);
        return "notifiedTotalPostList";
    }

    @GetMapping("/notified-univpost")
    public String notifiedUnivPosts(@ModelAttribute("notifiedUnivPostForm") NotifiedContentRequest notifiedContentRequest, Model model) {
        List<NotifiedContentResponse> listNotifiedUnivPost = postService.listNotifiedUnivPost();
        model.addAttribute("listNotifiedUnivPost", listNotifiedUnivPost);
        return "notifiedUnivPostList";
    }

    @GetMapping("/notified-totalcomment")
    public String notifiedTotalComments(@ModelAttribute("notifiedTotalCommentForm") NotifiedContentRequest notifiedContentRequest, Model model) {
        List<NotifiedContentResponse> listNotifiedTotalComment = postService.listNotifiedTotalComment();
        model.addAttribute("listNotifiedTotalComment", listNotifiedTotalComment);
        return "notifiedTotalCommentList";
    }

    @GetMapping("/notified-univcomment")
    public String notifiedUnivComments(@ModelAttribute("notifiedUnivCommentForm") NotifiedContentRequest notifiedContentRequest, Model model) {
        List<NotifiedContentResponse> listNotifiedUnivComment = postService.listNotifiedUnivComment();
        model.addAttribute("listNotifiedUnivComment", listNotifiedUnivComment);
        return "notifiedUnivCommentList";
    }

    /**
     * 신고 execute api
     */
    @PatchMapping("/report-totalpost")
    public String executeTotalPost(@RequestParam String contentId, @ModelAttribute("notifiedTotalPostForm") NotifiedContentRequest notifiedContentRequest, Model model) {
        postService.executeTotalPost(contentId);
        List<NotifiedContentResponse> listNotifiedTotalPost = postService.listNotifiedTotalPost();
        model.addAttribute("listNotifiedTotalPost", listNotifiedTotalPost);
        return "notifiedTotalPostList";
    }

    @PatchMapping("/report-univpost")
    public String executeUnivPost(@RequestParam String contentId, @ModelAttribute("notifiedTotalPostForm") NotifiedContentRequest notifiedContentRequest, Model model) {
        postService.executeUnivPost(contentId);
        List<NotifiedContentResponse> listNotifiedUnivPost = postService.listNotifiedUnivPost();
        model.addAttribute("listNotifiedTotalPost", listNotifiedUnivPost);
        return "notifiedTotalPostList";
    }

    @PatchMapping("/report-totalcomment")
    public String executeTotalComment(@RequestParam String contentId, @ModelAttribute("notifiedTotalPostForm") NotifiedContentRequest notifiedContentRequest, Model model) {
        postService.executeTotalComment(contentId);
        List<NotifiedContentResponse> listNotifiedTotalComment = postService.listNotifiedTotalComment();
        model.addAttribute("listNotifiedTotalPost", listNotifiedTotalComment);
        return "notifiedTotalPostList";
    }

    @PatchMapping("/report-univcomment")
    public String executeUnivComment(@RequestParam String contentId, @ModelAttribute("notifiedTotalPostForm") NotifiedContentRequest notifiedContentRequest, Model model) {
        postService.executeUnivComment(contentId);
        List<NotifiedContentResponse> listNotifiedUnivComment = postService.listNotifiedUnivComment();
        model.addAttribute("listNotifiedTotalPost", listNotifiedUnivComment);
        return "notifiedTotalPostList";
    }
}