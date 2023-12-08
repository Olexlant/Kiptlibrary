package com.Kipfk.Library.bookFiles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface BookFilesRepository extends JpaRepository<BookFiles, Long> {
    @Transactional
    void deleteAllById(Long fileId);
    BookFiles findBookFilesById(Long fileId);
}

