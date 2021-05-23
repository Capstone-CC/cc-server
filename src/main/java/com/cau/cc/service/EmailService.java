package com.cau.cc.service;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class EmailService  {


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private AccountRepository accountRepository;


    /**
     * 모든 account 조회
     */
    public List<Account> listAll() {
        return accountRepository.findAll();
    }

    /**
     * 실질적으로 이메일을 발송시키는 메소드
     * 인증코드를 리턴
     */
    public String sendVerificationEmail(String email)
            throws MessagingException, UnsupportedEncodingException {

        StringBuffer fullEmail = new StringBuffer(email);
        fullEmail.append("@cau.ac.kr");

        String fromAddress = "yohoee770"; //발신자 이메일
        String senderName = "CauConnect"; //발신자 이름
        String subject = "Please verify your registration"; // 메일 제목
        String content = "Dear [[name]],<br>" //메일내용
                + "Please input the Code below to verify your registration:<br>"
                + "<h3>Code = [[code]]</h3>"
                + "Thank you,<br>"
                + "CauConnect";

        // 메일 보내기위해 필요한 객체
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,"utf-8");


        // 메일 발신자 정보(주소,이름)와 수신자메일주소, 메일제목 담기
        helper.setFrom(fromAddress, senderName);
        helper.setTo(fullEmail.toString());
        helper.setSubject(subject);

        //랜덤코드
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        int num = 0;

        while(buffer.length() < 6) {
            num = random.nextInt(10);
            buffer.append(num);
        }
        String randomCode = buffer.toString();

        // html 내용 replace
        content = content.replace("[[name]]", fullEmail.toString());
        content = content.replace("[[code]]", randomCode);

        //본문 담기, true는 html 형식으로 보내겠다는 의미
        helper.setText(content, true);

        //메일 발송
        mailSender.send(message);

        System.out.println("Email has been sent");

        return randomCode;
    }


    /**
     * 임시 비밀번호 만들어서 이메일에 발송
     */
    public String sendVerificationPw(String email)
            throws MessagingException, UnsupportedEncodingException {

        StringBuffer fullEmail = new StringBuffer(email);
        fullEmail.append("@cau.ac.kr");

        String fromAddress = "yohoee770"; //발신자 이메일
        String senderName = "CauConnect"; //발신자 이름
        String subject = "Please verify your Temporary Password"; // 메일 제목
        String content = "Dear [[name]],<br>" //메일내용
                + "Please input the code below to verify your temporary password<br>"
                + "<h3>임시비밀번호 = [[code]]</h3>"
                + "Thank you,<br>"
                + "CauConnect";

        // 메일 보내기위해 필요한 객체
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,"utf-8");

        // 메일 발신자 정보(주소,이름)와 수신자메일주소, 메일제목 담기
        helper.setFrom(fromAddress, senderName);
        helper.setTo(fullEmail.toString());
        helper.setSubject(subject);

        //10 글자의 임시비밀번호 생성
        String temporaryPw = getRamdomPassword(10);

        // html 내용 replace
        content = content.replace("[[name]]", fullEmail.toString());
        content = content.replace("[[code]]", temporaryPw);

        //본문 담기, true는 html 형식으로 보내겠다는 의미
        helper.setText(content, true);

        //메일 발송
        mailSender.send(message);

        System.out.println("Email has been sent");

        return temporaryPw;
    }


    private String getRamdomPassword(int size) {
        char[] charSet = new char[]{
                '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
                'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
                'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
                'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                'u', 'v', 'w', 'x', 'y', 'z'};
        StringBuffer sb = new StringBuffer();
        SecureRandom sr = new SecureRandom();
        sr.setSeed(new Date().getTime());
        int idx = 0;
        int len = charSet.length;
        for (int i = 0; i < size; i++) {
            // idx = (int) (len * Math.random());
            idx = sr.nextInt(len); // 강력한 난수를 발생시키기 위해 SecureRandom을 사용한다.
            sb.append(charSet[idx]);
        }
        return sb.toString();
    }
}
