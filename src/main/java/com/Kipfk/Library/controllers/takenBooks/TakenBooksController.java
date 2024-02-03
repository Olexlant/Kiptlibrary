package com.Kipfk.Library.controllers.takenBooks;

import com.Kipfk.Library.appuser.AppUser;
import com.Kipfk.Library.appuser.AppUserService;
import com.Kipfk.Library.appuser.TakenBooks;
import com.Kipfk.Library.appuser.TakenBooksRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Controller
public class TakenBooksController {
    private final AppUserService appUserService;
    private final TakenBooksRepository takenBooksRepository;
    @GetMapping("/mytakenbooks")
    public String showUserAssigned(@AuthenticationPrincipal UserDetails userDetails, Model model){
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        if (user.getAddress()==null || user.getBirthday()==null){
            return "redirect:/editprofile?nodata";
        }
        List<TakenBooks> takenBooks = takenBooksRepository.findAllByUserAndDeletedIsFalse(user);
        model.addAttribute("takenbooks", takenBooks);
        return "mytakenbooks";
    }
}
