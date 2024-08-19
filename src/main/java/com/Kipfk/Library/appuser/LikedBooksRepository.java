package com.Kipfk.Library.appuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface LikedBooksRepository extends JpaRepository<LikedBooks,Long> {
    List<LikedBooks> findLikedBooksByUserEmail(String userEmail);

    List<LikedBooks> findAllByUser(AppUser user);

    boolean existsByUserAndBookId(AppUser user, Long bookId);

    @Transactional
    void deleteAllByUserEmailAndId(String userEmail, Long bookId);
    @Transactional
    void deleteAllByUserAndBookId(AppUser user, Long bookId);
    @Transactional
    void deleteAllByBookId(Long bookId);
    @Transactional
    void deleteAllByUser(AppUser user);
}
