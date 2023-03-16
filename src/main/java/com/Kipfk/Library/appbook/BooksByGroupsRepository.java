package com.Kipfk.Library.appbook;

import com.Kipfk.Library.appuser.Groups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface BooksByGroupsRepository extends JpaRepository<BooksByGroups,Long> {
    ArrayList<BooksByGroups> findAllByGroups(Groups groups);
    ArrayList<BooksByGroups> findAllByGroups_Id(Long groupsId);
    List<BooksByGroups> findByGroupsAndBook(Groups groups,AppBook book);
    List<BooksByGroups> findByGroups_IdAndBook_Id(Long groupId,Long BookId);
    List<BooksByGroups> findAllByBook(AppBook appBook);
    boolean existsByGroups_IdAndBook_Id(Long groupId,Long BookId);

}
