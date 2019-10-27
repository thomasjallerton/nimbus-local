package com.nimbusframework.nimbuslocal.deployment.services.usesresources

import com.nimbusframework.nimbuscore.annotations.database.UsesRelationalDatabase
import com.nimbusframework.nimbuscore.permissions.PermissionType
import com.nimbusframework.nimbuslocal.deployment.function.FunctionEnvironment
import com.nimbusframework.nimbuslocal.deployment.function.permissions.StorePermission
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder
import java.lang.reflect.Method

class LocalUsesRelationalDatabaseHandler(
        localResourceHolder: LocalResourceHolder,
        private val stage: String
) : LocalUsesResourcesHandler(localResourceHolder) {

    override fun handleUsesResources(clazz: Class<out Any>, method: Method, functionEnvironment: FunctionEnvironment) {
        val usesRelationalDatabases = method.getAnnotationsByType(UsesRelationalDatabase::class.java)

        for (usesRelationalDatabase in usesRelationalDatabases) {
            if (usesRelationalDatabase.stages.contains(stage)) {
                functionEnvironment.addPermission(PermissionType.RELATIONAL_DATABASE, StorePermission(usesRelationalDatabase.dataModel.qualifiedName.toString()))
            }
        }
    }

}