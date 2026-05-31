package com.example.notification;

import com.example.UserServiceApplication;
import com.example.notification.controller.EmailController;
import com.example.notification.service.EmailNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = EmailController.class)
@ContextConfiguration( classes = UserServiceApplication.class)
public class EmailNotificationServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailNotificationService emailService;

    @Test
    void testSendEmail() throws Exception {
        this.mockMvc.perform(post("/api/email")
                        .param("email", "test@example.com")
                        .param("operation", "CREATE"))
                .andExpect(status().isOk());
    }
}
