package de.diedavids.cuba.dataimport.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Table(name = "DDCDI_IMPORT_LOG_RECORD")
@Entity(name = "ddcdi$ImportLogRecord")
public class ImportLogRecord extends StandardEntity {
    private static final long serialVersionUID = -8403007601995115328L;

    @NotNull
    @Column(name = "MESSAGE", nullable = false)
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
    @JoinColumn(name = "IMPORT_LOG_ID")
    protected ImportLog importLog;

    public void setImportLog(ImportLog importLog) {
        this.importLog = importLog;
    }

    public ImportLog getImportLog() {
        return importLog;
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