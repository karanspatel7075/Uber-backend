package com.example.Navio.service;

import com.example.Navio.dto.RideRequestDto;
import com.example.Navio.interfaces.NotificationService;
import com.example.Navio.model.Driver;
import com.example.Navio.model.Ride;
import com.example.Navio.model.User;
import com.example.Navio.repository.DriverRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Service
public class NotificationServiceImple implements NotificationService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }

    @Override
    public void sendConfirmationMail(Driver driver, RideRequestDto dto, User user, double fare) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

        messageHelper.setFrom("ffg19162@gmail.com", "Uber Application");
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject("🚗 Ride Confirmation - Navio Rides");

        String msg = """
<html>
  <body style="font-family: Arial, sans-serif; color: #333;">
    <h2 style="color: #2C7BE5;">🚗 Ride Confirmation</h2>
    <p>Hi <b>[name]</b>,</p>
    <p>Your ride has been <b>successfully confirmed!</b></p>
    
    <h3>🧭 Ride Details:</h3>
    <table style="border-collapse: collapse; margin-top: 10px;">
      <tr>
        <td style="padding: 5px 10px;"><b>Pickup Location:</b></td>
        <td style="padding: 5px 10px;">[pickup]</td>
      </tr>
      <tr>
        <td style="padding: 5px 10px;"><b>Drop Location:</b></td>
        <td style="padding: 5px 10px;">[drop]</td>
      </tr>
      <tr>
        <td style="padding: 5px 10px;"><b>Fare:</b></td>
        <td style="padding: 5px 10px;">₹[fare]</td>
      </tr>
      <tr>
        <td style="padding: 5px 10px;"><b>Driver Name:</b></td>
        <td style="padding: 5px 10px;">[driver]</td>
      </tr>
      <tr>
        <td style="padding: 5px 10px;"><b>Contact:</b></td>
        <td style="padding: 5px 10px;">[driverContact]</td>
      </tr>
    </table>

    <p style="margin-top: 20px;">
      Please be ready at your pickup location on time.
      For any issues, contact our support team.
    </p>

    <p style="margin-top: 30px;">Thank you for choosing <b>Navio Rides</b>!<br>
    <i>Have a safe and pleasant journey.</i></p>

    <hr style="margin-top: 30px;">
    <p style="font-size: 12px; color: gray;">
      This is an automated message. Please do not reply to this email.
    </p>
  </body>
</html>
""";

        msg = msg.replace("[name]", user.getName());
        msg = msg.replace("[pickup]", dto.getPickUpLocation());
        msg = msg.replace("[drop]", dto.getDropLocation());
        msg = msg.replace("[fare]", String.valueOf(fare));
        msg = msg.replace("[driver]", driver.getName());
        msg = msg.replace("[driverContact]", String.valueOf(driver.getPhoneNumber()));

        messageHelper.setText(msg, true);
        javaMailSender.send(message);
    }

    @Override
    public void sendSMS(Long phoneNumber, String message) {

    }
}
