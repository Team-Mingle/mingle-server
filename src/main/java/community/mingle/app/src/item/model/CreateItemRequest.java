package community.mingle.app.src.item.model;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Getter
@AllArgsConstructor
public class CreateItemRequest {

    private String title;
    private Long price;
    private String content;
    private String location;
    private String chatUrl;
    private Boolean isAnonymous;
    private List<MultipartFile> multipartFile;

}
