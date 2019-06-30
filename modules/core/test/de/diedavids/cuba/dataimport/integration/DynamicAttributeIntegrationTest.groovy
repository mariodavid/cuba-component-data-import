package de.diedavids.cuba.dataimport.integration

import com.haulmont.cuba.core.app.dynamicattributes.DynamicAttributes
import com.haulmont.cuba.core.app.dynamicattributes.DynamicAttributesManagerAPI
import com.haulmont.cuba.core.app.dynamicattributes.PropertyType
import com.haulmont.cuba.core.entity.Category
import com.haulmont.cuba.core.entity.CategoryAttribute
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.CommitContext
import com.haulmont.cuba.core.global.LoadContext
import de.diedavids.cuba.dataimport.AbstractImportIntegrationTest
import de.diedavids.cuba.dataimport.binding.DatatypeFactory
import de.diedavids.cuba.dataimport.data.SimpleDataLoader
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportTransactionStrategy
import de.diedavids.cuba.dataimport.entity.example.mlb.MlbTeam
import de.diedavids.cuba.dataimport.service.GenericDataImporterService
import org.junit.Before
import org.junit.Test

import java.time.LocalDate

import static org.assertj.core.api.Assertions.assertThat

class DynamicAttributeIntegrationTest extends AbstractImportIntegrationTest {


    protected GenericDataImporterService sut

    protected ImportConfiguration importConfiguration

    protected SimpleDataLoader simpleDataLoader

    protected DynamicAttributes dynamicAttributes

    protected DynamicAttributesManagerAPI dynamicAttributesManagerAPI

    protected DatatypeFactory datatypeFactory


    @Before
    void setUp() throws Exception {
        super.setUp()

        sut = AppBeans.get(GenericDataImporterService.NAME)
        simpleDataLoader = AppBeans.get(SimpleDataLoader.NAME)

        dynamicAttributes = AppBeans.get(DynamicAttributes.NAME)
        datatypeFactory = AppBeans.get(DatatypeFactory.NAME)
        dynamicAttributesManagerAPI = AppBeans.get(DynamicAttributesManagerAPI.NAME)


        clearTable("DDCDI_MLB_TEAM")
        clearTable("SYS_ATTR_VALUE")
        clearTable("SYS_CATEGORY_ATTR")
        clearTable("SYS_CATEGORY")


        dynamicAttributesManagerAPI.loadCache()
    }


    @Test
    void "doDataImport can import a string based dynamic attribute"() {

        //given:
        createAndStoreDynamicAttribute(
                entityClass:  'ddcdi$MlbTeam',
                categoryName: 'Stadium Information',
                attributeName: 'stadiumName',
                dataType: PropertyType.STRING
        )


        importConfiguration = importConfigurationWithDynamicAttribute("+stadiumName", 'stadiumName')

        ImportData importData = createData([
                [name: 'Baltimore Orioles', code: 'BAL', stadiumName: 'Oriole Park at Camden Yards']
        ])

        //when:
        sut.doDataImport(importConfiguration, importData)

        //then:
        def baltimoreTeam = loadMlbTeamWithDynamicAttributes()

        assertThat(baltimoreTeam.getValue("+stadiumName")).isEqualTo("Oriole Park at Camden Yards")

    }

    @Test
    void "doDataImport can import an Integer based dynamic attribute"() {

        //given:
        createAndStoreDynamicAttribute(
                entityClass:  'ddcdi$MlbTeam',
                categoryName: 'Stadium Information',
                attributeName: 'stadiumSeats',
                dataType: PropertyType.INTEGER
        )


        importConfiguration = importConfigurationWithDynamicAttribute("+stadiumSeats", 'stadiumSeats')

        ImportData importData = createData([
                [name: 'Baltimore Orioles', code: 'BAL', stadiumSeats: 10000]
        ])

        //when:
        sut.doDataImport(importConfiguration, importData)

        //then:
        def baltimoreTeam = loadMlbTeamWithDynamicAttributes()

        assertThat(baltimoreTeam.getValue("+stadiumSeats")).isEqualTo(10000)

    }

    @Test
    void "doDataImport can import a date based dynamic attribute"() {

        //given:
        createAndStoreDynamicAttribute(
                entityClass:  'ddcdi$MlbTeam',
                categoryName: 'Stadium Information',
                attributeName: 'builtAt',
                dataType: PropertyType.DATE
        )


        importConfiguration = importConfigurationWithDynamicAttribute("+builtAt", 'builtAt')
        importConfiguration.dateFormat = "dd.MM.yyyy"

        ImportData importData = createData([
                [name: 'Baltimore Orioles', code: 'BAL', builtAt: "03.04.2010"]
        ])

        //when:
        sut.doDataImport(importConfiguration, importData)

        //then:
        def baltimoreTeam = loadMlbTeamWithDynamicAttributes()


        assertThat(baltimoreTeam.getValue("+builtAt")).isEqualTo(LocalDate.of(2010,4,3).toDate())

    }

