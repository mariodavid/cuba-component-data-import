package de.diedavids.cuba.dataimport.binding

import com.haulmont.cuba.core.app.dynamicattributes.DynamicAttributes
import com.haulmont.cuba.core.global.AppBeans
import de.diedavids.cuba.dataimport.dto.DataRowImpl
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportTransactionStrategy
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import org.junit.Before
import org.junit.Test

import java.time.LocalDate

import static org.assertj.core.api.Assertions.assertThat

class DatatypeFactoryIntegrationTest extends AbstractEntityBinderIntegrationTest {

    DatatypeFactory sut
    DynamicAttributes dynamicAttributes
    private importAttributeMapper

    @Before
    void setUp() throws Exception {
        super.setUp()

        sut = new DatatypeFactory()
        dynamicAttributes = AppBeans.get(DynamicAttributes.NAME)
    }

    @Test
    void "getValue can parse a String value correctly"() {

        // given:
        importAttributeMapper = attributeMapperFor('name')
        importConfiguration = importConfigurationFor(importAttributeMapper)

        // and:
        def expectedBindingValue = "MyTeamName"
        def bindRequest = bindRequestFor(importConfiguration, importAttributeMapper, [name: expectedBindingValue])

        // when:
        def result = sut.getValue(bindRequest)

        // then:
        assertThat(result).isInstanceOf(String)
        assertThat(result).isEqualTo(expectedBindingValue)

    }

    @Test
    void "getValue can parse an Integer value correctly"() {

        // given:
        importAttributeMapper = attributeMapperFor('height')
        importConfiguration = importConfigurationFor(importAttributeMapper)

        // and:
        def expectedBindingValue = 125
        def bindRequest = bindRequestFor(importConfiguration, importAttributeMapper, [height: expectedBindingValue])

        // when:
        def result = sut.getValue(bindRequest)

        // then:
        assertThat(result).isInstanceOf(Integer)
        assertThat(result).isEqualTo(expectedBindingValue)

    }
    @Test
    void "getValue can parse an Double value correctly"() {

        // given:
        importAttributeMapper = attributeMapperFor('age')
        importConfiguration = importConfigurationFor(importAttributeMapper)

        // and:
        def expectedBindingValue = 25.4d
        def bindRequest = bindRequestFor(importConfiguration, importAttributeMapper, [age: expectedBindingValue])

        // when:
        def result = sut.getValue(bindRequest)

        // then:
        assertThat(result).isInstanceOf(Double)
        assertThat(result).isEqualTo(expectedBindingValue)

    }

    @Test
    void "getValue can parse a Date value correctly"() {

        // given:
        importAttributeMapper = attributeMapperFor('birthday')
        importConfiguration = importConfigurationFor(importAttributeMapper)
        def dateFormat = "yyyy-MM-dd"
        importConfiguration.dateFormat = dateFormat

        // and:
        def expectedBindingValue = '1985-07-09'
        def bindRequest = bindRequestFor(importConfiguration, importAttributeMapper, [birthday: expectedBindingValue])

        // when:
        def result = sut.getValue(bindRequest)

        // then:
        assertThat(result).isInstanceOf(Date)
        assertThat(result).isEqualTo(expectedBindingValue)

    }

    @Test
    void "getValue can parse a Boolean value correctly"() {

        // given:
        importAttributeMapper = attributeMapperFor('leftHanded')
        importConfiguration = importConfigurationFor(importAttributeMapper)
        importConfiguration.booleanTrueValue = 'true'

        // and:
        def expectedBindingValue = 'true'
        def bindRequest = bindRequestFor(importConfiguration, importAttributeMapper, [leftHanded: expectedBindingValue])

        // when:
        def result = sut.getValue(bindRequest)

        // then:
        assertThat(result).isInstanceOf(Boolean)
        assertThat(result).isEqualTo(true)

    }


    @Test
    void "getValue can parse a BigDecimal value correctly"() {

        // given:
        importAttributeMapper = attributeMapperFor('annualSalary')
        importConfiguration = importConfigurationFor(importAttributeMapper)

        // and:
        def expectedBindingValue = 1000

        def bindRequest = bindRequestFor(importConfiguration, importAttributeMapper, [annualSalary: expectedBindingValue])

        // when:
        def result = sut.getValue(bindRequest)

        // then:
        assertThat(result).isInstanceOf(BigDecimal)
        assertThat(result).isEqualTo(BigDecimal.valueOf(1000))

    }

    protected AttributeBindRequest bindRequestFor(ImportConfiguration importConfiguration, ImportAttributeMapper importAttributeMapper, Map dataRowMap) {
        new AttributeBindRequest(
                importConfiguration: importConfiguration,
                importAttributeMapper: importAttributeMapper,
                dataRow: DataRowImpl.ofMap(dataRowMap),
                metadata: metadata,
                dynamicAttributes: dynamicAttributes
        )
    }

    protected ImportAttributeMapper attributeMapperFor(String attribute) {
        new ImportAttributeMapper(
                attributeType: AttributeType.DIRECT_ATTRIBUTE,
                entityAttribute: attribute,
                fileColumnAlias: attribute
        )
    }

    protected ImportConfiguration importConfigurationFor(ImportAttributeMapper importAttributeMapper) {
        new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [importAttributeMapper],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
    }

}