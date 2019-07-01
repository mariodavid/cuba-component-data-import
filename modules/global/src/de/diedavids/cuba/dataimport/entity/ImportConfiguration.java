package de.diedavids.cuba.dataimport.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.datatypes.FormatStrings;
import com.haulmont.chile.core.datatypes.FormatStringsRegistry;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.UserSessionSource;
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.List;

@NamePattern("%s|name")
@Table(name = "DDCDI_IMPORT_CONFIGURATION")
@Entity(name = "ddcdi$ImportConfiguration")
public class ImportConfiguration extends StandardEntity {
    private static final long serialVersionUID = -4678826366994897550L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @NotNull
    @Column(name = "TRANSACTION_STRATEGY", nullable = false)
    protected String transactionStrategy;

    @NotNull
    @Column(name = "ENTITY_CLASS", nullable = false)
    protected String entityClass;

    @Column(name = "AD_HOC")
    protected Boolean adHoc;

    @Transient
    @MetaProperty
    protected Boolean reuse;

    @OneToMany(mappedBy = "configuration")
    protected List<ImportExecution> logs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMPLATE_ID")
    protected FileDescriptor template;

    @Lob
    @Column(name = "COMMENT_")
    protected String comment;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "configuration")
    protected List<ImportAttributeMapper> importAttributeMappers;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "importConfiguration")
    protected List<UniqueConfiguration> uniqueConfigurations;

    @Column(name = "DATE_FORMAT")
    protected String dateFormat;

    @Column(name = "BOOLEAN_TRUE_VALUE")
    protected String booleanTrueValue;

    @Column(name = "BOOLEAN_FALSE_VALUE")
    protected String booleanFalseValue;

    @Lob
    @Column(name = "PRE_COMMIT_SCRIPT")
    protected String preCommitScript;

    @Column(name = "FILE_CHARSET")
    protected String fileCharset = StandardCharsets.UTF_8.name();

    public String getFileCharset() {
        return fileCharset;
    }

    public void setFileCharset(String fileCharset) {
        this.fileCharset = fileCharset;
    }

    public void setTransactionStrategy(ImportTransactionStrategy transactionStrategy) {
        this.transactionStrategy = transactionStrategy == null ? null : transactionStrategy.getId();
    }

    public ImportTransactionStrategy getTransactionStrategy() {
        return transactionStrategy == null ? null : ImportTransactionStrategy.fromId(transactionStrategy);
    }


    public void setPreCommitScript(String preCommitScript) {
        this.preCommitScript = preCommitScript;
    }

    public String getPreCommitScript() {
        return preCommitScript;
    }


    public void setUniqueConfigurations(List<UniqueConfiguration> uniqueConfigurations) {
        this.uniqueConfigurations = uniqueConfigurations;
    }

    public List<UniqueConfiguration> getUniqueConfigurations() {
        return uniqueConfigurations;
    }


    public Boolean getReuse() {
        return reuse;
    }

    public void setReuse(Boolean reuse) {
        this.reuse = reuse;
    }


    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setBooleanTrueValue(String booleanTrueValue) {
        this.booleanTrueValue = booleanTrueValue;
    }

    public String getBooleanTrueValue() {
        return booleanTrueValue;
    }

    public void setBooleanFalseValue(String booleanFalseValue) {
        this.booleanFalseValue = booleanFalseValue;
    }

    public String getBooleanFalseValue() {
        return booleanFalseValue;
    }


    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    public String getEntityClass() {
        return entityClass;
    }


    public void setAdHoc(Boolean adHoc) {
        this.adHoc = adHoc;
    }

    public Boolean getAdHoc() {
        return adHoc;
    }


    public void setImportAttributeMappers(List<ImportAttributeMapper> importAttributeMappers) {
        this.importAttributeMappers = importAttributeMappers;
    }

    public List<ImportAttributeMapper> getImportAttributeMappers() {
        return importAttributeMappers;
    }


    public void setLogs(List<ImportExecution> logs) {
        this.logs = logs;
    }

    public List<ImportExecution> getLogs() {
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


    @PostConstruct
    protected void initDefaultValues() {
        initDefaultTransactionStrategy();
        initDefaultFormats();
    }

    private void initDefaultTransactionStrategy() {
        setTransactionStrategy(ImportTransactionStrategy.SINGLE_TRANSACTION);
    }

    private void initDefaultFormats() {
        FormatStrings formatStrings = getFormatStrings();
        setDateFormat(formatStrings.getDateFormat());
        setBooleanTrueValue(formatStrings.getTrueString());
        setBooleanFalseValue(formatStrings.getFalseString());
    }

    private FormatStrings getFormatStrings() {
        UserSessionSource userSessionSource = AppBeans.get(UserSessionSource.NAME);
        FormatStringsRegistry formatStringsRegistry = AppBeans.get(FormatStringsRegistry.NAME);
        return formatStringsRegistry.getFormatStrings(userSessionSource.getLocale());
    }

}