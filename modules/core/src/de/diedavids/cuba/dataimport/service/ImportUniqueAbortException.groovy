package de.diedavids.cuba.dataimport.service

import com.haulmont.cuba.core.entity.Entity

class ImportUniqueAbortException extends RuntimeException {

    Entity alreadyExistingEntity

    ImportEntityRequest importEntityRequest


}
