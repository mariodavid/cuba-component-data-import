package de.diedavids.cuba.dataimport.web.importwizard;

import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.gui.data.impl.CustomValueCollectionDatasource;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ImportPreviewDatasource extends CustomValueCollectionDatasource {

    @Override
    protected Collection<KeyValueEntity> getEntities(Map<String, Object> params) {

        List<KeyValueEntity> data = (List<KeyValueEntity>) params.get("data");
        return data;
    }

}