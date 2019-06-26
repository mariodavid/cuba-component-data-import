package de.diedavids.cuba.dataimport.core;

import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.app.importexport.EntityImportView;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.Metadata;
import de.diedavids.cuba.dataimport.DataImportAPI;
import de.diedavids.cuba.dataimport.converter.DataConverterFactory;
import de.diedavids.cuba.dataimport.converter.ImportDataConverter;
import de.diedavids.cuba.dataimport.dto.ImportData;
import de.diedavids.cuba.dataimport.entity.ImportConfiguration;
import de.diedavids.cuba.dataimport.entity.ImportLog;
import de.diedavids.cuba.dataimport.service.GenericDataImporterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

@Component(DataImportAPI.NAME)
public class DataImport implements DataImportAPI {

    private static final Logger log = LoggerFactory.getLogger(DataImport.class);

    @Inject
    protected GenericDataImporterService genericDataImporterService;
    @Inject
    protected FileStorageAPI fileStorageAPI;
    @Inject
    protected Metadata metadata;
    @Inject
    private DataConverterFactory dataConverterFactory;

    @Override
    public ImportLog importFromFile(ImportConfiguration importConfiguration, FileDescriptor fileToImport) throws FileStorageException {
        return importFromFile(importConfiguration, fileToImport, Collections.emptyMap(), null);
    }

    @Override
    public ImportLog importFromFile(ImportConfiguration importConfiguration, FileDescriptor fileToImport, Map<String, Object> defaultValues, Consumer<EntityImportView> importViewCustomization) throws FileStorageException {

        boolean fileExists = fileStorageAPI.fileExists(fileToImport);

        if (!fileExists) {
            throw new FileStorageException(FileStorageException.Type.FILE_NOT_FOUND, "File descriptor does not exist");
        }

        byte[] fileBytes = fileStorageAPI.loadFile(fileToImport);
        String fileCharset = importConfiguration.getFileCharset();

        ImportDataConverter converter = dataConverterFactory.createTableDataConverter(fileToImport);

        try {
            String fileContentString = new String(fileBytes, fileCharset);
            ImportData importData = converter.convert(fileContentString);
            return genericDataImporterService.doDataImport(
                    importConfiguration,
                    importData,
                    defaultValues,
                    importViewCustomization
            );
        } catch (UnsupportedEncodingException e) {
            log.error("provided file charset of import configuration is not supported: " + fileCharset, e);
            return createFailedImportLog(importConfiguration);
        }
    }


    private ImportLog createFailedImportLog(ImportConfiguration importConfiguration) {
        ImportLog importLog = metadata.create(ImportLog.class);
        importLog.setConfiguration(importConfiguration);
        importLog.setSuccess(false);
        return importLog;
    }

}
