package de.diedavids.cuba.dataimport.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import de.diedavids.cuba.dataimport.ImporterAPI;
import de.diedavids.cuba.dataimport.entity.ImportLog;
import de.diedavids.cuba.dataimport.entity.Importer;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Service(DataImportService.NAME)
public class DataImportServiceBean implements DataImportService {
    @Inject
    private Metadata metadata;

    @Inject
    private Persistence persistence;

    @Override
    public Set<Importer> getImporters() {
        Set<Importer> result = new LinkedHashSet<>();

        Map<String, ImporterAPI> importerBeans = AppBeans.getAll(ImporterAPI.class);

        for (String beanName : importerBeans.keySet()) {
            Importer importer = metadata.create(Importer.class);
            importer.setBeanName(beanName);
            result.add(importer);
        }

        return result;
    }


    @Override
    public ImportLog doImport(ImportLog log, Map<String, Object> params, boolean doPersistLog) {
        log.setStartedAt(new Date());
        try {
            ImporterAPI importerAPI = AppBeans.get(log.getScenario().getImporterBeanName());
            importerAPI.setFileDescriptor(log.getFile());

            log = importerAPI.doImport(log, params);

        } finally {
            log.setFinishedAt(new Date());

            if (doPersistLog)
                persistLog(log);
        }
        return log;
    }


    protected ImportLog persistLog(ImportLog log) {
        ImportLog result = log;
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager em = persistence.getEntityManager();

            if (PersistenceHelper.isNew(log))
                em.persist(log);
            else
                result = em.merge(log);

            log.getRecords().forEach(em::persist);
            tx.commit();
        }
        return result;
    }
}