package de.diedavids.cuba.dataimport.web.importattributemapper

import com.haulmont.cuba.gui.components.AbstractEditor
import com.haulmont.cuba.gui.components.HBoxLayout
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeMapperMode
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper

import javax.inject.Inject

class ImportAttributeMapperEdit extends AbstractEditor<ImportAttributeMapper> {


    @Inject
    HBoxLayout contentBox


    @Override
    protected void postInit() {
        if (item.mapperMode == AttributeMapperMode.CUSTOM) {
            openFrame(contentBox, 'ddcdi$ImportAttributeMapperCustom.frame')
        }
        else {
            openFrame(contentBox, 'ddcdi$ImportAttributeMapperAutomatic.frame')
        }
    }


}