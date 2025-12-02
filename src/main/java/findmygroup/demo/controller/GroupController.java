package findmygroup.demo.controller;

import findmygroup.demo.model.GroupMember;
import findmygroup.demo.model.ProjectGroup;
import findmygroup.demo.model.User;
import findmygroup.demo.repository.GroupMemberRepository;
import findmygroup.demo.repository.GroupRepository;
import findmygroup.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class GroupController {

    @Autowired private GroupRepository groupRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private GroupMemberRepository groupMemberRepository;

    // หน้าค้นหา (หน้าแรก)
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // รับค่าค้นหา
    @GetMapping("/search-subject")
    public String searchSubject(@RequestParam("query") String query, Principal principal) {
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username);
        return "redirect:/subject/" + query.toUpperCase() + "?university=" + currentUser.getUniversity();
    }

    // หน้า Dashboard รายวิชา
    @GetMapping("/subject/{subjectName}")
    public String viewSubject(@PathVariable String subjectName,
                              @RequestParam(name = "university") String university,
                              @RequestParam(required = false) String section,
                              Model model) {

        List<ProjectGroup> allGroups;
        if (section != null && !section.isEmpty()) {
            allGroups = groupRepository.findBySubjectAndUniversityAndSection(subjectName, university, section);
            model.addAttribute("selectedSection", section);
        } else {
            allGroups = groupRepository.findBySubjectAndUniversity(subjectName, university);
        }

        List<String> allSections = groupRepository.findDistinctSections(subjectName, university);
        model.addAttribute("allSections", allSections);

        List<ProjectGroup> availableGroups = new ArrayList<>();
        for (ProjectGroup group : allGroups) {
            int memberCount = groupMemberRepository.countByGroupIdAndStatus(group.getId(), "APPROVED");
            if (group.getMaxMembers() == null || (memberCount + 1) < group.getMaxMembers()) {
                availableGroups.add(group);
            }
        }

        model.addAttribute("groups", availableGroups);
        model.addAttribute("subjectName", subjectName);
        model.addAttribute("university", university);
        return "subject-view";
    }

    // หน้าสร้างกลุ่ม
    @GetMapping("/create-group")
    public String showCreateGroupForm(Model model,
                                      @RequestParam(required = false) String preSelectSubject,
                                      @RequestParam(required = false) String university) {
        ProjectGroup group = new ProjectGroup();
        if(preSelectSubject != null) group.setSubject(preSelectSubject);
        if(university != null) group.setUniversity(university);

        model.addAttribute("group", group);
        model.addAttribute("selectedSubject", preSelectSubject);
        model.addAttribute("university", university);
        return "create-group";
    }

    // บันทึกกลุ่ม + แจ้งเตือน
    @PostMapping("/create-group")
    public String createGroup(@ModelAttribute ProjectGroup group, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        User owner = userRepository.findByUsername(username);
        group.setOwner(owner);
        groupRepository.save(group);

        // ส่งข้อความแจ้งเตือนสีเขียว
        redirectAttributes.addFlashAttribute("successMessage", "✅ สร้างกลุ่ม " + group.getProjectName() + " สำเร็จ!");
        return "redirect:/subject/" + group.getSubject() + "?university=" + group.getUniversity();
    }

    // หน้า My Groups
    @GetMapping("/my-groups")
    public String myGroups(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username);

        List<ProjectGroup> myOwned = groupRepository.findByOwnerId(user.getId());

        List<GroupMember> joinedMembers = groupMemberRepository.findByUserIdAndStatus(user.getId(), "APPROVED");
        List<ProjectGroup> myJoined = new ArrayList<>();
        for (GroupMember m : joinedMembers) {
            myJoined.add(m.getGroup());
        }

        model.addAttribute("ownedGroups", myOwned);
        model.addAttribute("joinedGroups", myJoined);
        return "my-groups";
    }

    // Join Group + แจ้งเตือน
    @PostMapping("/join/{groupId}")
    public String joinGroup(@PathVariable Long groupId, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        ProjectGroup group = groupRepository.findById(groupId).orElse(null);

        if (group != null) {
            boolean alreadyJoined = groupMemberRepository.existsByGroupIdAndUserId(groupId, user.getId());
            if (!alreadyJoined && !group.getOwner().getUsername().equals(username)) {
                GroupMember membership = new GroupMember();
                membership.setGroup(group);
                membership.setUser(user);
                membership.setStatus("PENDING");
                groupMemberRepository.save(membership);

                redirectAttributes.addFlashAttribute("successMessage", "ส่งคำขอเข้าร่วมกลุ่มเรียบร้อย รอหัวหน้าอนุมัตินะ!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "คุณอยู่ในกลุ่มนี้อยู่แล้ว หรือเป็นเจ้าของกลุ่ม");
            }
            return "redirect:/subject/" + group.getSubject() + "?university=" + group.getUniversity();
        }
        return "redirect:/";
    }

    // จัดการคำขอ
    @GetMapping("/my-requests")
    public String showMyRequests(Model model, Principal principal) {
        String username = principal.getName();
        User owner = userRepository.findByUsername(username);

        List<ProjectGroup> myGroups = groupRepository.findByOwnerId(owner.getId());

        List<GroupMember> pendingRequests = new ArrayList<>();
        List<GroupMember> currentMembers = new ArrayList<>(); // [เพิ่ม] ลิสต์สมาชิกปัจจุบัน

        for (ProjectGroup group : myGroups) {
            List<GroupMember> members = groupMemberRepository.findByGroupId(group.getId());
            for(GroupMember m : members) {
                if("PENDING".equals(m.getStatus())) {
                    pendingRequests.add(m);
                } else if("APPROVED".equals(m.getStatus())) {
                    currentMembers.add(m); // [เพิ่ม] เก็บคนที่เป็นสมาชิกแล้ว
                }
            }
        }

        model.addAttribute("requests", pendingRequests);
        model.addAttribute("members", currentMembers); // ส่งไปหน้าเว็บ
        return "requests";
    }

    // อนุมัติ
    @PostMapping("/approve/{memberId}")
    public String approveMember(@PathVariable Long memberId, RedirectAttributes redirectAttributes) {
        GroupMember member = groupMemberRepository.findById(memberId).orElse(null);
        if (member != null) {
            member.setStatus("APPROVED");
            groupMemberRepository.save(member);
            redirectAttributes.addFlashAttribute("successMessage", "รับ " + member.getUser().getName() + " เข้ากลุ่มแล้ว!");
        }
        return "redirect:/my-requests";
    }

    // ปฏิเสธ
    @PostMapping("/deny/{memberId}")
    public String denyMember(@PathVariable Long memberId, RedirectAttributes redirectAttributes) {
        groupMemberRepository.deleteById(memberId);
        redirectAttributes.addFlashAttribute("errorMessage", "ปฏิเสธคำขอเรียบร้อย");
        return "redirect:/my-requests";
    }

    // เตะสมาชิก (Kick)
    @PostMapping("/kick/{memberId}")
    public String kickMember(@PathVariable Long memberId, RedirectAttributes redirectAttributes) {
        GroupMember member = groupMemberRepository.findById(memberId).orElse(null);
        if (member != null) {
            String kickedName = member.getUser().getName();
            groupMemberRepository.delete(member); // ลบออกเหมือน Deny
            redirectAttributes.addFlashAttribute("errorMessage", "เชิญคุณ " + kickedName + " ออกจากกลุ่มเรียบร้อย");
        }
        return "redirect:/my-requests";
    }

    // Edit Group
    @GetMapping("/edit-group/{groupId}")
    public String showEditGroup(@PathVariable Long groupId, Model model, Principal principal) {
        String username = principal.getName();
        ProjectGroup group = groupRepository.findById(groupId).orElse(null);
        if (group != null && group.getOwner().getUsername().equals(username)) {
            model.addAttribute("group", group);
            return "edit-group";
        }
        return "redirect:/my-groups";
    }

    @PostMapping("/edit-group/{groupId}")
    public String updateGroup(@PathVariable Long groupId, @ModelAttribute ProjectGroup groupData, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        ProjectGroup existingGroup = groupRepository.findById(groupId).orElse(null);
        if (existingGroup != null && existingGroup.getOwner().getUsername().equals(username)) {
            existingGroup.setProjectName(groupData.getProjectName());
            existingGroup.setDescription(groupData.getDescription());
            existingGroup.setContact(groupData.getContact());
            existingGroup.setMaxMembers(groupData.getMaxMembers());
            existingGroup.setSection(groupData.getSection());
            groupRepository.save(existingGroup);
            redirectAttributes.addFlashAttribute("successMessage", "แก้ไขข้อมูลกลุ่มเรียบร้อย!");
        }
        return "redirect:/my-groups";
    }

    // ลบกลุ่ม + แจ้งเตือน
    @PostMapping("/delete-group/{groupId}")
    public String deleteGroup(@PathVariable Long groupId, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        ProjectGroup group = groupRepository.findById(groupId).orElse(null);
        if (group != null && group.getOwner().getUsername().equals(username)) {
            groupMemberRepository.deleteByGroupId(groupId);
            groupRepository.delete(group);
            redirectAttributes.addFlashAttribute("errorMessage", "ลบกลุ่ม " + group.getProjectName() + " แล้ว");
        }
        return "redirect:/my-groups";
    }
}