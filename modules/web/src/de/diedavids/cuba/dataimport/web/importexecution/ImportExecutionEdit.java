package de.diedavids.cuba.dataimport.web.importexecution;

import com.haulmont.cuba.core.global.Sort;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import de.diedavids.cuba.dataimport.entity.ImportExecution;
import de.diedavids.cuba.dataimport.entity.ImportLogRecord;

import javax.inject.Inject;

@UiController("ddcdi$ImportExecution.edit")
@UiDescriptor("import-execution-edit.xml")
@EditedEntityContainer("importExecutionDc")
@LoadDataBeforeShow
public class ImportExecutionEdit extends StandardEditor<ImportExecution> {

    @Inject
    protected CollectionLoader<ImportLogRecord> importExecutionRecordsDl;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        importExecutionRecordsDl.setParameter("importExecution", getEditedEntity());
        importExecutionRecordsDl.setSort(Sort.by(Sort.Order.asc("time")));
        importExecutionRecordsDl.load();
    }

}