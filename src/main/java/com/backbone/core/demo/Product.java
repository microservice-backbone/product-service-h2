package com.backbone.core.demo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
public class Product extends RepresentationModel {

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
}
