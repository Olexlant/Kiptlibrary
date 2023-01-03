package com.Kipfk.Library.appbook;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriesOfBooksRepository extends JpaRepository<CategoriesOfBooks, Long> {
    Optional<CategoriesOfBooks> findByName(String name);

    CategoriesOfBooks findAllByName(String name);
    CategoriesOfBooks findAllById(Long id);

}
