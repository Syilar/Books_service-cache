package com.example.booksmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Autowired
    private CacheManager cacheManager;

    public void updateCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        cache.clear();
    }

    public void updateCacheByKey(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        cache.evict(key);
    }
}