    @Test
    void "doDataImport can import an Integer based dynamic attribute from a String"() {

        //given:
        createAndStoreDynamicAttribute(
                entityClass:  'ddcdi$MlbTeam',
                categoryName: 'Stadium Information',
                attributeName: 'stadiumSeats',
                dataType: PropertyType.INTEGER
        )

        importConfiguration = importConfigurationWithDynamicAttribute("+stadiumSeats", 'stadiumSeats')

        ImportData importData = createData([
                [name: 'Baltimore Orioles', code: 'BAL', stadiumSeats: "10000"]
        ])

        //when:
        sut.doDataImport(importConfiguration, importData)

        //then:
        def baltimoreTeam = loadMlbTeamWithDynamicAttributes()

        assertThat(baltimoreTeam.getValue("+stadiumSeats")).isEqualTo(10000)

    }


    @Test
    void "doDataImport can import a boolean based dynamic attribute from a String using the boolean attribute configuration"() {

        //given:
        createAndStoreDynamicAttribute(
                entityClass:  'ddcdi$MlbTeam',
                categoryName: 'Stadium Information',
                attributeName: 'hasRoof',
                dataType: PropertyType.BOOLEAN
        )

        importConfiguration = importConfigurationWithDynamicAttribute("+hasRoof", 'hasRoof')
        importConfiguration.booleanTrueValue = "Yes"
        importConfiguration.booleanFalseValue = "No"

        ImportData importData = createData([
                [name: 'Baltimore Orioles', code: 'BAL', hasRoof: "Yes"]
        ])

        //when:
        sut.doDataImport(importConfiguration, importData)

        //then:
        def baltimoreTeam = loadMlbTeamWithDynamicAttributes()

        assertThat(baltimoreTeam.getValue("+hasRoof")).isEqualTo(true)

    }


    @Test
    void "doDataImport can import an enum based dynamic attribute"() {

        //given:
        createAndStoreDynamicAttribute(
                entityClass:  'ddcdi$MlbTeam',
                categoryName: 'Stadium Information',
                attributeName: 'stadiumSize',
                dataType: PropertyType.ENUMERATION,
                enumValues: 'SMALL,MEDIUM,BIG'
        )

        importConfiguration = importConfigurationWithDynamicAttribute('+stadiumSize', 'stadiumSize')

        ImportData importData = createData([
                [name: 'Baltimore Orioles', code: 'BAL', stadiumSize: "BIG"]
        ])

        //when:
        sut.doDataImport(importConfiguration, importData)

        //then:
        def baltimoreTeam = loadMlbTeamWithDynamicAttributes()

        assertThat(baltimoreTeam.getValue("+stadiumSize")).isEqualTo("BIG")

    }

    private MlbTeam loadMlbTeamWithDynamicAttributes() {
        LoadContext<MlbTeam> loadContext = LoadContext.create(MlbTeam)
                .setQuery(LoadContext.createQuery('select e from ddcdi$MlbTeam e'))
                .setLoadDynamicAttributes(true)
        dataManager.loadList(loadContext).first()
    }

    private CategoryAttribute createAndStoreDynamicAttribute(Map<String, Object> options) {
        Category category = metadata.create(Category)
        category.entityType = options.entityClass
        category.name = options.categoryName
        List<CategoryAttribute> categoryAttrs = []

        def categoryAttribute = metadata.create(CategoryAttribute)
        categoryAttribute.category = category
        categoryAttribute.name = options.attributeName
        categoryAttribute.code = options.attributeName
        categoryAttribute.categoryEntityType = options.entityClass
        categoryAttribute.dataType = options.dataType
        categoryAttribute.enumeration = options.enumValues
        categoryAttribute.entityClass = options.referenceEntityClass

        categoryAttrs << categoryAttribute
        category.setCategoryAttrs(categoryAttrs)

        CommitContext commitContext = new CommitContext()
        commitContext.addInstanceToCommit(category)
        commitContext.addInstanceToCommit(categoryAttribute)

        dataManager.commit(commitContext)

        assertThat(simpleDataLoader.loadAll(Category).size()).isEqualTo(1)

        dynamicAttributesManagerAPI.loadCache()

        categoryAttribute
    }




    private ImportConfiguration importConfigurationWithDynamicAttribute(String dynamicAttributeName, String fileColumnAlias) {
        new ImportConfiguration(
                entityClass: 'ddcdi$MlbTeam',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE,entityAttribute: 'name', fileColumnAlias: 'name'),
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE,entityAttribute: 'code', fileColumnAlias: 'code'),
                        new ImportAttributeMapper(attributeType: AttributeType.DYNAMIC_ATTRIBUTE, entityAttribute: dynamicAttributeName, fileColumnAlias: fileColumnAlias),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
    }


}
