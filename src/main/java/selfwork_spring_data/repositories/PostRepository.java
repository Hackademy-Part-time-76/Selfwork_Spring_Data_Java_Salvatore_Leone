package selfwork_spring_data.repositories;

import selfwork_spring_data.models.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Long> {}
