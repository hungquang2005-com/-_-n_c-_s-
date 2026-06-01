package com.hung_gamingshop.controller.admin;

import com.hung_gamingshop.service.ContactMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/messages")
public class AdminMessageController {

    @Autowired private ContactMessageService contactMessageService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("messages", contactMessageService.getAllMessages());
        return "admin/message-list";
    }

    @PostMapping("/{id}/read")
    public String markAsRead(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            contactMessageService.markAsRead(id);
            redirectAttributes.addFlashAttribute("message", "Đã đánh dấu tin nhắn #" + id + " là đã đọc.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/messages";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            contactMessageService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Đã xóa tin nhắn #" + id + ".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/messages";
    }
}
