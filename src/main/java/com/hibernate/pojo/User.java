package com.hibernate.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ：ltb
 * @date ：2020/9/4
 */
@Entity
@Table(name = "user_")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    @Column(name = "name")
    String name;

    @ManyToMany(cascade = CascadeType.REFRESH, fetch=FetchType.EAGER)
    @JoinTable(name="user_product",
            joinColumns=@JoinColumn(name="uid"),
            inverseJoinColumns=@JoinColumn(name="pid"))
    private Set<Product> products = new HashSet<>();


}
