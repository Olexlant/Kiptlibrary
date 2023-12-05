package com.Kipfk.Library.images;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Images {
    @SequenceGenerator(
            name = "image_files_sequence",
            sequenceName = "image_files_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "image_files_sequence"
    )
    private Long id;
    private byte[] imageFile;
    private String imageFileName;
    private String imageFileContentType;
}
