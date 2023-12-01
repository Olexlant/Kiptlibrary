package com.Kipfk.Library.bookFiles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface BookFilesRepository extends JpaRepository<BookFiles, Long> {
    @Query("update BookFiles f set f.bookFile = :file, f.bookFileName = :fileName, f.bookFileContentType = :fileContentType WHERE f.appBookId = :appBookId")
    void updateBookFile(@Param("appBookId") Long id, @Param("file") byte[] file, @Param("fileName") String fileName, @Param("fileContentType") String fileContentType);

    @Transactional
    void deleteAllByAppBookId(Long appBookId);
    BookFiles findBookFilesByAppBookId(Long appBookId);
}

