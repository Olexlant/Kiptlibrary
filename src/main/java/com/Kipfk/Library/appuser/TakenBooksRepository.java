package com.Kipfk.Library.appuser;

import com.Kipfk.Library.appbook.AppBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TakenBooksRepository extends JpaRepository <TakenBooks,Long>, JpaSpecificationExecutor<TakenBooks> {
    List<TakenBooks> findByUser(AppUser appUser);
    List<TakenBooks> findByUserAndBook(AppUser appUser, AppBook appBook);
    Optional<TakenBooks> findByBookId(Long id);
    List<TakenBooks> findAllByUser(AppUser appUser);
    List<TakenBooks> findAllByBook(AppBook book);
    int countAllBy();
}
