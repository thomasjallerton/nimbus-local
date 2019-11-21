package com.nimbusframework.nimbuslocal.exampleModels

import com.nimbusframework.nimbuscore.annotations.deployment.AfterDeployment
import com.nimbusframework.nimbuscore.annotations.file.FileStorageBucket
import com.nimbusframework.nimbuscore.annotations.file.UsesFileStorage
import com.nimbusframework.nimbuscore.clients.ClientBuilder

@FileStorageBucket(bucketName = "BucketTwo", staticWebsite = true)
class BucketTwo {
    @AfterDeployment
    @UsesFileStorage(bucketName = "BucketTwo")
    fun uploadFile() {
        val client = ClientBuilder.getFileStorageClient("BucketTwo")
        client.saveFile("test.txt", "HELLO WORLD")
    }
}