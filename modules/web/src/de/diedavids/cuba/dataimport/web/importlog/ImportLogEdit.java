package de.diedavids.cuba.dataimport.web.importlog;

import com.haulmont.cuba.gui.screen.*;
import de.diedavids.cuba.dataimport.entity.ImportLog;

@UiController("ddcdi$ImportLog.edit")
@UiDescriptor("import-log-edit.xml")
@EditedEntityContainer("importLogDc")
@LoadDataBeforeShow
public class ImportLogEdit extends StandardEditor<ImportLog> {
}