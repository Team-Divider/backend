package divider.backend.domain.completedate.repository;

import divider.backend.domain.completedate.entity.CompleteDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompleteDateRepository extends JpaRepository<CompleteDate, Long> {
}
