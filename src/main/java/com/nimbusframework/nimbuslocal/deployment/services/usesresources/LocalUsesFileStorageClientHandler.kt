package com.nimbusframework.nimbuslocal.deployment.services.usesresources

import com.nimbusframework.nimbuscore.annotations.file.UsesFileStorage
import com.nimbusframework.nimbuscore.permissions.PermissionType
import com.nimbusframework.nimbuslocal.deployment.function.FunctionEnvironment
import com.nimbusframework.nimbuslocal.deployment.function.permissions.FileStoragePermission
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder
import java.lang.reflect.Method

class LocalUsesFileStorageClientHandler(
        localResourceHolder: LocalResourceHolder,
        private val stage: String
): LocalUsesResourcesHandler(localResourceHolder) {

    override fun handleUsesResources(clazz: Class<out Any>, method: Method, functionEnvironment: FunctionEnvironment) {
        val usesFileStorages = method.getAnnotationsByType(UsesFileStorage::class.java)

        for (usesFileStorage in usesFileStorages) {
            if (usesFileStorage.stages.contains(stage)) {
                functionEnvironment.addPermission(PermissionType.FILE_STORAGE, FileStoragePermission(usesFileStorage.bucketName))
            }
        }
    }

}