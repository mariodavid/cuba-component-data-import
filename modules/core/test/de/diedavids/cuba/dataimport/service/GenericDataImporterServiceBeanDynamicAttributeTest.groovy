package de.diedavids.cuba.dataimport.service

import com.haulmont.cuba.core.app.dynamicattributes.DynamicAttributes
import com.haulmont.cuba.core.app.dynamicattributes.DynamicAttributesCacheService
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
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

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
    }


    @Test
    void "doDataImport imports dynamic attributes when they are marked with a plus sign in the mapper"() {

        //given:
        Category category = metadata.create(Category)
        category.entityType = 'ddcdit$MlbTeam'
        category.name = 'Stadium Information'
        List<CategoryAttribute> categoryAttrs = []

        def categoryAttribute = metadata.create(CategoryAttribute)
        categoryAttribute.category = category
        categoryAttribute.name = 'stadiumName'
        categoryAttribute.code = 'stadiumName'
        categoryAttribute.categoryEntityType = 'ddcdit$MlbTeam'
        categoryAttribute.dataType = PropertyType.STRING
        categoryAttrs << categoryAttribute
        category.setCategoryAttrs(categoryAttrs)

        CommitContext commitContext = new CommitContext()
        commitContext.addInstanceToCommit(category)
        commitContext.addInstanceToCommit(categoryAttribute)

        dataManager.commit(commitContext)



        assertThat(simpleDataLoader.loadAll(Category).size()).isEqualTo(1)

        dynamicAttributesManagerAPI.loadCache()

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbTeam',
                importAttributeMappers: [
                        new ImportAttributeMapper(entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0),
                        new ImportAttributeMapper(entityAttribute: '+stadiumName', fileColumnAlias: 'stadiumName', fileColumnNumber: 1),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )

        ImportData importData = createData([
                [name: 'Baltimore Orioles', stadiumName: 'Oriole Park at Camden Yards']
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


}
