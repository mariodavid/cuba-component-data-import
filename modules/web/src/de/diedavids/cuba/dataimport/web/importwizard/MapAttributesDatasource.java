package de.diedavids.cuba.dataimport.web.importwizard;

import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MapAttributesDatasource extends CustomCollectionDatasource<ImportAttributeMapper, UUID> {

    @Override
    protected Collection<ImportAttributeMapper> getEntities(Map<String, Object> params) {

        List<ImportAttributeMapper> data = (List<ImportAttributeMapper>) params.get("data");
        return data;
    }

}