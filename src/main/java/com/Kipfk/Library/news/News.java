package com.Kipfk.Library.news;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class News {
    @SequenceGenerator(
            name = "news_sequence",
            sequenceName = "news_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "news_sequence"
    )
    private Long id;
    private String title;
    private byte[] newsPhoto;
    @Column(columnDefinition="text", length=10485760)
    private String description;
    private byte[] newsFile;
    private LocalDate createdAt;

}
