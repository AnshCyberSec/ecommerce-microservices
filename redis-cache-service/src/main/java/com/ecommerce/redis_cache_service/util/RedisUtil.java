package com.ecommerce.redis_cache_service.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;


     // pattern-based key scanning

    public Set<String> scanKeys(String pattern) {
        Set<String> keys = new HashSet<>();
        try {
            ScanOptions options = ScanOptions.scanOptions()
                    .match(pattern)
                    .count(100)  // Batch size
                    .build();

            Cursor<byte[]> cursor = redisTemplate.execute(connection ->
                    connection.scan(options), true);

            while (cursor.hasNext()) {
                keys.add(new String(cursor.next()));
            }
            cursor.close();

        } catch (Exception e) {
            log.error("Error scanning Redis keys for pattern: {}", pattern, e);
        }
        return keys;
    }


     //Pattern-based key deletion

    public void deleteKeysByPattern(String pattern) {
        try {
            Set<String> keys = scanKeys(pattern);
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("Deleted {} keys for pattern: {}", keys.size(), pattern);
            } else {
                log.info("No keys found for pattern: {}", pattern);
            }
        } catch (Exception e) {
            log.error("Error deleting keys for pattern: {}", pattern, e);
        }
    }

     //Key count by pattern

    public long getKeyCountByPattern(String pattern) {
        return scanKeys(pattern).size();
    }


     //Check if key exists

    public boolean keyExists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Error checking key existence: {}", key, e);
            return false;
        }
    }


     //Get TTL of a key

    public Long getKeyTtl(String key) {
        try {
            return redisTemplate.getExpire(key);
        } catch (Exception e) {
            log.error("Error getting TTL for key: {}", key, e);
            return null;
        }
    }
}