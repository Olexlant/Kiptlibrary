package com.Kipfk.Library.controllers.book;

import com.Kipfk.Library.appbook.*;
import com.Kipfk.Library.appuser.LikedBooksRepository;
import com.Kipfk.Library.appuser.TakenBooksRepository;
import com.Kipfk.Library.bookFiles.BookFiles;
import com.Kipfk.Library.bookFiles.BookFilesService;
import com.Kipfk.Library.images.Images;
import com.Kipfk.Library.images.ImagesService;
import com.google.zxing.WriterException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@Controller
public class AdminBookController {
    private final AppBookService appBookService;
    private final AppBookRepository appBookRepository;
    private final TakenBooksRepository takenBooksRepository;
    private final LikedBooksRepository likedBooksRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final BooksByGroupsRepository booksByGroupsRepository;
    private final BookOrdersRepository bookOrdersRepository;
    private final BookFilesService bookFilesService;
    private final ImagesService imagesService;

//ADDBOOK
    @GetMapping("/admin/addbook")
    public String showBookAddingForm(Model model) {
        model.addAttribute("book", new AppBook());
        return "addbook";
    }

    @Transactional
    @PostMapping("/admin/book_adding")
    public String bookadd(AppBook appBook,@RequestParam("files") MultipartFile[] multipartFiles) throws IOException {
        if (multipartFiles[0] == null){
            appBook.setBookImgId(null);
        } else {
            Images bookImg = imagesService.addImageFile(multipartFiles[0]);
            appBook.setBookImgId(bookImg.getId());
        }
        if(appBook.getBookFileUrl().isEmpty()){
            if (!multipartFiles[1].isEmpty()){
                BookFiles bookFile = bookFilesService.addBookFile(multipartFiles[1]);
                appBook.setBookFileId(bookFile.getId());
                appBook.setElectronic(true);
            }
        }else {
            bookFilesService.deleteBookFileById(appBook.getBookFileId());
            appBook.setElectronic(true);
        }
        appBookService.bookadd(appBook);
        appBookRepository.save(appBook);
        try {
            Images qrCodeImg = imagesService.addQrCodeImageFile(
                    QRCodeGenerator.getQRCodeImage(String.format("%06d", appBook.getId()),300,300));
            appBook.setQrImgId(qrCodeImg.getId());
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
        appBook.setQrid(String.format("%06d", appBook.getId()));
        appBookRepository.save(appBook);
        return "redirect:/admin/addbook?success";
    }

    @GetMapping("/admin/allbooksadmin")
    public String showAllBooksAdmin(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, @RequestParam("category") Optional<String> category){
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        String categor = "";
        Page<AppBookRepository.BookNoFileAndPhoto> bookPage;
        if (category.isPresent()){
            categor = category.get();
        }
        if (categor.equals("electronic")){
            bookPage = appBookRepository.findAllByElectronicIsTrueOrderByTitle(PageRequest.of(currentPage - 1, pageSize));
            model.addAttribute("books", bookPage);
            model.addAttribute("body", appBookService.bodyArrayForPages(bookPage));
        }else if (categor.equals("physical")){
            bookPage = appBookRepository.findAllByElectronicIsFalseOrderByTitle(PageRequest.of(currentPage - 1, pageSize));
            model.addAttribute("books", bookPage);
            model.addAttribute("body", appBookService.bodyArrayForPages(bookPage));
        }else {
            Page<AppBookRepository.BookNoFileAndPhoto> allBooksPage = appBookRepository.findAllByOrderByTitle(PageRequest.of(currentPage - 1, pageSize));
            model.addAttribute("books", allBooksPage);
            model.addAttribute("body", appBookService.bodyArrayForPages(allBooksPage));
        }
        model.addAttribute("category", categor);
        return "allbooksadmin";
    }


    @GetMapping("/admin/allbooksadmin/{id}/edit")
    public String AdminBookEdit(@PathVariable(value = "id") long id, Model model){
        if (!appBookRepository.existsById(id)){
            return "redirect:/admin/allbooksadmin";
        }
        AppBookRepository.BookNoFileAndPhoto book = appBookRepository.findAppBookById(id);
        model.addAttribute("book", book);
        return "bookadminedit";
    }
    @Transactional
    @PostMapping("/admin/allbooksadmin/{id}/edit")
    public String AdminBookUpdate(@PathVariable(value = "id") long id, @RequestParam String title, @RequestParam String author, @RequestParam Long year, @RequestParam("files") MultipartFile[] multipartFiles,@RequestParam String bookFileUrl, @RequestParam String description, @RequestParam Long count, @RequestParam Long daysToReturn) throws IOException {
        AppBook book = appBookRepository.findAllByIdOrderByTitle(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setYear(year);
        book.setDescription(description);
        book.setCount(count);
        book.setDaysToReturn(daysToReturn);
        if (!multipartFiles[0].isEmpty()){
            if (book.getBookImgId() == null) {
                Images bookImg = imagesService.addImageFile(multipartFiles[0]);
                book.setBookImgId(bookImg.getId());
            } else {
                imagesService.updateImageFileById(multipartFiles[0], book.getBookImgId());
            }
        }
        if(bookFileUrl.isEmpty()){
            book.setBookFileUrl("");
            if (!multipartFiles[1].isEmpty()){
                bookFilesService.deleteBookFileById(book.getBookFileId());
                BookFiles bookFile = bookFilesService.addBookFile(multipartFiles[1]);
                book.setBookFileId(bookFile.getId());
                book.setElectronic(true);
            }
        }else {
            bookFilesService.deleteBookFileById(book.getBookFileId());
            book.setBookFileId(null);
            book.setBookFileUrl(bookFileUrl);
            book.setElectronic(true);
        }
        appBookRepository.save(book);
        return "redirect:/admin/allbooksadmin?changessaved";
    }
    @Transactional
    @PostMapping("/admin/allbooksadmin/{id}/remove")
    public String AdminBookDelete(@PathVariable(value = "id") long id) {
        if (takenBooksRepository.existsByBookIdAndDeletedIsFalse(id)){
            return "redirect:/admin/allbooksadmin?usernotreturn";
        } else {
            AppBookRepository.BookNoFileAndPhoto book = appBookRepository.findAppBookById(id);
            takenBooksRepository.deleteAllByBookIdAndDeletedIsTrue(id);
            likedBooksRepository.deleteAllByBookId(id);
            bookCategoryRepository.deleteAllByBookId(id);
            booksByGroupsRepository.deleteAllByBookId(id);
            bookOrdersRepository.deleteAllByBookId(id);
            bookFilesService.deleteBookFileById(book.getBookFileId());
            imagesService.deleteImageById(book.getBookImgId());
            imagesService.deleteImageById(book.getQrImgId());
            appBookRepository.deleteById(id);
            return "redirect:/admin/allbooksadmin?deletesuccess";
        }
    }
}
