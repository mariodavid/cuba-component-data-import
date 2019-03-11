package de.diedavids.cuba.dataimport.web.action

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.gui.components.Component
import com.haulmont.cuba.gui.components.ListComponent
import com.haulmont.cuba.gui.components.actions.BaseAction

class TableWithImportAction extends BaseAction {


    WithImportBean withImportBean = AppBeans.<WithImportBean> get(WithImportBean)
    private final ListComponent target

    @SuppressWarnings('ThisReferenceEscapesConstructor')
    TableWithImportAction(ListComponent target) {
        super('startImport')
        this.target = target
        withImportBean.setIcon(this)
        withImportBean.setCaption(this)
    }

    @Override
    void actionPerform(Component component) {
        withImportBean.openImportWizard(target, targetMetaClass)
    }

    MetaClass getTargetMetaClass() {
        target.datasource.metaClass
    }
}
