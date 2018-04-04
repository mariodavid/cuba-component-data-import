package de.diedavids.cuba.dataimport.web.importwizard

class FileNotSupportedException extends RuntimeException {

    FileNotSupportedException() {
        super('File extension not supported')
    }
}
