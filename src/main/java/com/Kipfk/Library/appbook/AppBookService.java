package com.Kipfk.Library.appbook;

import com.Kipfk.Library.appuser.TakenBooksRepository;
import com.Kipfk.Library.controllers.MainController;
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

    public int[] bodyArrayForPages(Page bookPage){
        int[] body;
        if (bookPage.getTotalPages() > 7) {
            int totalPages = bookPage.getTotalPages();
            int pageNumber = bookPage.getNumber()+1;
            int[] head = (pageNumber > 4) ? new int[]{1, -1} : new int[]{1,2,3};
            int[] bodyBefore = (pageNumber > 4 && pageNumber < totalPages - 1) ? new int[]{pageNumber-2, pageNumber-1} : new int[]{};
            int[] bodyCenter = (pageNumber > 3 && pageNumber < totalPages - 2) ? new int[]{pageNumber} : new int[]{};
            int[] bodyAfter = (pageNumber > 2 && pageNumber < totalPages - 3) ? new int[]{pageNumber+1, pageNumber+2} : new int[]{};
            int[] tail = (pageNumber < totalPages - 3) ? new int[]{-1, totalPages} : new int[] {totalPages-2, totalPages-1, totalPages};
            body = MainController.merge(head, bodyBefore, bodyCenter, bodyAfter, tail);
        } else {
            body = new int[bookPage.getTotalPages()];
            for (int i = 0; i < bookPage.getTotalPages(); i++) {
                body[i] = 1+i;
            }
        }
        return body;
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
                appBook.getBookimg(),
                appBook.getQrimg(),
                appBook.getBookfile(),
                appBook.isElectronic()
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


