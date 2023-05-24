package com.Kipfk.Library.controllers.user;

import com.Kipfk.Library.appuser.*;
import com.Kipfk.Library.registration.RegistrationService;
import com.Kipfk.Library.registration.token.ConfirmationToken;
import com.Kipfk.Library.registration.token.ConfirmationTokenRepository;
import com.Kipfk.Library.security.PasswordEncoder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@Controller
public class UserController {
    private final RegistrationService registrationService;
    private final GroupsRepository groupsRepository;
    private final AppUserService appUserService;
    private final AppUserRepository appUserRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final PasswordEncoder passwordEncoder;

    //REGISTRATION
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        List<Groups> groups = groupsRepository.findAll();
        model.addAttribute("groups", groups);
        model.addAttribute("user", new AppUser());
        return "signup_form";
    }
    @PostMapping("/process_register")
    public String signUp(AppUser user, @RequestParam String groupid) {
        if(groupid.equals("")){
            user.setGroups(null);
        }else{
            user.setGroups(groupsRepository.findAllById(Long.valueOf(groupid)));
        }
        registrationService.register(user);
        return "register_success";
    }
    @GetMapping("/registration/confirm")
    public String confirm(@RequestParam(required=false,name="token") String token) {
        return registrationService.confirmToken(token);
    }
    @GetMapping("/confirmsuccess")
    public String ShowConfirmSuccessForm(){
        return "confirm_success";
    }

//EDIT PROFILE
    @GetMapping("/editprofile")
    public String showEditProfilePage(@AuthenticationPrincipal UserDetails userDetails, Model model){
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        model.addAttribute("groups", groupsRepository.findAll());
        model.addAttribute("user",user);
        return "editprofile";
    }

    @PostMapping("/editprofile/save")
    public String saveProfileChanges(@AuthenticationPrincipal UserDetails userDetails, Model model, MultipartFile imgfile, @RequestParam String firstname, @RequestParam String lastname, @RequestParam String phonenum, @RequestParam String email, @RequestParam String groupid, @RequestParam String birthday, @RequestParam String address ) throws IOException {
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setEmail(email);
        user.setPhonenum(phonenum);
        user.setBirthday(LocalDate.parse(birthday));
        user.setAddress(address);
        if (!imgfile.isEmpty()){
            user.setProfileimage(imgfile.getBytes());
        }
        if (groupid.equals("")){
            user.setGroups(null);
        }else{
            user.setGroups(groupsRepository.findAllById(Long.valueOf(groupid)));
        }
        appUserRepository.save(user);
        model.addAttribute("user",user);
        return "redirect:/editprofile?success";
    }

//RESET PASSWORD
    @GetMapping("/resetpassword")
    public String resetPasswordForm(){
        return "sendresetpasswordmail";
    }

    @PostMapping("/resetpassword")
    public String sendResetPasswordMail(@RequestParam String email){
        if (appUserRepository.findByEmail(email).isPresent()){
            AppUser user = appUserRepository.findByEmail(email).get();
            ConfirmationToken token = confirmationTokenRepository.findByAppUser(user).get();
            token.setPasswordChangeExpiresAt(LocalDateTime.now().plusMinutes(10));
            confirmationTokenRepository.save(token);
            registrationService.sendchangepasswordmail(user, token.getToken());
            return "redirect:/resetpassword?sended";
        }else {
            return "redirect:/resetpassword?notfound";
        }
    }

    @GetMapping("/resetpassword/reset")
    public String resetPasswordFormByMail(Model model, @RequestParam(required=false,name="token") String token) {
        model.addAttribute("token", token);
        return "resetpassword";
    }
    @PostMapping("/resetpassword/reset")
    public String changePasswordByMail(@RequestParam String newpassword, @RequestParam String confirmnewpassword, @RequestParam(required=false,name="token") String token) {
        if (newpassword.equals(confirmnewpassword)){
            return registrationService.changePasswordByToken(newpassword,token);
        }else {
            return "redirect:/resetpassword/reset?token="+token+"&notmatch";
        }
    }

    //CHANGE PASSWORD IN PROFILE
    @PostMapping("/editprofile/changepassword")
    public String changePasswordInProfile(@AuthenticationPrincipal UserDetails userDetails,@RequestParam String currentpassword,@RequestParam String newpassword, @RequestParam String confirmnewpassword) {
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        if (passwordEncoder.bCryptPasswordEncoder().matches(currentpassword, user.getPassword())){
            if (newpassword.equals(confirmnewpassword)){
                user.setPassword(passwordEncoder.bCryptPasswordEncoder().encode(newpassword));
                appUserRepository.save(user);
                return "redirect:/editprofile?passchanged";
            }else {
                return "redirect:/editprofile?notmatch";
            }
        }else {
            return "redirect:/editprofile?currentnotmatch";
        }

    }

//GET USER PROFILE PICTURE
    @GetMapping("/profile/image")
    public void showProfileImage(@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        if (user.getProfileimage()==null){
            byte[] array = Files.readAllBytes(Paths.get("src/main/resources/static/images/book.jpg"));
            user.setProfileimage(array);
        }
        InputStream is = new ByteArrayInputStream(user.getProfileimage());
        IOUtils.copy(is, response.getOutputStream());
    }

}
