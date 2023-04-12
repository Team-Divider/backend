package divider.backend.domain.memberrole.repository;

import divider.backend.domain.memberrole.entity.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRoleRepository extends JpaRepository<MemberRole, Long> {
}
