package com.nimbusframework.nimbuslocal.deployment.services.function

import com.nimbusframework.nimbuscore.annotations.function.WebSocketServerlessFunction
import com.nimbusframework.nimbuslocal.deployment.function.FunctionIdentifier
import com.nimbusframework.nimbuslocal.deployment.function.ServerlessFunction
import com.nimbusframework.nimbuslocal.deployment.function.information.WebSocketFunctionInformation
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder
import com.nimbusframework.nimbuslocal.deployment.websocket.LocalWebsocketMethod
import java.lang.reflect.Method

class LocalWebSocketFunctionHandler(
        private val localResourceHolder: LocalResourceHolder,
        private val webSocketPort: Int,
        private val variableSubstitution: MutableMap<String, String>,
        private val stage: String
) : LocalFunctionHandler(localResourceHolder) {

    override fun handleMethod(clazz: Class<out Any>, method: Method): Boolean {
        val webSocketServerlessFunctions = method.getAnnotationsByType(WebSocketServerlessFunction::class.java)
        if (webSocketServerlessFunctions.isEmpty()) return false

        val functionIdentifier = FunctionIdentifier(clazz.canonicalName, method.name)

        for (webSocketFunction in webSocketServerlessFunctions) {
            if (webSocketFunction.stages.contains(stage)) {
                val invokeOn = clazz.getConstructor().newInstance()

                val webSocketMethod = LocalWebsocketMethod(method, invokeOn)
                val functionInformation = WebSocketFunctionInformation(webSocketFunction.topic)

                localResourceHolder.websocketMethods[webSocketFunction.topic] = webSocketMethod
                localResourceHolder.functions[functionIdentifier] = ServerlessFunction(
                        webSocketMethod,
                        functionInformation
                )
                localResourceHolder.webSocketServer.addTopic(webSocketFunction.topic, webSocketMethod)

                variableSubstitution["\${NIMBUS_WEBSOCKET_API_URL}"] = "ws://localhost:$webSocketPort"
            }
        }
        return true
    }

}