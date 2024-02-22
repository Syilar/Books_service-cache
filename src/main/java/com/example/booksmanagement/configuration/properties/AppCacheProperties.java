package com.example.booksmanagement.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "app.cache")
public class AppCacheProperties {

    private final List<String> cacheList = new ArrayList<>();

    private final Map<String, CacheProperties> caches = new HashMap<>();

    private CacheType cacheType;

    @Data
    public static class CacheProperties {

        private Duration expiry = Duration.ZERO;
    }

    public interface CachesNames {

        String DATABASE_BOOKS = "databaseBooks";

        String DATABASE_BOOKS_BY_ID = "databaseBooksById";

        String DATABASE_BOOKS_BY_NAME_CATEGORY = "databaseBooksByNameCategory";

        String DATABASE_BOOKS_BY_NAME_AND_AUTHOR = "databaseBooksByNameAndAuthor";
    }

    public enum CacheType {
        IN_MEMORY, REDIS
    }
}
