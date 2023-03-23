package com.Kipfk.Library.controllers;

import com.Kipfk.Library.appbook.*;
import com.Kipfk.Library.appuser.*;
import com.Kipfk.Library.news.NewsFilesStorageRepository;
import com.Kipfk.Library.news.NewsRepository;
import com.Kipfk.Library.registration.token.ConfirmationTokenRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AdminPanelControllerTest {

    ConfirmationTokenRepository confirmationTokenRepository = mock(ConfirmationTokenRepository.class);
    AppUserService appUserService = mock(AppUserService.class);
    AppBookService appBookService = mock(AppBookService.class);
    AppUserRepository appUserRepository = mock(AppUserRepository.class);
    AppBookRepository appBookRepository = mock(AppBookRepository.class);
    TakenBooksRepository takenBooksRepository = mock(TakenBooksRepository.class);
    LikedBooksRepository likedBooksRepository = mock(LikedBooksRepository.class);
    BookCategoryRepository bookCategoryRepository = mock(BookCategoryRepository.class);
    CategoriesOfBooksRepository categoriesOfBooksRepository = mock(CategoriesOfBooksRepository.class);
    GroupsRepository groupsRepository = mock(GroupsRepository.class);
    BooksByGroupsRepository booksByGroupsRepository = mock(BooksByGroupsRepository.class);
    BookOrdersRepository bookOrdersRepository = mock(BookOrdersRepository.class);
    NewsRepository newsRepository = mock(NewsRepository.class);
    NewsFilesStorageRepository newsFilesStorageRepository = mock(NewsFilesStorageRepository.class);

    // Create a new instance of the AdminPanelController
    AdminPanelController adminPanelController = new AdminPanelController(confirmationTokenRepository,appUserService,appBookService,appUserRepository,appBookRepository,takenBooksRepository,likedBooksRepository,bookCategoryRepository,categoriesOfBooksRepository,groupsRepository,booksByGroupsRepository,bookOrdersRepository,newsRepository,newsFilesStorageRepository);



    @Test
    public void testShowBookAddingForm() {

        // Mock the dependencies

        // Mock the Model object
        Model model = mock(Model.class);

        // Call the showBookAddingForm method and store the result
        String result = adminPanelController.showBookAddingForm(model);

        // Verify that the "book" attribute was added to the model
        verify(model).addAttribute("book", new AppBook());

        // Verify that the correct view name was returned
        assertEquals(result, "addbook");
    }

    @Test
    void testBookadd() throws IOException {
        AppBook appBook = new AppBook();
        appBook.setQrid(1234l);
        MockMultipartFile[] multipartFiles = {new MockMultipartFile("bookimg", "bookimg.jpg", "image/jpeg", "Test".getBytes()),
                new MockMultipartFile("bookfile", "bookfile.pdf", "application/pdf", "Test".getBytes())};
        appBook.setBookfileurl("https://www.google.com/");
        Model model = mock(Model.class);
        String viewName = adminPanelController.bookadd(appBook, multipartFiles);
        verify(appBookRepository, times(1)).save(appBook);
        assertEquals("redirect:/admin/addbook?success", viewName);
    }

}
