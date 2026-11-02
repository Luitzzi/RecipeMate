package de.example.backend.boundary.controller;

import de.example.backend.boundary.dto.mail.MailDTO;
import de.example.backend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mail")
public class EmailUiController {

    private final EmailService emailService;

    public EmailUiController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<String> sendTestEmail(@RequestBody @Valid MailDTO mailDTO) {
        try {
            emailService.sendTemplateMail(mailDTO.getTo(), mailDTO.getSubject(), "test-mail", Map.of("subject", mailDTO.getSubject(), "body", mailDTO.getBody()));
            return ResponseEntity.ok("E-Mail wurde erfolgreich an den Mailpit-Server gesendet!");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
