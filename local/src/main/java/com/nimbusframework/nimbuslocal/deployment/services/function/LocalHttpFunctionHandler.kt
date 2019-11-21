package com.nimbusframework.nimbuslocal.deployment.services.function

import com.nimbusframework.nimbuscore.annotations.function.HttpMethod
import com.nimbusframework.nimbuscore.annotations.function.HttpServerlessFunction
import com.nimbusframework.nimbuslocal.LocalNimbusDeployment.Companion.functionWebserverIdentifier
import com.nimbusframework.nimbuslocal.deployment.function.FunctionIdentifier
import com.nimbusframework.nimbuslocal.deployment.function.ServerlessFunction
import com.nimbusframework.nimbuslocal.deployment.function.information.HttpFunctionInformation
import com.nimbusframework.nimbuslocal.deployment.http.HttpMethodIdentifier
import com.nimbusframework.nimbuslocal.deployment.http.LocalHttpMethod
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder
import com.nimbusframework.nimbuslocal.deployment.webserver.WebServerHandler
import java.lang.reflect.Method

class LocalHttpFunctionHandler(
        private val localResourceHolder: LocalResourceHolder,
        private val httpPort: Int,
        private val variableSubstitution: MutableMap<String, String>,
        private val stage: String
) : LocalFunctionHandler(localResourceHolder) {

    override fun handleMethod(clazz: Class<out Any>, method: Method): Boolean {
        val httpServerlessFunctions = method.getAnnotationsByType(HttpServerlessFunction::class.java)
        if (httpServerlessFunctions.isEmpty()) return false

        val functionIdentifier = FunctionIdentifier(clazz.canonicalName, method.name)

        for (httpFunction in httpServerlessFunctions) {
            if (httpFunction.stages.contains(stage)) {
                val invokeOn = clazz.getConstructor().newInstance()

                val httpMethod = LocalHttpMethod(method, invokeOn)
                val functionInformation = HttpFunctionInformation(httpFunction.method, httpFunction.path)
                if (httpFunction.method != HttpMethod.ANY) {
                    val httpIdentifier = HttpMethodIdentifier(httpFunction.path, httpFunction.method)
                    localResourceHolder.httpMethods[httpIdentifier] = httpMethod

                } else {
                    for (httpMethodType in HttpMethod.values()) {
                        val httpIdentifier = HttpMethodIdentifier(httpFunction.path, httpMethodType)
                        localResourceHolder.httpMethods[httpIdentifier] = httpMethod
                    }
                }
                localResourceHolder.functions[functionIdentifier] = ServerlessFunction(httpMethod, functionInformation)

                val lambdaWebserver = localResourceHolder.httpServers.getOrPut(functionWebserverIdentifier) {
                    variableSubstitution["\${NIMBUS_REST_API_URL}"] = "http://localhost:$httpPort/$functionWebserverIdentifier"
                    WebServerHandler("", "", "http://localhost:$httpPort/$functionWebserverIdentifier/")
                }
                val allowedOrigin = if (variableSubstitution.containsKey(httpFunction.allowedCorsOrigin)) {
                    variableSubstitution[httpFunction.allowedCorsOrigin]!!
                } else {
                    httpFunction.allowedCorsOrigin
                }
                lambdaWebserver.addWebResource(
                        httpFunction.path,
                        httpFunction.method,
                        httpMethod,
                        allowedOrigin,
                        httpFunction.allowedCorsHeaders
                )
            }
        }
        return true
    }
}