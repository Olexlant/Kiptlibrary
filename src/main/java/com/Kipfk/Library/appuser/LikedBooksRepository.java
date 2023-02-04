package com.Kipfk.Library.appuser;

import com.Kipfk.Library.appbook.AppBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikedBooksRepository extends JpaRepository<LikedBooks,Long> {
    List<LikedBooks> findByUser(AppUser appUser);
    List<LikedBooks> findByUserAndBook(AppUser appUser, AppBook appBook);
    LikedBooks findByBookAndUser(AppBook appBook,AppUser appUser);
    List<LikedBooks> findAllByUser(AppUser user);
    List<LikedBooks> findAllByBook(AppBook book);
}
