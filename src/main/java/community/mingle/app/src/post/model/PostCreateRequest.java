package community.mingle.app.src.post.model;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostCreateRequest {

    private int categoryId;
    private String title;
    private String content;
    private Boolean isAnonymous;
    private Boolean isFileAttached;
    private List<MultipartFile> multipartFile;

}
