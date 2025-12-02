package findmygroup.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "group_members")
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // เชื่อมกับ ProjectGroup
    @ManyToOne
    @JoinColumn(name = "group_id")
    private ProjectGroup group;

    // เชื่อมกับ User (สมาชิก)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String status; // PENDING, APPROVED
}