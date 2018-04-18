package de.diedavids.cuba.dataimport.core.example;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.global.FileStorageException;
import de.diedavids.cuba.dataimport.core.DbHelper;
import de.diedavids.cuba.dataimport.core.xls.XlsHelper;
import de.diedavids.cuba.dataimport.core.xls.XlsImporter;
import de.diedavids.cuba.dataimport.entity.example.Customer;
import de.diedavids.cuba.dataimport.entity.example.CustomerPriority;
import de.diedavids.cuba.dataimport.exception.ImportFileEofEvaluationException;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aleksey on 18/10/2016.
 */

@Component(MlbPlayerImporter.NAME)
@Scope("prototype")
public class MlbPlayerImporter extends XlsImporter {

    public static final String NAME = "importer_MlbPlayerImporter";

    public static final String FIRST_NAME = "firstName";
    public static final String SECOND_NAME = "secondName";
    public static final String PRIORITY = "priority";
    public static final String DESCRIPTION = "description";
    public static final String DOCUMENT = "documentNumber";

    @Override
    protected void init() throws IOException, FileStorageException {
        super.init();
        firstDataRowIndex = 1;
    }

    @Override
    protected Map<String, Integer> createAttributesToColumns() {
        Map<String, Integer> columns = new HashMap<>();

        columns.put(FIRST_NAME, 0);
        columns.put(SECOND_NAME, 1);
        columns.put(DOCUMENT, 2);
        columns.put(PRIORITY, 3);
        columns.put(DESCRIPTION, 4);

        return columns;
    }

    @Override
    protected Map<String, Object> createDefaultValues() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put(PRIORITY, CustomerPriority.MEDIUM.toString());
        return defaults;
    }

    @Override
    protected boolean eof(Row row) throws ImportFileEofEvaluationException {
        return eofByColumnNullValue(row, FIRST_NAME);
    }

    @Override
    protected List<BaseGenericIdEntity> getEntitiesToPersist(Map<String, Object> values, Map<String, Object> params) throws Exception {
        List<BaseGenericIdEntity> result = new ArrayList<>();
        Persistence persistence = getPersistence();

        try (Transaction trx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            Customer customer = DbHelper.existedEntity(em, Customer.class,
                    ParamsMap.of(DOCUMENT, XlsHelper.getParameterValue(values, DOCUMENT)));

            //create customer if not found in the database
            if (customer == null) {
                customer = metadata.create(Customer.class);
            }

            // set date for a new customer, or update if customer already exists
            customer.setFirstName(XlsHelper.getParameterValue(values, FIRST_NAME));
            customer.setName(XlsHelper.getParameterValue(values, SECOND_NAME));
            customer.setDocumentNumber(XlsHelper.getParameterValue(values, DOCUMENT));
            customer.setDescription(XlsHelper.getParameterValue(values, DESCRIPTION));
            customer.setPriority(parsePriority(XlsHelper.getParameterValue(values, PRIORITY)));

            result.add(customer);

            return result;
        }
    }

    private CustomerPriority parsePriority(String input) throws IllegalArgumentException {
        if ("middle".equalsIgnoreCase(input) || "medium".equalsIgnoreCase(input))
            return CustomerPriority.MEDIUM;
        else if ("high".equalsIgnoreCase(input))
            return CustomerPriority.HIGH;
        else if ("low".equalsIgnoreCase(input))
            return CustomerPriority.LOW;
        else {
            String defValue = defaultValues.get(PRIORITY).toString();
            String logMessage = String.format("Priority string [%s] is not supported at row %s", input, currentRowIndex);
            if (defValue != null) {
                logMessage += String.format("\nDefault value will be set [%s]", defValue);
                logHelper.warn(logMessage, null);
                return CustomerPriority.valueOf(defaultValues.get(PRIORITY).toString());
            } else {
                IllegalArgumentException e = new IllegalArgumentException(logMessage);
                logHelper.error(logMessage, e);
                throw e;
            }
        }
    }

}
