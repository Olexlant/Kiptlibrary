package com.Kipfk.Library.registration;

import com.Kipfk.Library.appuser.*;
import com.Kipfk.Library.email.EmailSender;
import com.Kipfk.Library.registration.token.ConfirmationToken;
import com.Kipfk.Library.registration.token.ConfirmationTokenRepository;
import com.Kipfk.Library.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;
    private final AppUserRepository appUserRepository;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    public String register(AppUser user, String host) {
        boolean isValidEmail = emailValidator.test(user.getEmail());
        if(!isValidEmail) {
            throw new IllegalStateException("email is not valid");
        }
        String token = appUserService.signUpUser(
                new AppUser(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPhonenum(),
                        user.getPassword(),
                        user.getEmail(),
                        user.getGroups(),
                        AppUserRole.USER
                )
        );
        String link = "https://" + host + "/registration/confirm?token=" + token;
        emailSender.sendregistrationmail(user, link);
        return token;
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token);
        if (confirmationToken==null){
            return "redirect:/register?notfound";
        }
        if (confirmationToken.getConfirmedAt() != null) {
            return "redirect:/login?alreadyconfirmed";
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now()) && confirmationToken.getConfirmedAt() == null) {
            confirmationTokenRepository.deleteById(confirmationToken.getId());
            appUserRepository.deleteAllById(confirmationToken.getAppUser().getId());
            return "redirect:/register?expired";
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(confirmationToken.getAppUser().getEmail());
        return "redirect:/confirmsuccess";
    }

    public void sendchangepasswordmail (AppUser user, String token, String host){
        String link = "https://" + host + "/resetpassword/reset?token=" + token;
        emailSender.sendchangepasswordmail(user, link);
    }


    @Transactional
    public String changePasswordByToken(String newpassword, String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token);
        if (confirmationToken==null){
            return "redirect:/register?notfound";
        }
        confirmationToken.setPasswordChangeAt(LocalDateTime.now());
        String newtoken = UUID.randomUUID().toString();
        confirmationToken.setToken(newtoken);
        LocalDateTime expiredAt = confirmationToken.getPasswordChangeExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            return "redirect:/login?expiredpasschange";
        }
        AppUser user = confirmationToken.getAppUser();
        BCryptPasswordEncoder bcpe = new BCryptPasswordEncoder();
        user.setPassword(bcpe.encode(newpassword));
        appUserRepository.save(user);
        confirmationTokenRepository.save(confirmationToken);
        return "redirect:/login?passwordchanged";

    }
}

