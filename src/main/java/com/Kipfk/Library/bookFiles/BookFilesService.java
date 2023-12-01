package com.Kipfk.Library.bookFiles;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class BookFilesService {
    private final BookFilesRepository bookFilesRepository;

    public BookFilesService(BookFilesRepository bookFilesRepository) {
        this.bookFilesRepository = bookFilesRepository;
    }

    public BookFiles addBookFile(MultipartFile book, Long appBookId) throws IOException {
        BookFiles newBookFile = new BookFiles();
        newBookFile.setBookFile(book.getBytes());
        newBookFile.setBookFileContentType(book.getContentType());
        newBookFile.setBookFileName(book.getOriginalFilename());
        newBookFile.setAppBookId(appBookId);
        bookFilesRepository.save(newBookFile);
        return newBookFile;
    }

    public void deleteBookFileByAppBookId(Long appBookId){
        bookFilesRepository.deleteAllByAppBookId(appBookId);
    }
    public BookFiles getBookFileByAppBookId(Long appBookId){
        return bookFilesRepository.findBookFilesByAppBookId(appBookId);
    }
}
