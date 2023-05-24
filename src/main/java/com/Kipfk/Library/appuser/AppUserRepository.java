package com.Kipfk.Library.appuser;

import com.Kipfk.Library.appbook.AppBook;
import com.Kipfk.Library.appuser.AppUserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface AppUserRepository extends JpaRepository<AppUser, Long>, JpaSpecificationExecutor<AppUser> {
    interface UserNoPhoto {
        Long getId();
        String getFirstName();
        String getLastName();
        String getPhonenum();
        String getPassword();
        String getEmail();
        LocalDate getBirthday();
        String getAddress();
        Groups getGroups();
        AppUserRole getAppUserRole();
    }
    Optional<AppUser> findByEmail(String email);
    @Transactional
    @Modifying
    @Query("UPDATE AppUser a " + "SET a.enabled = TRUE WHERE a.email = ?1")
    int enableAppUser(String email);

    int countAllByEnabledIsTrue();

    Page<UserNoPhoto> findAllBy(Pageable pageable);
    Page<UserNoPhoto> findAllByEnabledIsTrue(Pageable pageable);
    Page<UserNoPhoto> findAllByAppUserRoleAndEnabledIsTrue(Pageable pageable,AppUserRole appUserRole);
    List<AppUser> findAllByGroups_Id(Long groupsId);

    AppUser findAllById(Long id);
}
