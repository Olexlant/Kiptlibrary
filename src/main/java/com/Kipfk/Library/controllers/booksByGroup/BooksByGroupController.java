package com.Kipfk.Library.controllers.booksByGroup;

import com.Kipfk.Library.appbook.AppBook;
import com.Kipfk.Library.appbook.BooksByGroups;
import com.Kipfk.Library.appbook.BooksByGroupsRepository;
import com.Kipfk.Library.appuser.AppUser;
import com.Kipfk.Library.appuser.AppUserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Controller
public class BooksByGroupController {
    private final AppUserService appUserService;
    private final BooksByGroupsRepository booksByGroupsRepository;

    //BOOKS BY USER GROUP
    @GetMapping("/booksbygroup")
    public String showBooksByGroup(@AuthenticationPrincipal UserDetails userDetails, Model model){
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        ArrayList<BooksByGroups> booksByGroups = booksByGroupsRepository.findAllByGroups(user.getGroups());
        List<AppBook> books = new ArrayList<>();
        for (BooksByGroups b : booksByGroups){
            books.add(b.getBook());
        }
        model.addAttribute("books",books);
        if (user.getAddress()==null || user.getBirthday()==null){
            return "redirect:/editprofile?nodata";
        }
        return "booksbygroup";
    }

}
