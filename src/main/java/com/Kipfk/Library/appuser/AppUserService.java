package com.Kipfk.Library.appuser;

import com.Kipfk.Library.appbook.AppBook;
import com.Kipfk.Library.registration.token.ConfirmationToken;
import com.Kipfk.Library.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private  final static String USER_NOT_FOUND_MSG = "Користувач з email %s не знайдено";
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    public Page<AppUser> findPaginated(Pageable pageable) {
        List<AppUser> users = appUserRepository.findAll(Sort.by(Sort.Direction.ASC, "lastName"));
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<AppUser> list;
        if (users.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, users.size());
            list = users.subList(startItem, toIndex);
        }
        Page<AppUser> userPage = new PageImpl<>(list, PageRequest.of(currentPage, pageSize), users.size());
        return userPage;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public String signUpUser(AppUser appUser) {
        boolean UserExists = appUserRepository.findByEmail(appUser.getEmail()).isPresent();
        if(UserExists){
            throw new IllegalStateException("Користувач з таким email вже існує");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());

        appUser.setPassword(encodedPassword);

        appUserRepository.save(appUser);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10),
                appUser
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);

//      TODO: SEND EMAIL

        return token;
    }
    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }
    public List<AppUser> getByKeyword(String keyword){
        return appUserRepository.findByKeyword(keyword);
    }
}
