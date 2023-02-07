package com.Kipfk.Library.appuser;

import com.Kipfk.Library.appbook.AppBook;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TakenBooksService {
    private final TakenBooksRepository takenBooksRepository;

    public TakenBooksService(TakenBooksRepository takenBooksRepository) {
        this.takenBooksRepository = takenBooksRepository;
    }
    public List<TakenBooks> getAllByKeyword(String keyword){
        Specification<TakenBooks> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("book").get("title")), "%"+keyword.toLowerCase()+"%"));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("book").get("author")), "%"+keyword.toLowerCase()+"%"));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("book").get("description")), "%"+keyword.toLowerCase()+"%"));

            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("firstName")), "%"+keyword.toLowerCase()+"%"));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("lastName")), "%"+keyword.toLowerCase()+"%"));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("email")), "%"+keyword.toLowerCase()+"%"));
            try {
                long numkeyword = Long.valueOf(keyword);
                predicates.add(criteriaBuilder.equal(root.get("book").get("qrid"), numkeyword));
                predicates.add(criteriaBuilder.equal(root.get("book").get("year"), numkeyword));
            }catch (NumberFormatException e){

            }
           return criteriaBuilder.or(predicates.toArray(new Predicate[]{}));
        };
        return takenBooksRepository.findAll(specification);
    };

    public Page<TakenBooks> findPaginated(Pageable pageable) {
        List<TakenBooks> takenBooks = takenBooksRepository.findAll(Sort.by(Sort.Direction.ASC, "takenat"));
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<TakenBooks> list;
        if (takenBooks.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, takenBooks.size());
            list = takenBooks.subList(startItem, toIndex);
        }
        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), takenBooks.size());
    }
}
