package de.diedavids.cuba.dataimport.web.util

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.cuba.core.app.dynamicattributes.DynamicAttributes
import com.haulmont.cuba.core.entity.CategoryAttribute
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.*
import com.haulmont.cuba.security.entity.EntityOp
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.inject.Inject

@Component('ddcdi_MetadataSelector')
@CompileStatic
class MetadataSelector {


    @Inject
    Metadata metadata

    @Inject
    Messages messages

    @Inject
    Security security

    @Inject
    DynamicAttributes dynamicAttributes


    Map<String, Object> getEntitiesLookupFieldOptions() {
        Map<String, Object> options = new TreeMap<>()

        for (MetaClass metaClass : metadataTools.allPersistentMetaClasses) {
            if (readPermitted(metaClass)) {
                Class javaClass = metaClass.javaClass
                if (Entity.isAssignableFrom(javaClass)) {
                    options.put(messageTools.getEntityCaption(metaClass) + ' (' + metaClass.name + ')', metaClass.name)
                }
            }
        }

        options
    }


    Map<String, Object> getAllAttributesLookupFieldOptions(MetaClass entityMetaClass) {
        getLookupMetaProperties(entityMetaClass.properties)
    }

    Map<String, Object> getDynamicAttributesLookupFieldOptions(MetaClass entityMetaClass) {

        Collection<CategoryAttribute> dynamicAttributesForImportConfiguration = dynamicAttributes.getAttributesForMetaClass(entityMetaClass)

        dynamicAttributesForImportConfiguration.collectEntries {
            ["${it.name} (${it.code})".toString(), it.name]
        }
    }
    Map<String, Object> getDirectAttributesLookupFieldOptions(MetaClass entityMetaClass) {

        def directMetaProperties = metaPropertiesOfType(entityMetaClass, MetaProperty.Type.DATATYPE) + metaPropertiesOfType(entityMetaClass, MetaProperty.Type.ENUM)
        getLookupMetaProperties(directMetaProperties)
    }

    Map<String, Object> getLookupMetaProperties(Collection<MetaProperty> metaProperties) {
        metaProperties.collectEntries {
            ["${messageTools.getPropertyCaption(it)} (${it.name})".toString(), it.name]
        }
    }

    Map<String, Object> getAssociationAttributes(MetaClass entityMetaClass) {

        def associationMetaProperties = metaPropertiesOfType(entityMetaClass, MetaProperty.Type.ASSOCIATION)

        getLookupMetaProperties(associationMetaProperties)
    }

    private Collection<MetaProperty> metaPropertiesOfType(MetaClass entityMetaClass, MetaProperty.Type metaPropertyType) {
        entityMetaClass.properties.findAll {
            it.type == metaPropertyType
        }
    }

    protected boolean readPermitted(MetaClass metaClass) {
        entityOpPermitted(metaClass, EntityOp.READ)
    }

    protected boolean entityOpPermitted(MetaClass metaClass, EntityOp entityOp) {
        security.isEntityOpPermitted(metaClass, entityOp)
    }


    private MessageTools getMessageTools() {
        messages.tools
    }

    private MetadataTools getMetadataTools() {
        metadata.tools
    }

}