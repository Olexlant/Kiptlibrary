package com.Kipfk.Library.controllers.book;

import com.Kipfk.Library.appbook.*;
import com.Kipfk.Library.appuser.*;
import com.Kipfk.Library.bookFiles.BookFiles;
import com.Kipfk.Library.bookFiles.BookFilesService;
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

import java.io.IOException;
import java.util.List;
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

//ADDBOOK
    @GetMapping("/admin/addbook")
    public String showBookAddingForm(Model model) {
        model.addAttribute("book", new AppBook());
        return "addbook";
    }

    @PostMapping("/admin/book_adding")
    public String bookadd(AppBook appBook,@RequestParam("files") MultipartFile[] multipartFiles) throws IOException {
        appBook.setBookimg(multipartFiles[0].getBytes());
        appBookService.bookadd(appBook);
        appBookRepository.save(appBook);
        try {
            appBook.setQrimg(QRCodeGenerator.getQRCodeImage(String.format("%06d", appBook.getId()),300,300));
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
        appBook.setQrid(String.format("%06d", appBook.getId()));
        if(appBook.getBookFileUrl().isEmpty()){
            if (!multipartFiles[1].isEmpty()){
                BookFiles bookFile = bookFilesService.addBookFile(multipartFiles[1],appBook.getId());
                appBook.setBookFileId(bookFile.getId());
                appBook.setElectronic(true);
            }
        }else {
            bookFilesService.deleteBookFileByAppBookId(appBook.getId());
            appBook.setElectronic(true);
        }
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
            book.setBookimg(multipartFiles[0].getBytes());
        }
        if(bookFileUrl.isEmpty()){
            book.setBookFileUrl("");
            if (!multipartFiles[1].isEmpty()){
                bookFilesService.deleteBookFileByAppBookId(book.getId());
                BookFiles bookFile = bookFilesService.addBookFile(multipartFiles[1], book.getId());
                book.setBookFileId(bookFile.getId());
                book.setElectronic(true);
            }
        }else {
            bookFilesService.deleteBookFileByAppBookId(book.getId());
            book.setBookFileId(null);
            book.setBookFileUrl(bookFileUrl);
            book.setElectronic(true);
        }
        appBookRepository.save(book);
        return "redirect:/admin/allbooksadmin?changessaved";
    }
    @PostMapping("/admin/allbooksadmin/{id}/remove")
    public String AdminBookDelete(@PathVariable(value = "id") long id) {
        if (takenBooksRepository.findByBookIdAndDeletedIsFalse(id).isPresent()){
            return "redirect:/admin/allbooksadmin?usernotreturn";
        } else {
            AppBook book = appBookRepository.findAllByIdOrderByTitle(id);
            List<TakenBooks> takenBooks = takenBooksRepository.findAllByBookAndDeletedIsTrue(book);
            if (!takenBooks.isEmpty()){
                takenBooksRepository.deleteAll(takenBooks);
            }
            List<LikedBooks> likedBooks = likedBooksRepository.findAllByBook(book);
            if (!likedBooks.isEmpty()){
                likedBooksRepository.deleteAll(likedBooks);
            }
            List<BookCategory> bookCategories = bookCategoryRepository.findAllByBookId(id);
            if (!bookCategories.isEmpty()){
                bookCategoryRepository.deleteAll(bookCategories);
            }
            List<BooksByGroups> booksByGroups = booksByGroupsRepository.findAllByBook(book);
            if (!booksByGroups.isEmpty()){
                booksByGroupsRepository.deleteAll(booksByGroups);
            }
            List<BookOrders> bookOrders = bookOrdersRepository.findAllByBook(book);
            if (!bookOrders.isEmpty()){
                bookOrdersRepository.deleteAll(bookOrders);
            }
            appBookRepository.deleteById(id);
            return "redirect:/admin/allbooksadmin?deletesuccess";
        }
    }
}
