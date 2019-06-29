package de.diedavids.cuba.dataimport.web.importlog;

import com.haulmont.cuba.core.global.Sort;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import de.diedavids.cuba.dataimport.entity.ImportLog;
import de.diedavids.cuba.dataimport.entity.ImportLogRecord;

import javax.inject.Inject;

@UiController("ddcdi$ImportLog.edit")
@UiDescriptor("import-log-edit.xml")
@EditedEntityContainer("importLogDc")
@LoadDataBeforeShow
public class ImportLogEdit extends StandardEditor<ImportLog> {

    @Inject
    protected CollectionLoader<ImportLogRecord> importLogRecordsDl;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        importLogRecordsDl.setParameter("importLog", getEditedEntity());
        importLogRecordsDl.setSort(Sort.by(Sort.Order.asc("time")));
        importLogRecordsDl.load();
    }

}