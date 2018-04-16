package de.diedavids.cuba.dataimport

import com.haulmont.bali.db.QueryRunner
import com.haulmont.cuba.core.Persistence
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Metadata
import de.diedavids.cuba.dataimport.binding.EntityBinder
import de.diedavids.cuba.dataimport.dto.DataRowImpl
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.dto.ImportDataImpl
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.service.UniqueEntityFinderService
import org.junit.Before
import org.junit.ClassRule

import java.sql.SQLException

abstract class AbstractDbIntegrationTest {

    @ClassRule
    public static DdcdiTestContainer cont = DdcdiTestContainer.Common.INSTANCE

    protected Metadata metadata
    protected Persistence persistence
    protected DataManager dataManager

    @Before
    void setUp() throws Exception {
        metadata = cont.metadata()
        persistence = cont.persistence()
        dataManager = AppBeans.get(DataManager.class)
    }

    void clearTable(String tableName) {
        String sql = "delete from $tableName"
        QueryRunner runner = new QueryRunner(persistence.getDataSource());
        try {
            runner.update(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

}
