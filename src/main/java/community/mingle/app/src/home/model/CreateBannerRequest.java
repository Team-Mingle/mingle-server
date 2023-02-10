package community.mingle.app.src.home.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@Getter
public class CreateBannerRequest {
    private List<MultipartFile> multipartFile;
    private String link;
}
