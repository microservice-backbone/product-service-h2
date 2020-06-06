package com.backbone.core.demo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
public class Product extends RepresentationModel implements Serializable {

    //todo: separate product header and detail classes!

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // disables HIBERNATE_SEQUENCE
    private int id;

    private String category;
    private String title;
    private String subTitle;
    private String brand;
    private int rating;
    private String shortDescription;
    private String description;

    //todo: that creates unnecessary REVIEW and PRODUCT_REVIEWS table. but not a big deal?
    @OneToMany(fetch = FetchType.EAGER)
    private List<Review> reviews;
}
