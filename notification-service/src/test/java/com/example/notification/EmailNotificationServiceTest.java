package com.example.notification;
/*
import com.example.notification.controller.EmailController;
import com.example.notification.service.EmailNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mail.javamail.JavaMailSender;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class) // Только контроллер, без полного контекста
public class EmailNotificationServiceTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private EmailNotificationService emailService;

    @MockBean
    private JavaMailSender mailSender; // Мок для почтового отправителя

    @Test
    void testSendEmail() throws Exception {
        this.mockMvc.perform(post("/api/email")
                        .param("email", "test@example.com")
                        .param("operation", "CREATE"))
                .andExpect(status().isOk());
    }
}
*/