package com.backbone.core.demo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
@Data
@EqualsAndHashCode(callSuper=false)
public class Review {

    @Id
    private int id;

    private String userName;
    private int productId;

    private String title;
    private int rating;
    private boolean isVerifiedPurchase;
    private boolean isHelpful;
    private boolean isAbuse;
    //    private List<String> tags;
    private String description;
}
