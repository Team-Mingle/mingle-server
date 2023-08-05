package community.mingle.app.src.item.model;

import community.mingle.app.src.domain.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class ModifyItemPostRequest {
    private String title;
    private String content;
    private Long price;
    private Currency currency;
    private String location;
    private String chatUrl;
    private boolean isAnonymous;

    private List<String> itemImageUrlsToDelete;
    private List<MultipartFile> itemImagesToAdd;
}
