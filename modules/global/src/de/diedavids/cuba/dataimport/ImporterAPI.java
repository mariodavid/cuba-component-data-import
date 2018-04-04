package de.diedavids.cuba.dataimport;

import com.haulmont.cuba.core.entity.FileDescriptor;
import de.diedavids.cuba.dataimport.dto.ImportData;
import de.diedavids.cuba.dataimport.entity.ImportConfiguration;
import de.diedavids.cuba.dataimport.entity.ImportLog;

import java.util.Map;

/**
 * Created by aleksey on 20/10/2016.
 */
public interface ImporterAPI {

    /**
     *
     * @param fileDescriptor Descriptor of the file to import from
     */
    void setFileDescriptor(FileDescriptor fileDescriptor);

    /**
     * @param params
     * @return Number of entities persisted
     */
    ImportLog doImport(ImportLog log, Map<String, Object> params);

    ImportLog doDataImport(ImportConfiguration importConfiguration, ImportData importData);
}
