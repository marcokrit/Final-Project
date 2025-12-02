package findmygroup.demo.controller;

import findmygroup.demo.model.User;
import findmygroup.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ForgotPasswordController {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    // รับค่าและเปลี่ยนรหัส
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("username") String username,
                                        @RequestParam("name") String name, // ใช้ชื่อจริงยืนยันตัวตน
                                        @RequestParam("newPassword") String newPassword,
                                        RedirectAttributes redirectAttributes) {

        // ค้นหา User จาก Username
        User user = userRepository.findByUsername(username);

        // เช็คว่า User มีจริงไหม AND ชื่อตรงกับที่กรอกมาไหม
        if (user != null && user.getName().equals(name)) {
            // ถ้าตรงกัน -> เปลี่ยนรหัสได้
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            redirectAttributes.addFlashAttribute("successMessage", "✅ เปลี่ยนรหัสผ่านสำเร็จ! กรุณาล็อกอินใหม่");
            return "redirect:/login";
        } else {
            // ถ้าไม่ตรง -> แจ้งเตือน
            redirectAttributes.addFlashAttribute("errorMessage", "❌ ข้อมูลไม่ถูกต้อง (Username หรือ ชื่อไม่ตรงกับในระบบ)");
            return "redirect:/forgot-password";
        }
    }
}