package com.hung_gamingshop.controller.admin;

import com.hung_gamingshop.service.ContactMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "com.hung_gamingshop.controller.admin")
public class AdminModelAdvice {

    @Autowired private ContactMessageService contactMessageService;

    @ModelAttribute("unreadMessageCount")
    public long unreadMessageCount() {
        return contactMessageService.countUnread();
    }
}
