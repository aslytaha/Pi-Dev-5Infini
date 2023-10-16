package com.example.pidev.Services;

import com.example.pidev.Entities.User;
import com.example.pidev.Entities.UserCode;
import com.example.pidev.Entities.VerificationToken;
import com.example.pidev.Repositories.UserCodeRepository;
import com.example.pidev.Repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class UserCodeService {

     @Autowired
    SmsService smsService;
    @Autowired
    UserCodeRepository userCodeRepository;

    @Autowired
    UserRepository userRepository;

    public UserCode createVerificationCode(User user) {
        String code = generateCode();
        UserCode userCode=new UserCode();
        userCode.setCode(code);
        userCode.setUser(user);
        userCodeRepository.save(userCode);
        user.setVerificationCode(code);
        smsService.SendSMS(String.valueOf(user.getPhone()),code);
        return userCode;
    }
    public static String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // génère un nombre aléatoire entre 100000 et 999999
        String SmsCode=Integer.toString(code);
        return SmsCode; // convertit le nombre en une chaîne de caractères
    }
    public void saveVerificationCode(UserCode userCode) {
        userCodeRepository.save(userCode);
    }


}
