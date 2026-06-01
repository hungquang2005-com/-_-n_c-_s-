package com.hung_gamingshop.service;

import com.hung_gamingshop.model.ContactMessage;
import com.hung_gamingshop.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactMessageService {

    @Autowired private ContactMessageRepository contactMessageRepository;

    public ContactMessage create(String name, String email, String phone, String message) {
        ContactMessage contactMessage = new ContactMessage();
        contactMessage.setName(name);
        contactMessage.setEmail(email);
        contactMessage.setPhone(phone);
        contactMessage.setMessage(message);
        return contactMessageRepository.save(contactMessage);
    }

    public List<ContactMessage> getAllMessages() {
        return contactMessageRepository.findAllByOrderByCreatedAtDesc();
    }

    public long countUnread() {
        return contactMessageRepository.countByReadFalse();
    }

    public void markAsRead(Long id) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin nhắn #" + id));
        message.setRead(true);
        contactMessageRepository.save(message);
    }

    public void delete(Long id) {
        contactMessageRepository.deleteById(id);
    }
}
