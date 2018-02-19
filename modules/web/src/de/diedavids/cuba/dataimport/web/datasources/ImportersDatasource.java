package de.diedavids.cuba.dataimport.web.datasources;


import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;
import de.diedavids.cuba.dataimport.entity.Importer;
import de.diedavids.cuba.dataimport.service.DataImportService;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * Created by aleksey on 20/10/2016.
 */
public class ImportersDatasource extends CustomCollectionDatasource<Importer, UUID> {

    private DataImportService importerService = AppBeans.get(DataImportService.NAME);

    @Override
    protected Collection<Importer> getEntities(Map<String, Object> params) {
        return importerService.getImporters();
    }
}
