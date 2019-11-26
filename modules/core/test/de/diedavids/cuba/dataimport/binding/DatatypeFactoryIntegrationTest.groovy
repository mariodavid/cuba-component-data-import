package de.diedavids.cuba.dataimport.binding

import com.haulmont.cuba.core.app.dynamicattributes.DynamicAttributes
import com.haulmont.cuba.core.global.AppBeans
import de.diedavids.cuba.dataimport.dto.DataRowImpl
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportTransactionStrategy
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.example.mlb.State
import org.junit.Before
import org.junit.Test

import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
        importConfiguration = importConfigurationFor('ddcdi$MlbPlayer', importAttributeMapper)

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
        importConfiguration = importConfigurationFor('ddcdi$MlbPlayer', importAttributeMapper)

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
        importConfiguration = importConfigurationFor('ddcdi$MlbPlayer', importAttributeMapper)

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
        importConfiguration = importConfigurationFor('ddcdi$MlbPlayer', importAttributeMapper)
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
    void "getValue can parse a missing Date value correctly"() {

        // given:
        importAttributeMapper = attributeMapperFor('birthday')
        importConfiguration = importConfigurationFor('ddcdi$MlbPlayer', importAttributeMapper)
        def dateFormat = "yyyy-MM-dd"
        importConfiguration.dateFormat = dateFormat

        // and:
        def bindRequest = bindRequestFor(importConfiguration, importAttributeMapper, [birthday: null])

        // when:
        def result = sut.getValue(bindRequest)

        // then:
        assertThat(result).isNull()
    }

    @Test
    void "getValue can parse a LocalDate value correctly"() {

        // given:
        importAttributeMapper = attributeMapperFor('birthdayLocalDate')
        importConfiguration = importConfigurationFor('ddcdi$MlbPlayer', importAttributeMapper)
        def dateFormat = "yyyy-MM-dd"
        importConfiguration.dateFormat = dateFormat

        // and:
        def expectedBindingValue = '1985-07-09'
        def bindRequest = bindRequestFor(importConfiguration, importAttributeMapper, [birthdayLocalDate: expectedBindingValue])

        // when:
        def result = sut.getValue(bindRequest)

        // then:
        assertThat(result).isInstanceOf(LocalDate)
        def resultAsString = result.format(DateTimeFormatter.ofPattern(dateFormat));
        assertThat(resultAsString).isEqualTo(expectedBindingValue)

    }


    @Test
    void "getValue can parse a Missing LocalDate value correctly"() {

        // given:
        importAttributeMapper = attributeMapperFor('birthdayLocalDate')
        importConfiguration = importConfigurationFor('ddcdi$MlbPlayer', importAttributeMapper)
        def dateFormat = "yyyy-MM-dd"
        importConfiguration.dateFormat = dateFormat

        // and:
        def bindRequest = bindRequestFor(importConfiguration, importAttributeMapper, [birthdayLocalDate: null])

        // when:
        def result = sut.getValue(bindRequest)

        // then:
       assertThat(result).isNull()

    }

    @Test
    void "getValue can parse a Boolean value correctly"() {

        // given:
        importAttributeMapper = attributeMapperFor('leftHanded')
        importConfiguration = importConfigurationFor('ddcdi$MlbPlayer', importAttributeMapper)
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
        importConfiguration = importConfigurationFor('ddcdi$MlbPlayer', importAttributeMapper)

        // and:
        def expectedBindingValue = 1000

        def bindRequest = bindRequestFor(importConfiguration, importAttributeMapper, [annualSalary: expectedBindingValue])

        // when:
        def result = sut.getValue(bindRequest)

        // then:
        assertThat(result).isInstanceOf(BigDecimal)
        assertThat(result).isEqualTo(BigDecimal.valueOf(1000))

    }

    @Test
    void "getValue can parse a Long value correctly"() {

        // given:
        importAttributeMapper = attributeMapperFor('annualSalaryLong')
        importConfiguration = importConfigurationFor('ddcdi$MlbPlayer', importAttributeMapper)

        // and:
        def expectedBindingValue = 1000L

        def bindRequest = bindRequestFor(importConfiguration, importAttributeMapper, [annualSalaryLong: expectedBindingValue])

        // when:
        def result = sut.getValue(bindRequest)

        // then:
        assertThat(result).isInstanceOf(Long)
        assertThat(result).isEqualTo(Long.valueOf(1000L))

    }


    @Test
    void "getValue can parse a Enum value correctly"() {

        // given:
        importAttributeMapper = attributeMapperFor('state')
        importConfiguration = importConfigurationFor('ddcdi$MlbTeam', importAttributeMapper)

        // and:
        def expectedBindingValue = "AL"

        def bindRequest = bindRequestFor(importConfiguration, importAttributeMapper, [state: expectedBindingValue])

        // when:
        def result = sut.getValue(bindRequest)

        // then:
        assertThat(result).isInstanceOf(State)
        assertThat(result).isEqualTo(State.AL)

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

    protected ImportConfiguration importConfigurationFor(String entityClass, ImportAttributeMapper importAttributeMapper) {
        new ImportConfiguration(
                entityClass: entityClass,
                importAttributeMappers: [importAttributeMapper],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
    }

}
