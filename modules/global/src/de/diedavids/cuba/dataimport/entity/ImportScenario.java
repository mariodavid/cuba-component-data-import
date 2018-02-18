package de.diedavids.cuba.dataimport.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.FileDescriptor;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import java.util.List;
import javax.persistence.OneToMany;

@NamePattern("%s|name")
@Table(name = "DDCDI_IMPORT_SCENARIO")
@Entity(name = "ddcdi$ImportScenario")
public class ImportScenario extends StandardEntity {
    private static final long serialVersionUID = -4678826366994897550L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @OneToMany(mappedBy = "scenario")
    protected List<ImportLog> logs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMPLATE_ID")
    protected FileDescriptor template;

    @Lob
    @Column(name = "COMMENT_")
    protected String comment;

    @NotNull
    @Column(name = "IMPORTER_BEAN_NAME", nullable = false)
    protected String importerBeanName;

    public void setLogs(List<ImportLog> logs) {
        this.logs = logs;
    }

    public List<ImportLog> getLogs() {
        return logs;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTemplate(FileDescriptor template) {
        this.template = template;
    }

    public FileDescriptor getTemplate() {
        return template;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setImporterBeanName(String importerBeanName) {
        this.importerBeanName = importerBeanName;
    }

    public String getImporterBeanName() {
        return importerBeanName;
    }


}