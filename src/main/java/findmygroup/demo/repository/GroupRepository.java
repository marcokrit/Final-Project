package findmygroup.demo.repository;

import findmygroup.demo.model.ProjectGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupRepository extends JpaRepository<ProjectGroup, Long> {
    // หาว่า User คนนี้เป็นเจ้าของกลุ่มไหนบ้าง
    List<ProjectGroup> findByOwnerId(Long ownerId);
    List<ProjectGroup> findBySubjectAndUniversity(String subject, String university);
    List<ProjectGroup> findBySubjectAndUniversityAndSection(String subject, String university, String section);
    @Query("SELECT DISTINCT p.section FROM ProjectGroup p WHERE p.subject = :subject AND p.university = :university ORDER BY p.section ASC")
    List<String> findDistinctSections(@Param("subject") String subject, @Param("university") String university);
}