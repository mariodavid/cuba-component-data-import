package de.diedavids.cuba.dataimport.service;

import com.haulmont.cuba.core.entity.Entity;
import de.diedavids.cuba.dataimport.entity.UniqueConfiguration;

import java.util.Collection;


public interface UniqueEntityFinderService {
    String NAME = "ddcdi_UniqueEntityFinderService";

    Entity findEntity(Entity entity, Collection<UniqueConfiguration> uniqueConfigurations);
    Entity findEntity(Entity entity, UniqueConfiguration uniqueConfiguration);
}