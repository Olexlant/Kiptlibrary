package com.Kipfk.Library.appbook;

import com.Kipfk.Library.appuser.TakenBooksRepository;
import com.Kipfk.Library.registration.token.ConfirmationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AppBookService {

    @Autowired
    private AppBookRepository appBookRepository;
    private ConfirmationTokenRepository confirmationTokenRepository;
    private TakenBooksRepository takenBooksRepository;
    private BookCategoryRepository bookCategoryRepository;


    public Page<AppBook> findPaginated(Pageable pageable) {
        List<AppBook> books = appBookRepository.findAll();
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<AppBook> list;

        if (books.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, books.size());
            list = books.subList(startItem, toIndex);
        }
        Page<AppBook> bookPage = new PageImpl<>(list, PageRequest.of(currentPage, pageSize), books.size());
        return bookPage;
    }
    public Page<AppBook> searchpagepaginated(Pageable pageable, List<AppBook> books) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<AppBook> list;

        if (books.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, books.size());
            list = books.subList(startItem, toIndex);
        }
        Page<AppBook> bookPage = new PageImpl<>(list, PageRequest.of(currentPage, pageSize), books.size());
        return bookPage;
    }

    public Page<AppBook> findPaginatedWithCategory(Pageable pageable, ArrayList<BookCategory> bc) {
        ArrayList<AppBook> books = new ArrayList<>();
        for (BookCategory b : bc){
            books.add(b.getBook());
        }
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<AppBook> list;

        if (books.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, books.size());
            list = books.subList(startItem, toIndex);
        }
        Page<AppBook> bookPage = new PageImpl<>(list, PageRequest.of(currentPage, pageSize), books.size());
        return bookPage;
    }
    public void bookadd(AppBook appBook) {
        new AppBook(
                appBook.getQrid(),
                appBook.getTitle(),
                appBook.getAuthor(),
                appBook.getYear(),
                appBook.getStilaj(),
                appBook.getPolka(),
                appBook.getBookimg(),
                appBook.getQrimg(),
                appBook.getBookfile()
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


