package com.citytechinc.aem.bedrock.core.services.cache

import com.google.common.cache.Cache
import com.google.common.cache.LoadingCache
import groovy.util.logging.Slf4j
import org.slf4j.Logger

@Slf4j("LOG")
class TestCacheService extends AbstractCacheService {

    Cache johnny

    LoadingCache june

    @Override
    Logger getLogger() {
        LOG
    }
}
