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

    public BookFiles addBookFile(MultipartFile book) throws IOException {
        BookFiles newBookFile = new BookFiles();
        newBookFile.setBookFile(book.getBytes());
        newBookFile.setBookFileContentType(book.getContentType());
        newBookFile.setBookFileName(book.getOriginalFilename());
        bookFilesRepository.save(newBookFile);
        return newBookFile;
    }

    public void deleteBookFileById(Long fileId){
        bookFilesRepository.deleteAllById(fileId);
    }
    public BookFiles getBookFileById(Long fileId){
        return bookFilesRepository.findBookFilesById(fileId);
    }
}
