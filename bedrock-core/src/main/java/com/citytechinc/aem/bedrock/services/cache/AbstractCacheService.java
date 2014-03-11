/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.services.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Responsible for exposing cache stats and cache variables in <code>CacheService</code> instances.
 */
public abstract class AbstractCacheService implements CacheService {

    @Override
    public final void clearAllCaches() {
        for (final Field field : collectFields(this.getClass())) {
            if (isCache(field)) {
                try {
                    getCache(field).invalidateAll();
                } catch (final Exception e) {
                    getLogger().error("An error has occurred while attempting to invalidate cache values for " + field
                        .getName() + " in the class " + this.getClass().getName() + ".", e);
                }
            }
        }
    }

    @Override
    public final void clearSpecificCache(final String cacheVariableName) {
        checkNotNull(cacheVariableName, "cache name must not be null");

        for (final Field field : collectFields(this.getClass())) {
            if (isNamedCache(field, cacheVariableName)) {
                try {
                    getCache(field).invalidateAll();
                } catch (final Exception e) {
                    getLogger().error("An error has occurred while attempting to invalidate cache values for " + field
                        .getName() + " in the class " + this.getClass().getName() + ".", e);
                }
            }
        }
    }

    @Override
    public final Long getCacheSize(final String cacheVariableName) {
        checkNotNull(cacheVariableName, "cache name must not be null");

        Long cacheSize = 0L;

        for (final Field field : collectFields()) {
            if (isNamedCache(field, cacheVariableName)) {
                try {
                    cacheSize = getCache(field).size();
                } catch (final Exception e) {
                    getLogger().error("An error has occurred while attempting retrieve cache size for " + field
                        .getName() + " in the class " + this.getClass().getName() + ".", e);
                }
            }
        }

        return cacheSize;
    }

    @Override
    public final CacheStats getCacheStats(final String cacheVariableName) {
        checkNotNull(cacheVariableName, "cache name must not be null");

        CacheStats cacheStats = null;

        for (final Field field : collectFields()) {
            if (isNamedCache(field, cacheVariableName)) {
                try {
                    cacheStats = getCache(field).stats();
                } catch (final Exception e) {
                    getLogger().error("An error has occurred while attempting retrieve cache statistics for " + field
                        .getName() + " in the class " + this.getClass().getName() + ".", e);
                }
            }
        }

        return cacheStats;
    }

    @Override
    public final List<String> listCaches() {
        final ImmutableList.Builder<String> cachesBuilder = new ImmutableList.Builder<String>();

        for (final Field field : collectFields()) {
            if (isCache(field)) {
                cachesBuilder.add(field.getName());
            }
        }

        return cachesBuilder.build();
    }

    protected abstract Logger getLogger();

    private List<Field> collectFields() {
        return collectFields(this.getClass());
    }

    private static List<Field> collectFields(final Class clazz) {
        final List<Field> fields = Lists.newArrayList();

        if (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            fields.addAll(collectFields(clazz.getSuperclass()));
        }

        return fields;
    }

    private static boolean isNamedCache(final Field field, final String cacheVariableName) {
        return isCache(field) && cacheVariableName.equals(field.getName());
    }

    private static boolean isCache(final Field field) {
        return isCacheType(field) || isAssignableFromCache(field);
    }

    private static boolean isCacheType(final Field field) {
        return field.getType() == Cache.class;
    }

    private static boolean isAssignableFromCache(final Field field) {
        return Cache.class.isAssignableFrom(field.getType());
    }

    private Cache getCache(final Field field) throws IllegalAccessException {
        field.setAccessible(true);

        return (Cache) field.get(this);
    }
}