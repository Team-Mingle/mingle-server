package community.mingle.app.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@AllArgsConstructor
@Getter
public class CreatePostResponse {
    private Long id;
    private List<String> fileNameList;
}
