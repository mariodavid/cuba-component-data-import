package de.diedavids.cuba.dataimport.web.importexecution;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Sort;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import de.diedavids.cuba.dataimport.entity.ImportExecution;

import javax.inject.Inject;

@UiController("ddcdi$ImportExecution.browse")
@UiDescriptor("import-execution-browse.xml")
@LookupComponent("importExecutionsTable")
public class ImportExecutionBrowse extends StandardLookup<ImportExecution> {

    @Inject
    protected ExportDisplay exportDisplay;
    @Inject
    protected CollectionLoader<ImportExecution> importExecutionsDl;

    public void downloadFile(Entity item, String columnId) {
        exportDisplay.show(((ImportExecution) item).getFile());
    }

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        importExecutionsDl.setSort(Sort.by(Sort.Order.desc("startedAt")));
        importExecutionsDl.load();
    }
    
    
}