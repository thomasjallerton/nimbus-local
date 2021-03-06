package com.nimbusframework.nimbuslocal.deployment.store

import com.nimbusframework.nimbuscore.annotations.persistent.StoreEventType
import com.nimbusframework.nimbuscore.eventabstractions.StoreEvent
import com.nimbusframework.nimbuslocal.ServerlessMethod
import com.nimbusframework.nimbuslocal.deployment.function.FunctionType
import java.lang.reflect.Method

abstract class StoreMethod(
        private val method: Method,
        private val invokeOn: Any,
        private val storeEventType: StoreEventType,
        functionType: FunctionType
) : ServerlessMethod(method, StoreEvent::class.java, functionType) {

    fun invokeInsert(newItem: Any?) {
        timesInvoked++

        if (storeEventType != StoreEventType.INSERT) return

        val event = StoreEvent(storeEventType.name)

        val params = method.parameters
        val eventIndex = eventIndex()

        mostRecentInvokeArgument = newItem
        mostRecentValueReturned = when {
            params.isEmpty() -> method.invoke(invokeOn)
            params.size == 1 && eventIndex == 0 -> method.invoke(invokeOn, event)
            params.size == 1 -> method.invoke(invokeOn, newItem)
            else -> { //Params.size == 2
                if (eventIndex == 0) {
                    method.invoke(invokeOn, event, newItem)
                } else {
                    method.invoke(invokeOn, newItem, event)
                }
            }
        }
    }

    fun invokeModify(oldItem: Any?, newItem: Any?) {
        timesInvoked++

        if (storeEventType != StoreEventType.MODIFY) return

        val event = StoreEvent(storeEventType.name)

        val params = method.parameters
        val eventIndex = eventIndex()

        mostRecentInvokeArgument = listOf(oldItem, newItem)
        mostRecentValueReturned = when {
            params.isEmpty() -> method.invoke(invokeOn)
            params.size == 1 -> when (eventIndex) {
                0 -> method.invoke(invokeOn, event)
                else -> method.invoke(invokeOn, newItem)
            }
            params.size == 2 -> {
                when (eventIndex) {
                    1 -> method.invoke(invokeOn, newItem, event)
                    0 -> method.invoke(invokeOn, event, newItem)
                    else -> method.invoke(invokeOn, oldItem, newItem)
                }
            }
            //Params.size == 3
            else -> method.invoke(invokeOn, oldItem, newItem, event)
        }
    }

    fun invokeRemove(oldItem: Any?) {
        timesInvoked++

        if (storeEventType != StoreEventType.REMOVE) return

        val event = StoreEvent(storeEventType.name)

        val params = method.parameters
        val eventIndex = eventIndex()

        mostRecentInvokeArgument = oldItem
        mostRecentValueReturned = when {
            params.isEmpty() -> method.invoke(invokeOn)
            params.size == 1 && eventIndex == 0 -> method.invoke(invokeOn, event)
            params.size == 1 -> method.invoke(invokeOn, oldItem)
            else -> { //Params.size == 2
                if (eventIndex == 0) {
                    method.invoke(invokeOn, event, oldItem)
                } else {
                    method.invoke(invokeOn, oldItem, event)
                }
            }
        }
    }
}