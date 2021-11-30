package com.Kipfk.Library.appbook;

import org.springframework.stereotype.Service;

@Service
public class AppBookService {
    public void bookadd(AppBook appBook) {
        new AppBook(
                appBook.getQrid(),
                appBook.getTitle(),
                appBook.getAuthor(),
                appBook.getYear(),
                appBook.getStilaj(),
                appBook.getPolka(),
                appBook.getBookimg(),
                appBook.getQrimg()
        );
    }

}
