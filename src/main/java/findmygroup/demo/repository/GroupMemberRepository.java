package findmygroup.demo.repository;

import findmygroup.demo.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    // หาคำขอเข้ากลุ่ม จาก ID ของกลุ่ม
    List<GroupMember> findByGroupId(Long groupId);
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);
    List<GroupMember> findByUserIdAndStatus(Long userId, String status);
    int countByGroupIdAndStatus(Long groupId, String status);

    @Transactional
    void deleteByGroupId(Long groupId);
}