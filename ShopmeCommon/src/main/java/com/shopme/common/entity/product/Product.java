package com.shopme.common.entity.product;

import com.shopme.common.Constants;
import com.shopme.common.IdBasedEntity;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Review;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product extends IdBasedEntity {
    @Column(unique = true, length = 256, nullable = false)
    private String name;
    @Column(unique = true, length = 256, nullable = false)
    private String alias;
    @Column(nullable = false, name = "short_description",length = Integer.MAX_VALUE)
    private String shortDescription;
    @Column(nullable = false, name = "full_description",length = Integer.MAX_VALUE)
    private String fullDescription;
    @Column(name = "created_time")
    private Date createdTime;
    @Column(name = "updated_time")
    private Date updatedTime;
    private boolean enabled;
    private float cost;
    private float price;
    private float sale;
    private float averageRating;
    private int reviewCount;

    @Column(name = "main_image",nullable = false)
    private String mainImage;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<ProductDetail> productDetails = new HashSet<>();

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<ProductImage> productImages = new HashSet<>();

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<>();

    @Transient private boolean customerCanReview;
    @Transient private boolean reviewedByCustomer;

    public Product(Integer id) {
        this.id = id;
    }

    public Product(String name) {
        this.name = name;
    }

    @Transient
    public String getMainImagePath() {
        if (this.id == null || this.mainImage.isEmpty() ) return Constants.S3_BASE_URI + "/images/default-user.png";
        return Constants.S3_BASE_URI +"/product-images/" + this.id +  "/" + this.mainImage;
    }

    public void addDetail(String detailName,String detailValue) {
        this.productDetails.add(new ProductDetail(detailName,detailValue,this));
    }
    public void addDetail(Integer id, String name, String value) {
        this.productDetails.add(new ProductDetail(id,name,value,this));
    }

    public void addExtraImage(String name) {
        this.productImages.add(new ProductImage(name,this));
    }


    public boolean containsImageName(String fileName) {
        Iterator<ProductImage> iterator = productImages.iterator();
        while (iterator.hasNext()) {
            ProductImage image = iterator.next();
            if (image.getName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    @Transient
    public String getFormatFloat() {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(price);
    }


    @Transient
    public float getDiscountPrice() {
        return price;
    }


}
