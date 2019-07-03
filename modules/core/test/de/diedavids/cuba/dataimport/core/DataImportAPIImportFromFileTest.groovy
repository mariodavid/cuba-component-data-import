package de.diedavids.cuba.dataimport.core

import com.haulmont.cuba.core.app.FileStorageAPI
import com.haulmont.cuba.core.entity.FileDescriptor
import com.haulmont.cuba.core.global.AppBeans
import de.diedavids.cuba.dataimport.AbstractImportIntegrationTest
import de.diedavids.cuba.dataimport.DataImportAPI
import de.diedavids.cuba.dataimport.data.SimpleDataLoader
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportExecution
import de.diedavids.cuba.dataimport.entity.ImportTransactionStrategy
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.example.mlb.MlbTeam
import org.junit.Before
import org.junit.Test

import java.nio.charset.StandardCharsets

import static org.assertj.core.api.Assertions.assertThat

class DataImportAPIImportFromFileTest extends AbstractImportIntegrationTest {


    protected DataImportAPI sut

    protected ImportConfiguration importConfiguration

    protected SimpleDataLoader simpleDataLoader

    protected FileStorageAPI fileStorageAPI

    @Before
    void setUp() throws Exception {
        super.setUp()

        sut = AppBeans.get(DataImportAPI.NAME)
        simpleDataLoader = AppBeans.get(SimpleDataLoader.NAME)
        fileStorageAPI = AppBeans.get(FileStorageAPI.NAME)

        clearTable("DDCDI_MLB_TEAM")
        clearTable("SYS_FILE")
    }

    @Test
    void "importFromFile reads the file from the file descriptor, parses them and imports them correctly for a valid file"() {

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbTeam',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'code', fileColumnAlias: 'Code', fileColumnNumber: 0),
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name', fileColumnAlias: 'Name', fileColumnNumber: 1),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION,
                fileCharset: StandardCharsets.UTF_8.name()
        )

        String fileContent = '''"Code","Name","State"
ALT,"Altoona Mountain City",OK
ANA,"Los Angeles Angels of Anaheim",WA
CAL,"California Angels",OH
'''
        def bytes = fileContent.bytes
        FileDescriptor persistedFileDescriptor = dataManager.commit(createFileDescriptorForFile(bytes))

        fileStorageAPI.saveFile(persistedFileDescriptor, bytes)

        //when:
        ImportExecution importExecution = sut.importFromFile(importConfiguration, persistedFileDescriptor)

        //then:
        def mlbTeams = simpleDataLoader.loadAll(MlbTeam)
        assertThat(mlbTeams.size()).isEqualTo(3)

        //and:
        assertThat(importExecution.entitiesProcessed).isEqualTo(3)
        assertThat(importExecution.entitiesImportSuccess).isEqualTo(3)
        assertThat(importExecution.entitiesImportValidationError).isEqualTo(0)
    }

    private FileDescriptor createFileDescriptorForFile(byte[] bytes) {
        FileDescriptor fileDescriptor = dataManager.create(FileDescriptor.class)
        fileDescriptor.setName("mlb_teams.csv")
        fileDescriptor.setExtension("csv")
        fileDescriptor.setSize(bytes.length)
        fileDescriptor.setCreateDate(new Date())
        fileDescriptor
    }

}
