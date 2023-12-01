package com.Kipfk.Library.bookFiles;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Entity
public class BookFiles {

    @SequenceGenerator(
            name = "book_files_sequence",
            sequenceName = "book_files_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "book_sequence"
    )
    private Long id;
    private Long appBookId;
    private byte[] bookFile;
    private String bookFileName;
    private String bookFileContentType;


}
