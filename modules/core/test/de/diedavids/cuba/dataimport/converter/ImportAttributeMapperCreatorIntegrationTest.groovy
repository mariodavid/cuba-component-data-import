package de.diedavids.cuba.dataimport.converter

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.global.AppBeans
import de.diedavids.cuba.dataimport.AbstractDbIntegrationTest
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.example.mlb.MlbPlayer
import org.junit.Before
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class ImportAttributeMapperCreatorIntegrationTest extends AbstractDbIntegrationTest {


    ImportAttributeMapperCreator sut

    @Before
    void setUp() throws Exception {
        super.setUp()

        sut = AppBeans.get(ImportAttributeMapperCreator.class)
    }


    @Test
    void "createMapper creates a Mapper for an association attribute without associationLookupAttribute"() {

        //when
        def attributeMapper = sut.createMapper(0, "Team", getMetaClassFor(MlbPlayer))

        //then
        assertThat(attributeMapper.attributeType).isEqualTo(AttributeType.ASSOCIATION_ATTRIBUTE)
        assertThat(attributeMapper.entityAttribute).isEqualTo("team")
    }


    @Test
    void "createMapper creates a Mapper for a Datatype with a similar name"() {

        //when
        def attributeMapper = sut.createMapper(0, "Annual Salary", getMetaClassFor(MlbPlayer))

        //then
        assertThat(attributeMapper.attributeType).isEqualTo(AttributeType.DIRECT_ATTRIBUTE)
        assertThat(attributeMapper.entityAttribute).isEqualTo("annualSalary")
    }

    private MetaClass getMetaClassFor(Class aClass) {
        metadata.getSession().getClass(aClass)
    }


}
