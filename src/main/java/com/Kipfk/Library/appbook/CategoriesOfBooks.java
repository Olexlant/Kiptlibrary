package com.Kipfk.Library.appbook;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class CategoriesOfBooks {

    @Id
    @GeneratedValue
    private Long id;

    private String name;


}
