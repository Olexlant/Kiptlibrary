package com.Kipfk.Library.appbook;

import antlr.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Repository
@Transactional(readOnly = true)
public interface AppBookRepository extends JpaRepository<AppBook, Long>, JpaSpecificationExecutor<AppBook> {

    Optional<AppBook> findById(Long id);

}
