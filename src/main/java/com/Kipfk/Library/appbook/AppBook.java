package com.Kipfk.Library.appbook;

import com.Kipfk.Library.appuser.AppUser;
import com.Kipfk.Library.appuser.LikedBooks;
import com.Kipfk.Library.appuser.TakenBooks;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.tomcat.jni.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity

public class AppBook {
    @SequenceGenerator(
            name = "book_sequence",
            sequenceName = "book_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "book_sequence"
    )
    private Long id;
    private Long qrid;
    private String title;
    private String author;
    private Long year;
    private Long stilaj;
    private Long polka;
    private byte[] bookimg;
    private byte[] qrimg;
    private byte[] bookfile;
    private String bookfileurl;
    @Column(columnDefinition="text", length=10485760)
    private String description;
    private Long count;

    public AppBook(Long qrid, String title, String author, Long year, Long stilaj, Long polka, byte[] bookimg, byte[] qrimg,byte[] bookfile){
        this.qrid = qrid;
        this.title = title;
        this.author = author;
        this.year = year;
        this.stilaj = stilaj;
        this.polka = polka;
        this.bookimg = bookimg;
        this.qrimg = qrimg;
        this.bookfile = bookfile;
    }
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "book", orphanRemoval = true)
    private Set<LikedBooks> likedBooks;
    @OneToMany(mappedBy = "book")
    private Set<TakenBooks> takenBooks;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "book", orphanRemoval = true)
    private Set<BooksByGroups> booksByGroups;


    public Long getId() {
        return id;
    }

    public Long getQrid() {
        return qrid;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Long getYear() {
        return year;
    }

    public Long getStilaj() {
        return stilaj;
    }

    public Long getPolka() {
        return polka;
    }

    public byte[] getBookimg() { return bookimg; }

    public byte[] getQrimg() { return qrimg; }
    public byte[] getBookfile() {return bookfile; }

}
