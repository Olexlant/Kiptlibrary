package com.Kipfk.Library.controllers.book;

import com.Kipfk.Library.appbook.*;
import com.Kipfk.Library.appuser.AppUser;
import com.Kipfk.Library.appuser.AppUserService;
import com.Kipfk.Library.appuser.LikedBooks;
import com.Kipfk.Library.appuser.LikedBooksRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Getter
@Setter
@AllArgsConstructor
@Controller
public class BookController {
    private final CategoriesOfBooksRepository categoriesOfBooksRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final AppBookService appBookService;
    private final AppUserService appUserService;
    private final AppBookRepository appBookRepository;
    private final LikedBooksRepository likedBooksRepository;


    //ALLBOOKS
    @RequestMapping(value = "/allbooks", method = RequestMethod.GET)
    public String showAllBooks(Model model, @AuthenticationPrincipal UserDetails userDetails, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, @RequestParam("category") Optional<String> category){
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        String categor = "";
        if (category.isPresent()){
            categor = category.get();
        }
        if (!categor.isEmpty()){
            CategoriesOfBooks cb = categoriesOfBooksRepository.findAllByName(category.get());
            ArrayList<BookCategory> bc = bookCategoryRepository.findAllByCategoryId(cb.getId());
            Page<AppBook> bookPage = appBookService.findPaginatedWithCategory(PageRequest.of(currentPage - 1, pageSize),bc);
            model.addAttribute("books",bookPage);
            model.addAttribute("body", appBookService.bodyArrayForPages(bookPage));
        }else {
            Page<AppBook> bookPage = appBookRepository.findAppBooksByOrderByTitle(PageRequest.of(currentPage - 1, pageSize, Sort.by("title")));
            model.addAttribute("books",bookPage);
            model.addAttribute("body", appBookService.bodyArrayForPages(bookPage));
        }
        List<CategoriesOfBooks> categoriesOfBooks = categoriesOfBooksRepository.findAll();
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        List<LikedBooks> lb = likedBooksRepository.findAllByUser(user);
        ArrayList<AppBook> likedbooks = new ArrayList<>();
        for (LikedBooks b : lb){
            likedbooks.add(b.getBook());
        }
        if (user.getAddress()==null || user.getBirthday()==null){
            return "redirect:/editprofile?nodata";
        }
        model.addAttribute("status","allbookspage");
        model.addAttribute("likedbooks", likedbooks);
        model.addAttribute("currentcategory",categor);
        model.addAttribute("bookcategories", categoriesOfBooks);
        return "allbooks";
    }

    @GetMapping("/allbooks/{id}")
    public String showBookDetails(@PathVariable(value = "id") long id, Model model){
        if (!appBookRepository.existsById(id)){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND
            );
        }
        AppBook book = appBookRepository.findAllByIdOrderByTitle(id);
        model.addAttribute("bookd", book);
        return "bookdetails";
    }

//GET BOOK FILE TO READ
    @GetMapping("/allbooks/{id}/ebook")
    public void showEbookFile(@PathVariable Long id, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        AppBook book = appBookRepository.findAllByIdOrderByTitle(id);
        if (book.getBookfile()!=null){
            InputStream is = new ByteArrayInputStream(book.getBookfile());
            IOUtils.copy(is, response.getOutputStream());
        }
        else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND
            );
        }

    }
//DOWNLOAD BOOK FILE
    @RequestMapping("/allbooks/{id}/download")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id, HttpServletResponse response) {
        AppBook book = appBookRepository.findAllByIdOrderByTitle(id);
        if (book.getBookfile()!=null){
            HttpHeaders headers = new HttpHeaders();
            ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                    .filename(book.getTitle()+".pdf", StandardCharsets.UTF_8)
                    .build();
            response.setContentType("application/pdf");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(book.getBookfile());
        }else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND
            );
        }

    }

//GET BOOK IMAGE BY ID
    @GetMapping("/book/image/{bookid}")
    public void showBookImage(@PathVariable Long bookid, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        AppBook book = appBookRepository.findAllByIdOrderByTitle(bookid);
        InputStream is = new ByteArrayInputStream(book.getBookimg());
        IOUtils.copy(is, response.getOutputStream());
    }

//GET BOOK QRIMAGE BY BOOK ID
    @GetMapping("/qrcode/image/{bookid}")
    public void showQrCodeImage(@PathVariable Long bookid,HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        AppBook book = appBookRepository.findAllByIdOrderByTitle(bookid);
        InputStream is = new ByteArrayInputStream(book.getQrimg());
        IOUtils.copy(is, response.getOutputStream());
    }

}
