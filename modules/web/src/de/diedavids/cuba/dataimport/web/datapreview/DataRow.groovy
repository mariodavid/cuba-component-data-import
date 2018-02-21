package de.diedavids.cuba.dataimport.web.datapreview

import com.haulmont.cuba.core.entity.KeyValueEntity

class DataRow {

    def values = []
    Map<String, Object> columns = [:]

    static DataRow ofMap(Map<String, Object> data) {
        def row = new DataRow()

        int i = 0
        data.each { k, v ->
            row.columns[k] = i
            row.values << v
            i++
        }

        row
    }

    KeyValueEntity toKevValueEntity() {
        def result = new KeyValueEntity()

        toMap().each {String k, Object v ->
            result.setValue(k, v)
        }
        result
    }

    def propertyMissing(String name) {
        def index = columns[name]
        if (index != null) {
            values[index]
        } else {
            throw new MissingPropertyException(name)
        }
    }

    def getAt(Integer index) {
        values[index]
    }


    String toString() {
        columns.collect { key, index -> "$key: ${values[index]}" }.join(', ')
    }

    Map toMap() {
        def sortedKeys = columns.keySet().sort { columns[it] }
        [sortedKeys, values].transpose().collectEntries()
    }

    List<String> getColumnNames() {
        columns.keySet().sort { columns[it] }
    }
}