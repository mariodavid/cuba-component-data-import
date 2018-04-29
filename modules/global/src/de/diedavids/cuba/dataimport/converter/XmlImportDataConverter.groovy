package de.diedavids.cuba.dataimport.converter

import com.xlson.groovycsv.CsvParser
import com.xlson.groovycsv.PropertyMapper
import de.diedavids.cuba.dataimport.dto.DataRow
import de.diedavids.cuba.dataimport.dto.DataRowImpl
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.dto.ImportDataImpl
import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.NodeChild

class XmlImportDataConverter implements ImportDataConverter {

    @Override
    ImportData convert(String content) {
        def result = new ImportDataImpl()

        GPathResult root = parseXML(content)

        root.children().each { NodeChild nodeChild ->
            result.columns = getColumns(nodeChild)
            addToTableData(result, processNode(nodeChild))
        }

        result
    }

    private List<String> getColumns(NodeChild nodeChild) {
        nodeChild.children()*.name()
    }

    private GPathResult parseXML(String content) {
        new XmlSlurper().parseText(content)
    }

    @Override
    ImportData convert(File file) {
        convert(file.text)
    }

    private DataRow addToTableData(ImportDataImpl importData, Map row) {
        def dataRow = DataRowImpl.ofMap(row)
        importData.rows << dataRow
        dataRow
    }


    def processNode(node, Map<String, ?> map = [:]) {
        if (!map[node.name()]) {
            map[node.name()] = map.getClass().newInstance()
        }
        Map<String, ?> nodeMap = map[node.name()]

        node.children().each { it ->
            if (it.children().size() == 0) {
                processLeaf(it, nodeMap)
            } else {
                processNode(it, nodeMap)
            }
        }
        nodeMap
    }


    def processLeaf(node, Map<String, ?> map) {
        if (map[node.name()] == null) {
            map[node.name()] = node.text()
        } else {
            if (!(map[node.name()] instanceof List)) {
                map[node.name()] = [map[node.name()]]
            }
            map[node.name()] << node.text()
        }

        map[node.name()]
    }

}
