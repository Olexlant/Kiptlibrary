package com.Kipfk.Library.appuser;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Groups {

    @SequenceGenerator(
            name = "groups_sequence",
            sequenceName = "groups_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "groups_sequence"
    )
    private Long id;

    private String name;

    @OneToMany(mappedBy = "groups",cascade = CascadeType.ALL)
    private List<AppUser> users;

}
