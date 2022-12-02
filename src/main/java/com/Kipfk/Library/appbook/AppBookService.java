package com.Kipfk.Library.appbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppBookService {

    @Autowired
    private AppBookRepository appBookRepository;

    public void bookadd(AppBook appBook) {
        new AppBook(
                appBook.getQrid(),
                appBook.getTitle(),
                appBook.getAuthor(),
                appBook.getYear(),
                appBook.getStilaj(),
                appBook.getPolka(),
                appBook.getBookimg(),
                appBook.getQrimg()
        );
    }
    public List<AppBook> getAllByKeyword (String keyword){
        Specification<AppBook> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), "%"+keyword.toLowerCase()+"%"));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%"+keyword.toLowerCase()+"%"));
            try {
                long numkeyword = Long.valueOf(keyword);
                predicates.add(criteriaBuilder.equal(root.get("qrid"), numkeyword));
                predicates.add(criteriaBuilder.equal(root.get("year"), numkeyword));

            }catch (NumberFormatException e){

            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[]{}));
        };
        return appBookRepository.findAll(specification);
    };

}


