package com.Kipfk.Library.controllers;

import com.Kipfk.Library.appuser.AppUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminPanelRestController {

    private final AppUserService appUserService;

    public AdminPanelRestController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/admin/userService/generateRegistrationAccessKey")
    public String generateRegistrationAccessKey() {
        return appUserService.generateRegistrationAccessKey();
    }
    @GetMapping("/admin/userService/getRegistrationAccessKey")
    public String getRegistrationAccessKey() {
        return appUserService.getRegistrationAccessKey();
    }

}
