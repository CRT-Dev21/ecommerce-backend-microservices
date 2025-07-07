package com.example.email_service.domain.service;

import com.example.basedomains.dto.request.ItemOrderRequest;
import com.example.email_service.domain.exceptions.EmailSendingException;
import com.example.email_service.model.Customer;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

// Use only when sending real emails
//    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

// These properties are injected from application.properties, but since this is for testing purposes,
// it is commented out since it is not used. If you want to test sending real emails, see the comments below before using this.
//
//    @Value("${spring.mail.username}")
//    private String fromEmail;
//
//    @Value("${spring.mail.display-name:MyApp}")
//    private String displayName;

    public void sendOrderConfirmation(Customer customer, Long orderId, List<ItemOrderRequest> items, double total) {
        try {
            String subject = "Order confirmation #" + orderId;
            String content = buildOrderConfirmationContent(customer.getName(), orderId, items, total);
            sendEmail(customer.getEmail(), subject, content);
            log.info("Order confirmation sent to {}", customer.getEmail());
        } catch (Exception ex) {
            log.error("Failed to send order confirmation to {}", customer.getEmail(), ex);
        }
    }

// All of this commented code works to send real emails, but you need a real email address to send them,
// and you need to use an application password that you can generate from your sender email in application.properties.
// You can then uncomment this code and comment out or delete the two methods below, which are used to simulate sending emails.

//    public void sendEmail(String to, String subject, String content) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            helper.setFrom(new InternetAddress(fromEmail, displayName));
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(content, true);
//
//            mailSender.send(message);
//            log.debug("Email sent to {}", to);
//        } catch (Exception ex) {
//            log.error("Error sending email to {}", to, ex);
//            throw new EmailSendingException("Failed to send email", ex);
//        }
//    }
//
//    private String buildOrderConfirmationContent(String name, Long orderId, List<ItemOrderRequest> items, double total) {
//        Context context = new Context();
//        context.setVariable("name", name);
//        context.setVariable("orderId", orderId);
//        context.setVariable("items", items);
//        context.setVariable("total", total);
//        return templateEngine.process("order-confirmation", context);
//    }

    public void sendEmail(String to, String subject, String content) {
        try {
            log.debug("Email sent to {}", to);
        } catch (Exception ex) {
            log.error("Error sending email to {}", to, ex);
            throw new EmailSendingException("Failed to send email", ex);
        }
    }

    private String buildOrderConfirmationContent(String name, Long orderId, List<ItemOrderRequest> items, double total) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("orderId", orderId);
        context.setVariable("items", items);
        context.setVariable("total", total);
        return templateEngine.process("order-confirmation", context);
    }
}
