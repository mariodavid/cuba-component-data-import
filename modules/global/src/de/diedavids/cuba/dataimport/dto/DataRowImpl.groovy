package de.diedavids.cuba.dataimport.dto

import com.haulmont.cuba.core.entity.KeyValueEntity

class DataRowImpl implements DataRow {

    def values = []
    Map<String, Object> columns = [:]

    static DataRow ofMap(Map<String, Object> data) {
        def row = new DataRowImpl()

        int i = 0
        data.each { k, v ->
            row.columns[k] = i
            row.values << v
            i++
        }

        row
    }

    @Override
    KeyValueEntity toKevValueEntity() {
        def result = new KeyValueEntity()

        toMap().each {String k, Object v ->
            result.setValue(k, v)
        }
        result
    }

    @Override
    boolean isEmpty() {

        def valueMap = toMap()

        valueMap.isEmpty() || valueMap.every {k,v -> !v}
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

    @Override
    Map toMap() {
        def sortedKeys = columns.keySet().sort { columns[it] }
        [sortedKeys, values].transpose().collectEntries()
    }

    @Override
    List<String> getColumnNames() {
        columns.keySet().sort { columns[it] }
    }
}