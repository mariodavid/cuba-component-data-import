package de.diedavids.cuba.dataimport.service

import com.haulmont.cuba.core.app.dynamicattributes.DynamicAttributes
import com.haulmont.cuba.core.app.dynamicattributes.DynamicAttributesManagerAPI
import com.haulmont.cuba.core.app.dynamicattributes.PropertyType
import com.haulmont.cuba.core.entity.Category
import com.haulmont.cuba.core.entity.CategoryAttribute
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.CommitContext
import com.haulmont.cuba.core.global.LoadContext
import de.diedavids.cuba.dataimport.AbstractImportIntegrationTest
import de.diedavids.cuba.dataimport.data.SimpleDataLoader
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportTransactionStrategy
import de.diedavids.cuba.dataimport.entity.example.MlbTeam
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.fail

class GenericDataImporterServiceBeanDynamicAttributeTest extends AbstractImportIntegrationTest {


    protected GenericDataImporterService sut

    protected ImportConfiguration importConfiguration

    protected SimpleDataLoader simpleDataLoader

    protected DynamicAttributes dynamicAttributes

    protected DynamicAttributesManagerAPI dynamicAttributesManagerAPI


    @Before
    void setUp() throws Exception {
        super.setUp()

        sut = AppBeans.get(GenericDataImporterService.NAME)
        simpleDataLoader = AppBeans.get(SimpleDataLoader.NAME)

        dynamicAttributes = AppBeans.get(DynamicAttributes.NAME)

        dynamicAttributesManagerAPI = AppBeans.get(DynamicAttributesManagerAPI.NAME)

        clearTable("DDCDI_MLB_TEAM")
        clearTable("SYS_ATTR_VALUE")
        clearTable("SYS_CATEGORY_ATTR")
        clearTable("SYS_CATEGORY")


        dynamicAttributesManagerAPI.loadCache()
    }


    @Test
    void "doDataImport imports dynamic attributes when they are marked with a plus sign in the mapper"() {

        //given:
        createAndStoreDynamicAttribute('ddcdi$MlbTeam', 'Stadium Information', 'stadiumName')


        assertThat(simpleDataLoader.loadAll(Category).size()).isEqualTo(1)

        dynamicAttributesManagerAPI.loadCache()

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbTeam',
                importAttributeMappers: [
                        new ImportAttributeMapper(entityAttribute: 'name', fileColumnAlias: 'name'),
                        new ImportAttributeMapper(entityAttribute: 'code', fileColumnAlias: 'code'),
                        new ImportAttributeMapper(entityAttribute: '+stadiumName', fileColumnAlias: 'stadiumName', dynamicAttribute: true),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )

        ImportData importData = createData([
                [name: 'Baltimore Orioles', code: 'BAL', stadiumName: 'Oriole Park at Camden Yards']
        ])

        sut.doDataImport(importConfiguration, importData)

        LoadContext<MlbTeam> loadContext = LoadContext.create(MlbTeam)
                .setQuery(LoadContext.createQuery('select e from ddcdi$MlbTeam e'))
                .setLoadDynamicAttributes(true)
        def mlbTeams = dataManager.loadList(loadContext);

        def baltimoreTeam = mlbTeams.first()

        assertThat(baltimoreTeam.name).isEqualTo("Baltimore Orioles")
        assertThat(baltimoreTeam.getValue("+stadiumName")).isEqualTo("Oriole Park at Camden Yards")

    }


    private void createAndStoreDynamicAttribute(String entityClass,String categoryName, String attributeName) {
        Category category = metadata.create(Category)
        category.entityType = entityClass
        category.name = categoryName
        List<CategoryAttribute> categoryAttrs = []

        def categoryAttribute = metadata.create(CategoryAttribute)
        categoryAttribute.category = category
        categoryAttribute.name = attributeName
        categoryAttribute.code = attributeName
        categoryAttribute.categoryEntityType = entityClass
        categoryAttribute.dataType = PropertyType.STRING

        categoryAttrs << categoryAttribute
        category.setCategoryAttrs(categoryAttrs)

        CommitContext commitContext = new CommitContext()
        commitContext.addInstanceToCommit(category)
        commitContext.addInstanceToCommit(categoryAttribute)

        dataManager.commit(commitContext)
    }




}
