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
import de.diedavids.cuba.dataimport.entity.ImportExecution;
import de.diedavids.cuba.dataimport.service.GenericDataImporterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
    public ImportExecution importFromFile(ImportConfiguration importConfiguration, FileDescriptor fileToImport) throws FileStorageException {
        return importFromFile(importConfiguration, fileToImport, Collections.emptyMap());
    }

    @Override
    public ImportExecution importFromFile(ImportConfiguration importConfiguration, FileDescriptor fileToImport, Map<String, Object> defaultValues) throws FileStorageException {
        return importFromFile(importConfiguration, fileToImport, Collections.emptyMap(),null);
    }

    @Override
    public ImportExecution importFromFile(
            ImportConfiguration importConfiguration,
            FileDescriptor fileToImport,
            Map<String, Object> defaultValues,
            Consumer<EntityImportView> importViewCustomization
    ) throws FileStorageException {

        boolean fileExists = fileStorageAPI.fileExists(fileToImport);

        if (!fileExists) {
            throw new FileStorageException(FileStorageException.Type.FILE_NOT_FOUND, "File descriptor does not exist");
        }

        String fileCharset = importConfiguration.getFileCharset();
        String chosenFileCharset = fileCharset != null ? fileCharset : StandardCharsets.UTF_8.name();

        ImportDataConverter converter = dataConverterFactory.createTableDataConverter(fileToImport);

        try {
            ImportData importData = converter.convert(
                    tempFileFromFileDescriptor(fileToImport),
                    chosenFileCharset
            );
            return genericDataImporterService.doDataImport(
                    importConfiguration,
                    importData,
                    defaultValues,
                    importViewCustomization
            );
        } catch (IOException e) {
            log.error("provided file charset of import configuration is not supported: " + chosenFileCharset, e);
            return createFailedImportExecution(importConfiguration);
        }
    }

    private File tempFileFromFileDescriptor(FileDescriptor fileToImport) throws FileStorageException, IOException {
        byte[] fileBytes = fileStorageAPI.loadFile(fileToImport);
        File tempFile = File.createTempFile("tempImportFile", "tmp");
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(fileBytes);

        return tempFile;
    }


    private ImportExecution createFailedImportExecution(ImportConfiguration importConfiguration) {
        ImportExecution importExecution = metadata.create(ImportExecution.class);
        importExecution.setConfiguration(importConfiguration);
        importExecution.setSuccess(false);
        return importExecution;
    }

}
