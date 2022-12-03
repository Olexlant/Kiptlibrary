package com.Kipfk.Library.appbook;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriesOfBooksRepository extends JpaRepository<CategoriesOfBooks, Long> {

}
