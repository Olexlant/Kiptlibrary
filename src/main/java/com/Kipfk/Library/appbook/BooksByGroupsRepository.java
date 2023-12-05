package com.Kipfk.Library.appbook;

import com.Kipfk.Library.appuser.Groups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface BooksByGroupsRepository extends JpaRepository<BooksByGroups,Long> {
    ArrayList<BooksByGroups> findAllByGroups(Groups groups);
    ArrayList<BooksByGroups> findAllByGroups_Id(Long groupsId);

    List<BooksByGroups> findByGroups_IdAndBook_Id(Long groupId,Long BookId);

    @Transactional
    void deleteAllByBookId(Long bookId);
    boolean existsByGroups_IdAndBook_Id(Long groupId,Long BookId);

}
