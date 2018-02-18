package de.diedavids.cuba.dataimport.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s %s|beanName,description")
@MetaClass(name = "ddcdi$Importer")
public class Importer extends BaseUuidEntity {
    private static final long serialVersionUID = -3373048833927947846L;

    @NotNull
    @MetaProperty(mandatory = true)
    protected String beanName;

    @MetaProperty
    protected String description;

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}