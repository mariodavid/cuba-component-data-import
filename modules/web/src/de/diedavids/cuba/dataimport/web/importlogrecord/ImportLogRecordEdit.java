package de.diedavids.cuba.dataimport.web.importlogrecord;

import com.haulmont.cuba.gui.screen.*;
import de.diedavids.cuba.dataimport.entity.ImportLogRecord;

@UiController("ddcdi$ImportLogRecord.edit")
@UiDescriptor("import-log-record-edit.xml")
@EditedEntityContainer("importExecutionRecordDc")
@LoadDataBeforeShow
public class ImportLogRecordEdit extends StandardEditor<ImportLogRecord> {
}