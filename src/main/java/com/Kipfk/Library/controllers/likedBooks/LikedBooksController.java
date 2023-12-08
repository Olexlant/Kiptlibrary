package com.Kipfk.Library.controllers.likedBooks;

import com.Kipfk.Library.appbook.AppBook;
import com.Kipfk.Library.appbook.AppBookRepository;
import com.Kipfk.Library.appuser.AppUser;
import com.Kipfk.Library.appuser.AppUserService;
import com.Kipfk.Library.appuser.LikedBooks;
import com.Kipfk.Library.appuser.LikedBooksRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Controller
public class LikedBooksController {
    private final AppUserService appUserService;
    private final AppBookRepository appBookRepository;
    private final LikedBooksRepository likedBooksRepository;

//USER ADD LIKED BOOKS
    @PostMapping("/likingbook/{id}")
    public String createlikedbook(@AuthenticationPrincipal UserDetails userDetails, LikedBooks likedBooks, @PathVariable(value = "id") long bookid) {
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        if (likedBooksRepository.existsByUserAndBookId(user,bookid)){
            return "redirect:/allbooks?alreadyLiked";
        }else {
            likedBooks.setUser(user);
            AppBook book = appBookRepository.findAllByIdOrderByTitle(bookid);
            likedBooks.setBook(book);
            likedBooks.setAddedat(LocalDate.now());
            likedBooksRepository.save(likedBooks);
            return "redirect:/allbooks?addedToFavourite";
        }
    }

    @PostMapping("/likingbook/{id}/deletebyuser")
    public String deletelikedbookbyuser(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(value = "id") long bookid) {
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        likedBooksRepository.deleteAllByUserAndBookId(user, bookid);
        return "redirect:/allbooks?deletedFromFavourite";
    }

//USER FAVOURITE BOOKS
    @GetMapping("/myfavouritebooks")
    public String showUserFavouriteBooks(@AuthenticationPrincipal UserDetails userDetails, Model model){
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        List<LikedBooks> likedBooks = likedBooksRepository.findLikedBooksByUserEmail(userDetails.getUsername());
        model.addAttribute("likedbooks", likedBooks);
        if (user.getAddress()==null || user.getBirthday()==null){
            return "redirect:/editprofile?nodata";
        }
        return "myfavouritebooks";
    }
    @PostMapping("/myfavouritebooks/{likeId}/remove")
    public String removelikedbooks(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(value = "likeId") long likeId) {
        likedBooksRepository.deleteAllByUserEmailAndId(userDetails.getUsername(), likeId);
        return "redirect:/myfavouritebooks?deletedFromFavourite";
    }
}
