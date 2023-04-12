package divider.backend.domain.groupmember.repository;

import divider.backend.domain.groupmember.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
}
