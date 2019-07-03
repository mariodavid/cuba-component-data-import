package de.diedavids.cuba.dataimport.web.importexecutiondetail;

import com.haulmont.cuba.gui.screen.*;
import de.diedavids.cuba.dataimport.entity.ImportExecutionDetail;

@UiController("ddcdi$ImportExecutionDetail.edit")
@UiDescriptor("import-execution-detail-edit.xml")
@EditedEntityContainer("importExecutionDetailDc")
@LoadDataBeforeShow
public class ImportExecutionDetailEdit extends StandardEditor<ImportExecutionDetail> {
}