package de.diedavids.cuba.dataimport.web.importwizard

import com.haulmont.cuba.core.entity.FileDescriptor
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.gui.components.FileUploadField
import com.haulmont.cuba.gui.components.UploadField
import com.haulmont.cuba.gui.upload.FileUploadingAPI

class ImportFileHandler {


    FileUploadingAPI fileUploadingAPI


    FileUploadField importFileUploadBtn


    FileDescriptor uploadedFileDescriptor

    File uploadedFile

    DataManager dataManager

    void onUploadSuccess(Closure onSuccessClosure) {
        importFileUploadBtn.addFileUploadSucceedListener(new FileUploadField.FileUploadSucceedListener() {
            @Override
            void fileUploadSucceed(FileUploadField.FileUploadSucceedEvent e) {
                File file = fileUploadingAPI.getFile(importFileUploadBtn.fileId)
                uploadedFileDescriptor = importFileUploadBtn.fileDescriptor
                uploadedFile = file
                onSuccessClosure.call()
            }
        })
    }


    void onUploadError(Closure onErrorClosure) {
        importFileUploadBtn.addFileUploadErrorListener(new UploadField.FileUploadErrorListener() {
            @Override
            void fileUploadError(UploadField.FileUploadErrorEvent e) {
                onErrorClosure.call()
            }
        })
    }

    String getFileName() {
        uploadedFileDescriptor.name
    }


    FileDescriptor saveFile() {
        dataManager.commit(uploadedFileDescriptor)
        fileUploadingAPI.putFileIntoStorage(importFileUploadBtn.fileId, uploadedFileDescriptor)

        uploadedFileDescriptor
    }
}
