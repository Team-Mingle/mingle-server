package community.mingle.app.src.home.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@AllArgsConstructor
@Getter
public class CreateBannerResponse {
    private int id;
    private List<String> fileNameList;
}
