package de.diedavids.cuba.dataimport.web.importexecution;

import com.haulmont.cuba.core.global.Sort;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import de.diedavids.cuba.dataimport.entity.ImportExecution;
import de.diedavids.cuba.dataimport.entity.ImportExecutionDetail;

import javax.inject.Inject;

@UiController("ddcdi$ImportExecution.edit")
@UiDescriptor("import-execution-edit.xml")
@EditedEntityContainer("importExecutionDc")
@LoadDataBeforeShow
public class ImportExecutionEdit extends StandardEditor<ImportExecution> {

    @Inject
    protected CollectionLoader<ImportExecutionDetail> importExecutionDetailsDl;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        importExecutionDetailsDl.setParameter("importExecution", getEditedEntity());
        importExecutionDetailsDl.setSort(Sort.by(Sort.Order.asc("time")));
        importExecutionDetailsDl.load();
    }

}