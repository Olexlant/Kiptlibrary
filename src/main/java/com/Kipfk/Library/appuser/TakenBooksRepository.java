package com.Kipfk.Library.appuser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TakenBooksRepository extends JpaRepository <TakenBooks,Long>, JpaSpecificationExecutor<TakenBooks> {
    List<TakenBooks> findByUserAndDeletedIsFalse(AppUser appUser);
    boolean existsByUser_IdAndDeletedIsFalse(Long userId);

    boolean existsByUserIdAndBookIdAndDeletedIsFalse(Long userId, Long bookId);

    boolean existsByBookIdAndDeletedIsFalse(Long id);

    List<TakenBooks> findAllByUserAndDeletedIsFalse(AppUser appUser);

    @Transactional
    void deleteAllByBookIdAndDeletedIsTrue(Long Id);
    Page<TakenBooks> findAllByDeletedIsFalse(Pageable pageable);
    Page<TakenBooks> findAllByDeletedIsTrue(Pageable pageable);
    List<TakenBooks> findAllByDeletedIsFalseAndNotificationSendedIsFalseAndReturnExpiresAtIsBefore(LocalDate now);
    Page<TakenBooks> findAllByDeletedIsFalseAndReturnExpiresAtIsBefore(Pageable pageable, LocalDate now);
    Page<TakenBooks> findAllByDeletedIsFalseAndReturnExpiresAtIsBeforeAndBook_Electronic(Pageable pageable, LocalDate now, boolean isElectronic);

    @Transactional
    void deleteAllByUserAndDeletedIsTrue(AppUser appUser);

    @Transactional
    void deleteAllByDeletedIsTrue();

    int countAllByDeletedIsFalse();
}
