package community.mingle.app.src.item.model;

import community.mingle.app.src.domain.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class CreateItemRequest {

    private String title;
    private Long price;
    private Currency currency;
    private String content;
    private String location;
    private String chatUrl;
    private Boolean isAnonymous;
    private List<MultipartFile> multipartFile;

}
