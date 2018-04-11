package de.diedavids.cuba.dataimport.converter

import org.apache.poi.ss.usermodel.WorkbookFactory

class ExcelReader {

    def workbook
    def labels
    def row

    ExcelReader(File file) {
        workbook = WorkbookFactory.create(file)
    }

    def getSheet(idx) {
        def sheet

        def actualIndex = idx ?: 0
        if (actualIndex instanceof Number) {
            sheet = workbook.getSheetAt(actualIndex)
        } else if (actualIndex ==~ /^\d+$/) {
            sheet = workbook.getSheetAt(Integer.valueOf(actualIndex))
        } else {
            sheet = workbook.getSheet(actualIndex)
        }
        sheet
    }

    def cell(idx) {
        def actualIndex = idx
        if (labels && (actualIndex instanceof String)) {
            actualIndex = labels.indexOf(actualIndex.toLowerCase())
        }
        row[actualIndex]
    }

    def propertyMissing(String name) {
        cell(name)
    }

    def eachLine(Map params = [:], Closure closure) {
        def offset = params.offset ?: 0
        def max = params.max ?: 9999999
        def sheet = getSheet(params.sheet)
        def rowIterator = sheet.rowIterator()
        def linesRead = 0

        if (params.labels) {
            labels = rowIterator.next().collect { it.toString().toLowerCase() }
        }
        offset.times { rowIterator.next() }

        closure.setDelegate(this)

        while (rowIterator.hasNext() && linesRead++ < max) {
            row = rowIterator.next()
            closure.call(row)
        }
    }
}
