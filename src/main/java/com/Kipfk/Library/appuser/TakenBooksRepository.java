package com.Kipfk.Library.appuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TakenBooksRepository extends JpaRepository <TakenBooks,Long> {

}
