package com.cinnamon.cleanexplorers.api.service

interface ApiClient {
    fun <S> createService(serviceClass: Class<S>): S
}