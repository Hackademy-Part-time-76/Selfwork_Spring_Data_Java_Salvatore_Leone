package selfwork_spring_data.repositories;

import selfwork_spring_data.models.Comment;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long> {}
