package de.diedavids.cuba.dataimport.service;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.View;
import de.diedavids.cuba.dataimport.entity.UniqueConfiguration;

import java.util.Collection;


public interface UniqueEntityFinderService {
    String NAME = "ddcdi_UniqueEntityFinderService";

    Entity findEntity(Entity entity, Collection<UniqueConfiguration> uniqueConfigurations, View targetView);
    Entity findEntity(Entity entity, UniqueConfiguration uniqueConfiguration, View targetView);
}