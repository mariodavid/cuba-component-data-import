package de.diedavids.cuba.dataimport.converter

class FileNotSupportedException extends RuntimeException {

    FileNotSupportedException() {
        super('File extension not supported')
    }
}
