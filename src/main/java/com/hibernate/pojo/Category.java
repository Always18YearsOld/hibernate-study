package com.hibernate.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ：ltb
 * @date ：2020/9/4
 */
@Entity
@Table(name = "category_")
@Data
@AllArgsConstructor
@NoArgsConstructor
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Category {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "category")
//    @JoinColumn
//    @JoinTable(name = "product_", joinColumns = {@JoinColumn(name = "id",referencedColumnName = "cid")})
    private List<Product> products;// = new HashSet<>();
}
