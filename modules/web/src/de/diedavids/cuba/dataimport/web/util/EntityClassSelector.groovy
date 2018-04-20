package de.diedavids.cuba.dataimport.web.util

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.*
import com.haulmont.cuba.security.entity.EntityOp
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.inject.Inject

@Component('ddcdi_EntityClassSelector')
@CompileStatic
class EntityClassSelector {


    @Inject
    Metadata metadata

    @Inject
    Messages messages

    @Inject
    Security security


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

    private MessageTools getMessageTools() {
        messages.tools
    }

    private MetadataTools getMetadataTools() {
        metadata.tools
    }




    protected boolean readPermitted(MetaClass metaClass) {
        entityOpPermitted(metaClass, EntityOp.READ)
    }

    protected boolean entityOpPermitted(MetaClass metaClass, EntityOp entityOp) {
        security.isEntityOpPermitted(metaClass, entityOp)
    }
}