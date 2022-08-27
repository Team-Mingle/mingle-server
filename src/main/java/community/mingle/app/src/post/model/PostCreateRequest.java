package community.mingle.app.src.post.model;

import community.mingle.app.src.auth.AuthRepository;
import community.mingle.app.src.domain.Category;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.UnivName;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Data
public class PostCreateRequest {

    private int categoryId;
    private String title;
    private String content;
    private Boolean isAnonymous;
    private Boolean isFileAttached;
    private List<MultipartFile> multipartFile;

}
