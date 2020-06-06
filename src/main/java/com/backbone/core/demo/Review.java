package com.backbone.core.demo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;


@Entity
@Data
@EqualsAndHashCode(callSuper=false)
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

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
