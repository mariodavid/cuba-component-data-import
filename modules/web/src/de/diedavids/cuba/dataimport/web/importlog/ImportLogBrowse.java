package de.diedavids.cuba.dataimport.web.importlog;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Sort;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import de.diedavids.cuba.dataimport.entity.ImportLog;

import javax.inject.Inject;

@UiController("ddcdi$ImportLog.browse")
@UiDescriptor("import-log-browse.xml")
@LookupComponent("importLogsTable")
public class ImportLogBrowse extends StandardLookup<ImportLog> {

    @Inject
    protected ExportDisplay exportDisplay;
    @Inject
    protected CollectionLoader<ImportLog> importLogsDl;

    public void downloadFile(Entity item, String columnId) {
        exportDisplay.show(((ImportLog) item).getFile());
    }

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        importLogsDl.setSort(Sort.by(Sort.Order.desc("startedAt")));
        importLogsDl.load();
    }
    
    
}