package de.diedavids.cuba.dataimport.web.importattributemapper

import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.gui.components.AbstractFrame
import com.haulmont.cuba.gui.components.FieldGroup
import com.haulmont.cuba.gui.data.Datasource
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
import de.diedavids.cuba.dataimport.converter.MetaPropertyMatcher
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import de.diedavids.cuba.dataimport.web.util.MetadataSelector

import javax.inject.Inject

class ImportAttributeMapperAutomaticFrame extends AbstractFrame {

    @Inject
    Datasource<ImportAttributeMapper> importAttributeMapperDs

    @Inject
    Metadata metadata

    @Inject
    MetaPropertyMatcher metaPropertyMatcher

    @Inject
    private ComponentsFactory componentsFactory

    @Inject
    private FieldGroup fieldGroup

    @Inject
    private MetadataSelector metadataSelector


    EntityAttributeSelectionHandler entityAttributeSelectionHandler

    @Override
    void init(Map<String, Object> params) {

        entityAttributeSelectionHandler = new EntityAttributeSelectionHandler(
                messages: messages,
                metadata: metadata,
                metadataSelector: metadataSelector,
                importAttributeMapperDs: importAttributeMapperDs,
                componentsFactory: componentsFactory,
                fieldGroup: fieldGroup,
        )


        entityAttributeSelectionHandler.registerLookupChangeListener()

        entityAttributeSelectionHandler.initEntityAttributeLookupFields()
    }



}