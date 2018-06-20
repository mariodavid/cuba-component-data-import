package de.diedavids.cuba.dataimport.converter

import de.diedavids.cuba.dataimport.dto.ImportData
import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.NodeChild

class XmlImportDataConverter extends AbstractTextBasedImportDataConverter<GPathResult> {


    @Override
    protected void doConvert(GPathResult entries, ImportData result) {
        entries.children().each { NodeChild nodeChild ->
            result.columns = getColumns(nodeChild)
            addToTableData(result, processNode(nodeChild))
        }
    }

    @Override
    protected GPathResult parse(String content) {
        new XmlSlurper().parseText(content)
    }

    private List<String> getColumns(NodeChild nodeChild) {
        nodeChild.children()*.name()
    }


    Map<String, Object> processNode(node, Map<String, ?> map = [:]) {
        map[node.name()] = map[node.name()] ?: map.getClass().newInstance()

        Map<String, Object> nodeMap = map[node.name()]

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
