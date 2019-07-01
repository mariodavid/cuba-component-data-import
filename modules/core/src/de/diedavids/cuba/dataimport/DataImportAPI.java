package de.diedavids.cuba.dataimport;

import com.haulmont.cuba.core.app.importexport.EntityImportView;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.FileStorageException;
import de.diedavids.cuba.dataimport.entity.ImportConfiguration;
import de.diedavids.cuba.dataimport.entity.ImportExecution;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Interface to import data from files via a import configuration
 */
public interface DataImportAPI {

    String NAME = "ddcdi_DataImportAPI";

    /**
     * imports data from a file descriptor configured by the import configuration
     *
     * @param importConfiguration the import configuration which defines how to to import the data
     * @param fileToImport a reference to a persisted file descriptor, that is already stored in the DB before
     * @return the import log containing information about the import process
     * @throws FileStorageException in case an error occurred during reading the file
     */
    ImportExecution importFromFile(ImportConfiguration importConfiguration, FileDescriptor fileToImport) throws FileStorageException;


    /**
     * imports data from a file descriptor configured by the import configuration with default values
     *
     * @param importConfiguration the import configuration which defines how to to import the data
     * @param fileToImport a reference to a persisted file descriptor, that is already stored in the DB before
     * @param defaultValues the default values that should be populated to the created entities
     * @return the import log containing information about the import process
     * @throws FileStorageException in case an error occurred during reading the file
     */
    ImportExecution importFromFile(ImportConfiguration importConfiguration, FileDescriptor fileToImport, Map<String, Object> defaultValues) throws FileStorageException;

    /**
     * imports data from a file descriptor configured by the import configuration with default values
     *
     * @param importConfiguration the import configuration which defines how to to import the data
     * @param fileToImport a reference to a persisted file descriptor, that is already stored in the DB before
     * @param defaultValues the default values that should be populated to the created entities
     * @param importViewCustomization consumer which receives the EntityImportView instance for adjusting the attributes
     *                                that are part of the import process
     * @return the import log containing information about the import process
     * @throws FileStorageException in case an error occurred during reading the file
     */
    ImportExecution importFromFile(ImportConfiguration importConfiguration, FileDescriptor fileToImport, Map<String, Object> defaultValues, Consumer<EntityImportView> importViewCustomization) throws FileStorageException;
}