package de.diedavids.cuba.dataimport.core;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import de.diedavids.cuba.dataimport.entity.ImportLog;
import de.diedavids.cuba.dataimport.entity.ImportLogRecord;
import de.diedavids.cuba.dataimport.entity.LogRecordLevel;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;

/**
 * Created by aleksey on 21/10/2016.
 */
public class ImportLogHelper {

    protected ImportLog importLog;
    protected Log log;
    protected Metadata metadata;

    public ImportLogHelper(Class logClazz, ImportLog importLog) {
        this.importLog = importLog;
        metadata =  AppBeans.get(Metadata.NAME);
        log = LogFactory.getLog(logClazz);
    }

    public void error(String message, Throwable t) {
        writeLogRecord(message, LogRecordLevel.ERROR, t);
        log.error(message, t);
    }

    public void warn(String message, Throwable t) {
        writeLogRecord(message, LogRecordLevel.WARN, t);
        log.warn(message, t);
    }

    public void debug(String message, Throwable t) {
        writeLogRecord(message, LogRecordLevel.DEBUG, t);
        log.debug(message, t);
    }

    private ImportLogRecord writeLogRecord(String message, LogRecordLevel level, Throwable t) {
        ImportLogRecord record = metadata.create(ImportLogRecord.class);
        record.setImportLog(importLog);
        record.setLevel(level);
        record.setMessage(message);
        record.setTime(new Date());

        if (t != null) {
            record.setStacktrace(t.getLocalizedMessage()
                    + "\n" + ExceptionUtils.getStackTrace(t));
        }

        importLog.getRecords().add(record);

        return record;
    }

    public void moreEntitiesProcessed(int entitiesPersisted) {
        importLog.setEntitiesProcessed(entitiesPersisted
                + (importLog.getEntitiesProcessed() != null ? importLog.getEntitiesProcessed() : 0));
    }

    public ImportLog getImportLog() {
        return importLog;
    }

}
