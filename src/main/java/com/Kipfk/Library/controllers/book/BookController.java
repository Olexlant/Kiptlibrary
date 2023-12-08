package com.Kipfk.Library.controllers.book;

import com.Kipfk.Library.appbook.*;
import com.Kipfk.Library.appuser.AppUser;
import com.Kipfk.Library.appuser.AppUserService;
import com.Kipfk.Library.appuser.LikedBooks;
import com.Kipfk.Library.appuser.LikedBooksRepository;
import com.Kipfk.Library.bookFiles.BookFiles;
import com.Kipfk.Library.bookFiles.BookFilesService;
import com.Kipfk.Library.images.Images;
import com.Kipfk.Library.images.ImagesService;
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
    private final BookFilesService bookFilesService;
    private final ImagesService imagesService;

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

//OPEN BOOK READER
    @PostMapping("/readBook")
    public String readBook(@RequestParam Long bookId, Model model) {
        model.addAttribute("book_id",bookId);
        return "pdfReader";
    }

//DOWNLOAD BOOK FILE
    @RequestMapping("/allbooks/{id}/download")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id, HttpServletResponse response) {
        AppBookRepository.BookNoFileAndPhoto book = appBookRepository.findAppBookById(id);
        BookFiles bookFile =bookFilesService.getBookFileById(book.getBookFileId());
        if (bookFile.getBookFile()!=null){
            HttpHeaders headers = new HttpHeaders();
            ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                    .filename(bookFile.getBookFileName()+".pdf", StandardCharsets.UTF_8)
                    .build();
            response.setContentType("application/pdf");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(bookFile.getBookFile());
        }else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND
            );
        }

    }

//GET BOOK IMAGE BY ID
    @GetMapping("/book/image/{imageId}")
    public void showBookImage(@PathVariable Long imageId, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        Images bookImg = imagesService.getImageById(imageId);
        if (bookImg!=null){
            InputStream is = new ByteArrayInputStream(bookImg.getImageFile());
            IOUtils.copy(is, response.getOutputStream());
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND
            );
        }
    }

//GET BOOK QRIMAGE BY BOOK ID
    @GetMapping("/qrcode/image/{imageId}")
    public void showQrCodeImage(@PathVariable Long imageId,HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        Images bookImg = imagesService.getImageById(imageId);
        if (bookImg!=null){
            InputStream is = new ByteArrayInputStream(bookImg.getImageFile());
            IOUtils.copy(is, response.getOutputStream());
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND
            );
        }
    }

}
