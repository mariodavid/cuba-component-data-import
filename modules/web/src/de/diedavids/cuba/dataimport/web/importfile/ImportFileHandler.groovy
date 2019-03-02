package de.diedavids.cuba.dataimport.web.importfile

import com.haulmont.cuba.core.entity.FileDescriptor
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.gui.components.FileUploadField
import com.haulmont.cuba.gui.components.UploadField
import com.haulmont.cuba.gui.upload.FileUploadingAPI

import java.util.function.Consumer

class ImportFileHandler {


    FileUploadingAPI fileUploadingAPI


    FileUploadField importFileUploadBtn


    FileDescriptor uploadedFileDescriptor

    File uploadedFile

    DataManager dataManager

    void onUploadSuccess(Closure onSuccessClosure) {
        importFileUploadBtn.addFileUploadSucceedListener(new Consumer<FileUploadField.FileUploadSucceedEvent>() {
            @Override
            void accept(FileUploadField.FileUploadSucceedEvent fileUploadSucceedEvent) {
                File file = fileUploadingAPI.getFile(importFileUploadBtn.fileId)
                uploadedFileDescriptor = importFileUploadBtn.fileDescriptor
                uploadedFile = file
                onSuccessClosure.call()
            }
        })
    }


    void onUploadError(Closure onErrorClosure) {
        importFileUploadBtn.addFileUploadErrorListener(new Consumer<UploadField.FileUploadErrorEvent>() {
            @Override
            void accept(UploadField.FileUploadErrorEvent fileUploadErrorEvent) {
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
