package de.diedavids.cuba.dataimport

import com.haulmont.bali.db.QueryRunner
import com.haulmont.cuba.core.Persistence
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Metadata
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
