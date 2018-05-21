package de.diedavids.cuba.dataimport.web.action

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.gui.components.Component
import com.haulmont.cuba.gui.components.ListComponent
import com.haulmont.cuba.gui.components.actions.BaseAction

class TableWithImportAction extends BaseAction {


    WithImportBean withImportBean = AppBeans.<WithImportBean> get(WithImportBean)

    @SuppressWarnings('ThisReferenceEscapesConstructor')
    TableWithImportAction(ListComponent listComponent) {
        super('startImport')
        target = listComponent
        withImportBean.setIcon(this)
        withImportBean.setCaption(this)
        withImportBean.setDs(target.datasource)
    }

    @Override
    void actionPerform(Component component) {
        withImportBean.openImportWizard(target.frame, targetMetaClass)
    }

    MetaClass getTargetMetaClass() {
        target.datasource.metaClass
    }
}
