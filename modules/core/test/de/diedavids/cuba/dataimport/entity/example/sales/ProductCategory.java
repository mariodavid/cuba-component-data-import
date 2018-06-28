package de.diedavids.cuba.dataimport.entity.example.sales;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NamePattern("%s|name")
@Table(name = "DDCDI_PRODUCT_CATEGORY")
@Entity(name = "ddcdi$ProductCategory")
public class ProductCategory extends StandardEntity {
    private static final long serialVersionUID = -520449303854413L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @NotNull
    @Column(name = "CODE", nullable = false)
    protected String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    protected ProductCategory parent;

    public void setParent(ProductCategory parent) {
        this.parent = parent;
    }

    public ProductCategory getParent() {
        return parent;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }


}