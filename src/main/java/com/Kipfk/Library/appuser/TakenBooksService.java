package com.Kipfk.Library.appuser;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TakenBooksService {
    private final TakenBooksRepository takenBooksRepository;

    public TakenBooksService(TakenBooksRepository takenBooksRepository) {
        this.takenBooksRepository = takenBooksRepository;
    }

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
