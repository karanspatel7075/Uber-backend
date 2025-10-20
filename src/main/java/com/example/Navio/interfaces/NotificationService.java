package com.example.Navio.interfaces;

import com.example.Navio.dto.RideRequestDto;
import com.example.Navio.model.Driver;
import com.example.Navio.model.User;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface NotificationService {
    //
    public void sendEmail(String to, String subject, String body);
    public void sendConfirmationMail(Driver driver, RideRequestDto dto, User user, double fare) throws MessagingException, UnsupportedEncodingException;
    public void sendSMS(Long phoneNumber, String message);
}
