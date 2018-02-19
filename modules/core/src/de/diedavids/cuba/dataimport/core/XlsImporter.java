package de.diedavids.cuba.dataimport.core;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import de.diedavids.cuba.dataimport.ImporterAPI;
import de.diedavids.cuba.dataimport.core.example.ImportLogHelper;
import de.diedavids.cuba.dataimport.core.example.xls.XlsHelper;
import de.diedavids.cuba.dataimport.entity.ImportLog;
import de.diedavids.cuba.dataimport.exception.ImportFileEofEvaluationException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aleksey on 18/10/2016.
 */
public abstract class XlsImporter implements ImporterAPI {

    private Sheet sheet = null;
    private int sheetIndex = 0;

    protected FileDescriptor fileDescriptor;
    protected Persistence persistence;
    protected Metadata metadata;
    protected TimeSource timeSource;

    protected int firstDataRowIndex = 0;
    protected int dataRowIncrement = 1;
    protected Map<String, Integer> attributesToColumns;
    protected Map<String, Object> defaultValues;
    protected ImportLogHelper logHelper;
    protected int currentRowIndex;

    public ImportLog doImport(ImportLog importLog, Map<String, Object> params) {
        logHelper =  new ImportLogHelper(this.getClass(), importLog);

        try {
            init();
        } catch (FileStorageException | IOException e) {
            logHelper.error("Excel workbook was not initialized", e);
            return logHelper.getImportLog();
        }

        int entitiesPersisted = 0;

        try {
            for (currentRowIndex = firstDataRowIndex; !eof(sheet.getRow(currentRowIndex)); currentRowIndex += dataRowIncrement) {
                try {
                    Row row = sheet.getRow(currentRowIndex);
                    Map<String, Object> values = new HashMap<>();

                    for (String attribute : attributesToColumns.keySet()) {
                        Cell cell = row.getCell(attributesToColumns.get(attribute));
                        Object value = XlsHelper.getCellValue(cell);
                        values.put(attribute, value);
                    }

                    for (String attribute : defaultValues.keySet()) {
                        if (!values.keySet().contains(attribute)) {
                            values.put(attribute, defaultValues.get(attribute));
                        } else values.putIfAbsent(attribute, defaultValues.get(attribute));
                    }

                    List<BaseGenericIdEntity> entitiesToPersist = getEntitiesToPersist(values, params);
                    try {
                        entitiesToPersist.forEach(this::persistEntity);
                        entitiesPersisted += entitiesToPersist.size();
                    } catch (Exception e) {
                        logHelper.error(String.format("Error while persisting entities for row: %s", currentRowIndex), e);
                    }

                    afterEntitiesPersisted(entitiesToPersist);
                } catch (Throwable t) {
                    logHelper.error(String.format("Error while processing row: %s", currentRowIndex), t);
                }
            }
        } catch (ImportFileEofEvaluationException e) {
            logHelper.error(e.getMessage(), e);
        }
        logHelper.moreEntitiesProcessed(entitiesPersisted);

        return logHelper.getImportLog();
    }

    protected void init() throws FileStorageException, IOException {
        FileStorageAPI fileStorageAPI = AppBeans.get(FileStorageAPI.NAME);
        byte[] xlsFile = fileStorageAPI.loadFile(fileDescriptor);
        Workbook workbook = XlsHelper.openWorkbook(xlsFile);

        if (workbook == null)
            throw new FileStorageException(FileStorageException.Type.FILE_NOT_FOUND, "File was not loaded");

        sheet = workbook.getSheetAt(sheetIndex);
        persistence = AppBeans.get(Persistence.NAME);
        metadata = AppBeans.get(Metadata.NAME);
        timeSource = AppBeans.get(TimeSource.NAME);

        attributesToColumns = createAttributesToColumns();
        defaultValues = createDefaultValues();
    }

    /**
     * Key - string attribute name
     * Value - attributes' column number
     *
     * @return map with attribute - columnNumber pairs
     */
    protected abstract Map<String, Integer> createAttributesToColumns();

    /**
     * If some cell misses value, or that cell's column is not specified via {@link #createAttributesToColumns()},
     * it will be initialized with the default value, if any specified.
     *
     * @return
     */
    protected abstract Map<String, Object> createDefaultValues();

    /**
     * @return true if current row is end of file; false otherwise
     * @throws Exception
     */
    protected abstract boolean eof(Row row) throws ImportFileEofEvaluationException;

    /**
     * Convert given map to list of entities that should be persisted.
     *
     * @throws Exception
     */
    protected abstract List<BaseGenericIdEntity> getEntitiesToPersist(Map<String, Object> values, Map<String, Object> params) throws Exception;

    protected Boolean eofByColumnNullValue(Row row, String columnAlias) throws ImportFileEofEvaluationException {
        Integer columnNumber = attributesToColumns.get(columnAlias);
        if (row != null) {
            Cell cell = row.getCell(columnNumber);
            Object cellValue;
            try {
                cellValue = XlsHelper.getCellValue(cell);
            } catch (Exception e) {
                throw new ImportFileEofEvaluationException(String.format("Eof evaluation has failed on row %s", row.getRowNum()));
            }
            return cellValue == null;
        }
        return true;
    }

    protected void persistEntity(BaseGenericIdEntity entity) {
        try (Transaction tx = getPersistence().getTransaction()) {
            EntityManager em = getPersistence().getEntityManager();
            if (PersistenceHelper.isNew(entity)) {
                em.persist(entity);
            } else {
                em.merge(entity);
            }
            tx.commit();
        }
    }

    /**
     * Hook, invoked after all entities are persisted.
     */
    protected void afterEntitiesPersisted(List persistedEntities){
    }

    public Integer getFirstDataRowIndex() {
        return firstDataRowIndex;
    }

    public void setFirstDataRowIndex(Integer firstDataRowIndex) {
        this.firstDataRowIndex = firstDataRowIndex;
    }

    public int getDataRowIncrement() {
        return dataRowIncrement;
    }

    public void setDataRowIncrement(int dataRowIncrement) {
        this.dataRowIncrement = dataRowIncrement;
    }

    public Persistence getPersistence() {
        return persistence;
    }

    public Integer getSheetIndex() {
        return sheetIndex;
    }

    public void setSheetIndex(Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public void setFileDescriptor(FileDescriptor fileDescriptor) {
        this.fileDescriptor = fileDescriptor;
    }

    public FileDescriptor getFileDescriptor(){
        return fileDescriptor;
    }
}
