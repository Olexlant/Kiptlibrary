package com.Kipfk.Library.appuser;

import com.Kipfk.Library.appbook.AppBook;
import com.Kipfk.Library.appuser.AppUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
    @Transactional
    @Modifying
    @Query("UPDATE AppUser a " + "SET a.enabled = TRUE WHERE a.email = ?1")
    int enableAppUser(String email);

    @Query(value = "select * from app_user u where u.first_name like %:keyword% or u.last_name like %:keyword% or u.groups like %:keyword% or u.phonenum like %:keyword% or u.email like %:keyword%", nativeQuery = true)
    List<AppUser> findByKeyword(@Param("keyword") String keyword);


}
