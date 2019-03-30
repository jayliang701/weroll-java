package com.magicfish.weroll.security.jwt.identifier;

import com.alibaba.fastjson.JSONObject;
import com.magicfish.weroll.config.property.RedisProperties;
import com.magicfish.weroll.config.property.SessProperties;
import com.magicfish.weroll.exception.IllegalSessionTokenException;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SessionRedisIdentifier extends AbstractSessionIdentifier {

    public SessionRedisIdentifier(SessProperties properties) {
        super(properties);

        RedisProperties redisProperties = properties.getRedis();

        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        if (redisProperties.getPass() != null && !redisProperties.getPass().isEmpty()) {
            RedisPassword password = RedisPassword.of(redisProperties.getPass());
            configuration.setPassword(password);
        }
        JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder().usePooling().build();
        JedisConnectionFactory factory = new JedisConnectionFactory(configuration, jedisClientConfiguration);
        factory.afterPropertiesSet();

        redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new GenericToStringSerializer<>(Object.class));
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.afterPropertiesSet();
    }

    private RedisTemplate<String, Object> redisTemplate;

    private String buildKey(String key) {
        String prefix = properties.getRedis().getPrefix();
        if (prefix != null && !prefix.isEmpty()) {
            return prefix + key;
        }
        return key;
    }

    @Override
    public void saveUserPayload(String secretKey, String token, Map<String, Object> params, Long tokenExpireTime) {
        String identify = getIdentify(secretKey, token);
        String key = buildKey(identify);

        JSONObject store = new JSONObject();
        for (Map.Entry<String, Object> pair : params.entrySet()) {
            store.put(pair.getKey(), pair.getValue());
        }

        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(store), tokenExpireTime, TimeUnit.SECONDS);
    }

    @Override
    public UserPayload getUserPayload(String secretKey, String token) throws IllegalSessionTokenException {

        String identify = getIdentify(secretKey, token);

        String key = buildKey(identify);
        Object store = redisTemplate.opsForValue().get(key);
        UserPayload payload = null;
        if (store != null) {
            String value = String.valueOf(store);
            if (!value.isEmpty()) {
                JSONObject source = JSONObject.parseObject(value);
                String userid = source.getString("userid");
                if (source.containsKey("type")) {
                    payload = UserPayload.build(userid, source.get("type"), source);
                } else {
                    payload = UserPayload.build(userid, source);
                }
            }
        }
        if (payload == null) {
            throw new IllegalSessionTokenException();
        }
        return payload;
    }
}