package de.diedavids.cuba.dataimport.binding

import com.haulmont.bali.db.QueryRunner
import com.haulmont.cuba.core.Persistence
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Metadata
import de.diedavids.cuba.dataimport.DdcdiTestContainer
import de.diedavids.cuba.dataimport.dto.DataRowImpl
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.dto.ImportDataImpl
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportTransactionStrategy
import org.junit.Before
import org.junit.ClassRule

import java.sql.SQLException

abstract class AbstractEntityBinderIntegrationTest {

    @ClassRule
    public static DdcdiTestContainer cont = DdcdiTestContainer.Common.INSTANCE

    protected Metadata metadata
    protected Persistence persistence
    protected DataManager dataManager

    protected EntityBinder sut

    protected ImportConfiguration importConfiguration

    @Before
    void setUp() throws Exception {
        metadata = cont.metadata()
        persistence = cont.persistence()
        dataManager = AppBeans.get(DataManager.class)

        sut = AppBeans.get(EntityBinder.NAME)

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0),
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'height', fileColumnAlias: 'height', fileColumnNumber: 2),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
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

    ImportData createData(List<Map<String, Object>> data) {
        ImportData importData = new ImportDataImpl()

        data.each {
            def dataRow = DataRowImpl.ofMap(it)
            importData.rows.add(dataRow)
        }

        importData
    }

}