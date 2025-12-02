package findmygroup.demo.model;

import jakarta.persistence.*;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
@Table(name = "project_groups")
public class ProjectGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    private String subject;
    private String section;
    private Integer maxMembers;
    private String contact;
    private String university;
    @OneToMany(mappedBy = "group")
    private List<GroupMember> members;

    // --- Manual Getters/Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public Integer getMaxMembers() { return maxMembers; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }

    public List<GroupMember> getMembers() { return members; }
    public void setMembers(List<GroupMember> members) { this.members = members; }
}