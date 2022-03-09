package com.Kipfk.Library.registration;

import com.Kipfk.Library.appuser.AppUser;
import org.springframework.web.bind.annotation.*;

public class RegistrationController {

    private final RegistrationService registrationService ;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public String register(@RequestBody AppUser user) {
        return registrationService.register(user);
    }

}
