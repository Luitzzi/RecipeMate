package de.example.backend.boundary.controller;

import de.example.backend.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestEmailUiController {

    private final EmailService emailService;

    public TestEmailUiController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("send-test-email")
    public ResponseEntity<String> sendTestEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String message) {
        emailService.sendTestMail(to, subject, message);

        return ResponseEntity.ok("E-Mail wurde erfolgreich an den Mailpit-Server gesendet!");
    }
}
