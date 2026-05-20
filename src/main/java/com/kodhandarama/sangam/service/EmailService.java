package com.kodhandarama.sangam.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String adminEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ── OTP Email ─────────────────────────────────────────────────

    public void sendOtp(String toEmail, String otp) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("Kodhandarama Sangam - OTP Verification");
        msg.setText(
            "Dear Member,\n\n" +
            "Your OTP for registration is: " + otp + "\n\n" +
            "This OTP is valid for 5 minutes.\n\n" +
            "Jai Sri Rama!\n" +
            "Kodhandarama Sangam Team"
        );
        mailSender.send(msg);
    }

    // ── Approval Notification ─────────────────────────────────────

    public void sendApprovalNotification(String toEmail, String memberName) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("Kodhandarama Sangam - Registration Approved!");
        msg.setText(
            "Dear " + memberName + ",\n\n" +
            "Congratulations! Your membership has been approved by the admin.\n" +
            "You can now login using your phone number and password.\n\n" +
            "Jai Sri Rama!\n" +
            "Kodhandarama Sangam Team"
        );
        mailSender.send(msg);
    }

    // ── Rejection Notification ────────────────────────────────────

    public void sendRejectionNotification(String toEmail, String memberName) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("Kodhandarama Sangam - Registration Update");
        msg.setText(
            "Dear " + memberName + ",\n\n" +
            "We regret to inform you that your membership request has not been approved.\n" +
            "Please contact the admin for more information.\n\n" +
            "Kodhandarama Sangam Team"
        );
        mailSender.send(msg);
    }

    // ── Announcement Email ────────────────────────────────────────

    public void sendAnnouncementEmail(String toEmail, String memberName,
                                      String title, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("Kodhandarama Sangam - " + title);
        msg.setText(
            "Dear " + memberName + ",\n\n" +
            body + "\n\n" +
            "Jai Sri Rama!\n" +
            "Kodhandarama Sangam Team"
        );
        mailSender.send(msg);
    }

    // ── Contact Message to Admin ──────────────────────────────────

    public void sendContactMessageToAdmin(String memberName, String memberEmail,
                                          String memberPhone, String message) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(adminEmail);
        msg.setSubject("Kodhandarama Sangam - Contact Message from " + memberName);
        msg.setText(
            "New contact message received from a member:\n\n" +
            "------------------------------\n" +
            "Name    : " + memberName  + "\n" +
            "Email   : " + (memberEmail.isBlank() ? "Not provided" : memberEmail) + "\n" +
            "Phone   : " + (memberPhone.isBlank() ? "Not provided" : memberPhone) + "\n" +
            "------------------------------\n" +
            "Message :\n" + message + "\n" +
            "------------------------------\n\n" +
            "Please reply to the member at: " + memberEmail + "\n\n" +
            "Kodhandarama Sangam System"
        );
        mailSender.send(msg);
    }
}