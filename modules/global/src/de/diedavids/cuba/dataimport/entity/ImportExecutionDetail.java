package de.diedavids.cuba.dataimport.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "DDCDI_IMPORT_EXEC_DETAIL")
@Entity(name = "ddcdi$ImportExecutionDetail")
public class ImportExecutionDetail extends StandardEntity {
    private static final long serialVersionUID = -8403007601995115328L;

    @NotNull
    @Column(name = "MESSAGE", nullable = false, length = 4000)
    protected String message;

    @NotNull
    @Column(name = "LEVEL_", nullable = false)
    protected String level;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(name = "TIME_", nullable = false)
    protected Date time;

    @Lob
    @Column(name = "STACKTRACE")
    protected String stacktrace;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IMPORT_EXEC_ID")
    protected ImportExecution importExecution;

    @Lob
    @Column(name = "DATA_ROW")
    protected String dataRow;

    @Column(name = "DATA_ROW_INDEX")
    protected Integer dataRowIndex;

    @Lob
    @Column(name = "ENTITY_INSTANCE")
    protected String entityInstance;

    @Column(name = "CATEGORY")
    protected String category;

    public ImportExecutionDetailCategory getCategory() {
        return category == null ? null : ImportExecutionDetailCategory.fromId(category);
    }

    public void setCategory(ImportExecutionDetailCategory category) {
        this.category = category == null ? null : category.getId();
    }

    public String getEntityInstance() {
        return entityInstance;
    }

    public void setEntityInstance(String entityInstance) {
        this.entityInstance = entityInstance;
    }

    public Integer getDataRowIndex() {
        return dataRowIndex;
    }

    public void setDataRowIndex(Integer dataRowIndex) {
        this.dataRowIndex = dataRowIndex;
    }

    public String getDataRow() {
        return dataRow;
    }

    public void setDataRow(String dataRow) {
        this.dataRow = dataRow;
    }

    public void setImportExecution(ImportExecution importExecution) {
        this.importExecution = importExecution;
    }

    public ImportExecution getImportExecution() {
        return importExecution;
    }


    public void setLevel(LogRecordLevel level) {
        this.level = level == null ? null : level.getId();
    }

    public LogRecordLevel getLevel() {
        return level == null ? null : LogRecordLevel.fromId(level);
    }


    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getTime() {
        return time;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    public String getStacktrace() {
        return stacktrace;
    }


}