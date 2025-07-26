package com.adx.agent.service;


import com.adx.agent.model.vo.EmailVO;

public interface EmailService {

    /**
     * Sends a verification code to the specified email address.
     *
     * @param receiveEmail The recipient's email address.
     * @return true if the email was sent successfully, false otherwise.
     */
    boolean sendVerificationCode(String receiveEmail, EmailVO emilVO);

    boolean checkVerificationCode(String receiveEmail, String code);
    
}
