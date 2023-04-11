package community.mingle.app.src.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter @AllArgsConstructor
public class ModifyItemPostRequest {
    private String title;
    private String content;
    private Long price;
    private String location;
    private String chatUrl;

    private List<String> itemImageUrlsToDelete;
    private List<MultipartFile> itemImageUrlsToAdd;
}
