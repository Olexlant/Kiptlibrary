package com.Kipfk.Library.appbook;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategory,Long> {
    ArrayList<BookCategory> findAllByCategoryId(Long id);
    List<BookCategory> findAllByBookId(Long id);
    @Transactional
    void deleteAllByBookId(Long bookId);

    BookCategory findByCategory_IdAndBook_Id (Long categoryId, Long bookId);
}
