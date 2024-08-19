package com.Kipfk.Library.appuser;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
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
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("book").get("qrid")), "%"+keyword.toLowerCase()+"%"));

            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("firstName")), "%"+keyword.toLowerCase()+"%"));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("lastName")), "%"+keyword.toLowerCase()+"%"));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("email")), "%"+keyword.toLowerCase()+"%"));
            try {
                long numkeyword = Long.valueOf(keyword);
                predicates.add(criteriaBuilder.equal(root.get("book").get("year"), numkeyword));
            }catch (NumberFormatException e){

            }
           return criteriaBuilder.or(predicates.toArray(new Predicate[]{}));
        };
        return takenBooksRepository.findAll(specification);
    }
}
