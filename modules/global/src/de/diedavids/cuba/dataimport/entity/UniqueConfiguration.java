package de.diedavids.cuba.dataimport.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Table(name = "DDCDI_UNIQUE_CONFIGURATION")
@Entity(name = "ddcdi$UniqueConfiguration")
public class UniqueConfiguration extends StandardEntity {
    private static final long serialVersionUID = 2124633657969899092L;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "uniqueConfiguration")
    protected List<UniqueConfigurationAttribute> entityAttributes;

    @NotNull
    @Column(name = "POLICY", nullable = false)
    protected String policy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IMPORT_CONFIGURATION_ID")
    protected ImportConfiguration importConfiguration;

    public void setImportConfiguration(ImportConfiguration importConfiguration) {
        this.importConfiguration = importConfiguration;
    }

    public ImportConfiguration getImportConfiguration() {
        return importConfiguration;
    }


    public void setPolicy(UniquePolicy policy) {
        this.policy = policy == null ? null : policy.getId();
    }

    public UniquePolicy getPolicy() {
        return policy == null ? null : UniquePolicy.fromId(policy);
    }


    public void setEntityAttributes(List<UniqueConfigurationAttribute> entityAttributes) {
        this.entityAttributes = entityAttributes;
    }

    public List<UniqueConfigurationAttribute> getEntityAttributes() {
        return entityAttributes;
    }


}