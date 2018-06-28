package de.diedavids.cuba.dataimport.entity.example.sales;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NamePattern("%s|name")
@Table(name = "DDCDI_PRODUCT")
@Entity(name = "ddcdi$Product")
public class Product extends StandardEntity {
    private static final long serialVersionUID = 6590520188494047540L;

    @NotNull
    @Column(name = "PRODUCT_ID", nullable = false, unique = true)
    protected String productId;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    protected ProductCategory category;

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public ProductCategory getCategory() {
        return category;
    }


    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}