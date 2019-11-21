package com.nimbusframework.nimbuslocal.exampleHandlers

import com.nimbusframework.nimbuscore.annotations.file.FileStorageEventType
import com.nimbusframework.nimbuscore.annotations.function.FileStorageServerlessFunction
import com.nimbusframework.nimbuscore.eventabstractions.FileStorageEvent

class ExampleFileStorageHandler {

    @FileStorageServerlessFunction(bucketName = "testbucket", eventType = FileStorageEventType.OBJECT_CREATED)
    fun newFile(event: FileStorageEvent) {
        return
    }

    @FileStorageServerlessFunction(bucketName = "testbucket", eventType = FileStorageEventType.OBJECT_DELETED)
    fun deletedFile(event: FileStorageEvent) {
        return
    }
}