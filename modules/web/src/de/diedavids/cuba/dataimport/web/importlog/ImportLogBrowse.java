package de.diedavids.cuba.dataimport.web.importlog;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.screen.*;
import de.diedavids.cuba.dataimport.entity.ImportLog;

import javax.inject.Inject;

@UiController("ddcdi$ImportLog.browse")
@UiDescriptor("import-log-browse.xml")
@LookupComponent("importLogsTable")
@LoadDataBeforeShow
public class ImportLogBrowse extends StandardLookup<ImportLog> {


    @Inject
    protected ExportDisplay exportDisplay;

    public void downloadFile(Entity item, String columnId) {
        exportDisplay.show(((ImportLog) item).getFile());
    }
}