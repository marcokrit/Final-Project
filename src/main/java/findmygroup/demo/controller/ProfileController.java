package findmygroup.demo.controller;

import findmygroup.demo.model.User;
import findmygroup.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    // เปิดหน้าโปรไฟล์
    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        return "profile";
    }

    // บันทึกการแก้ไข
    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute User userFormData, Principal principal) {
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username);

        // อัปเดตข้อมูล
        currentUser.setName(userFormData.getName());
        currentUser.setFaculty(userFormData.getFaculty());
        currentUser.setSkills(userFormData.getSkills());

        userRepository.save(currentUser);
        return "redirect:/profile?success";
    }
}