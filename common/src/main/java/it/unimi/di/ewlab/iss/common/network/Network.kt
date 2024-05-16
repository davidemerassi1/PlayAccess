package it.unimi.di.ewlab.iss.common.network

import kotlin.reflect.KClass

/**
 * Network module entry point.
 */
class Network {

    /**
     * Creates an instance of [apiClass]
     * using the [Network] config. This instance use the base URL for "api".
     */
    fun <T : Any> createApiServiceAPI(apiClass: KClass<T>): T {
        return NetworkRetrofit().retrofitApi.create(
            apiClass.java
        )
    }
}