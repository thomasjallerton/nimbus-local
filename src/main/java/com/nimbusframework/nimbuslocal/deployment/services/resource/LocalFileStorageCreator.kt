package com.nimbusframework.nimbuslocal.deployment.services.resource

import com.nimbusframework.nimbuscore.annotations.deployment.FileUpload
import com.nimbusframework.nimbuscore.annotations.file.FileStorageBucket
import com.nimbusframework.nimbuscore.annotations.file.UsesFileStorage
import com.nimbusframework.nimbuscore.persisted.FileUploadDescription
import com.nimbusframework.nimbuslocal.deployment.file.LocalFileStorage
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder
import com.nimbusframework.nimbuslocal.deployment.webserver.WebServerHandler

class LocalFileStorageCreator(
        private val localResourceHolder: LocalResourceHolder,
        private val httpPort: Int,
        private val variableSubstitution: MutableMap<String, String>,
        private val fileUploadDetails: MutableMap<String, MutableList<FileUploadDescription>>,
        private val stage: String
) : LocalCreateResourcesHandler {

    override fun createResource(clazz: Class<out Any>) {
        val fileStorageBuckets = clazz.getAnnotationsByType(FileStorageBucket::class.java)
        val localWebservers = localResourceHolder.httpServers

        for (fileStorageBucket in fileStorageBuckets) {
            if (fileStorageBucket.stages.contains(stage)) {
                if (fileStorageBucket.staticWebsite && !localWebservers.containsKey(fileStorageBucket.bucketName)) {
                    val localWebserver = WebServerHandler(fileStorageBucket.indexFile, fileStorageBucket.errorFile, "http://localhost:$httpPort/${fileStorageBucket.bucketName}/")
                    localWebservers[fileStorageBucket.bucketName] = localWebserver
                    variableSubstitution["\${${fileStorageBucket.bucketName.toUpperCase()}_URL}"] = "http://localhost:$httpPort/${fileStorageBucket.bucketName}"
                }

                val fileStorage = localResourceHolder.fileStorage
                if (!fileStorage.containsKey(fileStorageBucket.bucketName)) {
                    val allowedOrigins = fileStorageBucket.allowedCorsOrigins.map {
                        if (it == "#{NIMBUS_REST_API_URL}") {
                            "http://localhost:$httpPort/function/"
                        } else {
                            it
                        }
                    }

                    fileStorage[fileStorageBucket.bucketName] = LocalFileStorage(fileStorageBucket.bucketName, allowedOrigins)
                }
            }
        }

        for (method in clazz.declaredMethods) {
            val usesFileStorages = method.getAnnotationsByType(UsesFileStorage::class.java)

            val fileStorage = localResourceHolder.fileStorage

            for (usesFileStorage in usesFileStorages) {
                if (usesFileStorage.stages.contains(stage)) {
                    if (!fileStorage.containsKey(usesFileStorage.bucketName)) {
                        fileStorage[usesFileStorage.bucketName] = LocalFileStorage(usesFileStorage.bucketName, listOf())
                    }
                }
            }
        }

        for (fileUpload in clazz.getAnnotationsByType(FileUpload::class.java)) {
            if (fileUpload.stages.contains(stage)) {
                val bucketFiles = fileUploadDetails.getOrPut(fileUpload.bucketName) { mutableListOf() }
                val description = FileUploadDescription(fileUpload.localPath, fileUpload.targetPath, fileUpload.substituteNimbusVariables)
                bucketFiles.add(description)
            }
        }
    }

}