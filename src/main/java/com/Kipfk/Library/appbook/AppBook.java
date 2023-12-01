package com.Kipfk.Library.appbook;

import com.Kipfk.Library.appuser.AppUser;
import com.Kipfk.Library.appuser.LikedBooks;
import com.Kipfk.Library.appuser.TakenBooks;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.tomcat.jni.User;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Size;
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
    private String qrid;
    private String title;
    private String author;
    private Long year;

    @Basic(fetch=FetchType.LAZY)
    private byte[] bookimg;

    @Basic(fetch=FetchType.LAZY)
    private byte[] qrimg;

    @Basic(fetch=FetchType.LAZY)
    private Long bookFileId;
    private String bookFileUrl;
    @Column(columnDefinition="text", length=10485760)
    private String description;
    private Long count;
    private Long daysToReturn;
    private boolean electronic = false;

    public AppBook(String qrid, String title, String author, Long year, byte[] bookimg, byte[] qrimg,Long bookFileId,boolean electronic){
        this.qrid = qrid;
        this.title = title;
        this.author = author;
        this.year = year;
        this.bookimg = bookimg;
        this.qrimg = qrimg;
        this.bookFileId = bookFileId;
        this.electronic = electronic;
    }
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "book", orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<LikedBooks> likedBooks;
    @OneToMany(mappedBy = "book")
    @Fetch(FetchMode.SUBSELECT)
    private List<TakenBooks> takenBooks= new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "book", orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<BooksByGroups> booksByGroups;


}
