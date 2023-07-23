package community.mingle.app.src.post;

import community.mingle.app.src.domain.Category;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BackOfficeService {

    private final PostRepository postRepository;


    @Transactional
    public void moveCareerToQuestions() {
        Category careerCategory = postRepository.findCategoryById(3);
        Category questionCategory = postRepository.findCategoryById(2);
        List<TotalPost> totalCareerPosts = postRepository.findAllTotalPostsByCategory(careerCategory.getName());
        List<UnivPost> univCareerPosts = postRepository.findAllUnivPostsByCategory(careerCategory.getName());
        totalCareerPosts
                .forEach(totalPost -> {
                            totalPost.changeCategory(questionCategory);
                        }
                );
        univCareerPosts
                .forEach(univPost -> {
                    univPost.changeCategory(questionCategory);
                });
    }

}
