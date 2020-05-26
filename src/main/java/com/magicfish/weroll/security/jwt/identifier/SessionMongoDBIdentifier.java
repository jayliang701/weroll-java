package com.magicfish.weroll.security.jwt.identifier;

import com.alibaba.fastjson.JSONObject;
import com.magicfish.weroll.config.property.MongoDBProperties;
import com.magicfish.weroll.config.property.SessProperties;
import com.magicfish.weroll.exception.IllegalSessionTokenException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SessionMongoDBIdentifier extends AbstractSessionIdentifier {

    public SessionMongoDBIdentifier(SessProperties properties) {
        super(properties);

        MongoDBProperties mongodbProperties = properties.getMongodb();

        MongoClient mongoClient = MongoClients.create(mongodbProperties.getUri());

        mongoTemplate = new MongoTemplate(mongoClient, mongodbProperties.getDbname());
//        mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClientURI(mongodbProperties.getUri())));

        if (mongodbProperties.isAutoBuildIndex()) {
            buildDBCollectionIndex();
        }
    }

    private MongoTemplate mongoTemplate;

    private void buildDBCollectionIndex() {
        List<IndexInfo> indexes = mongoTemplate.indexOps(properties.getMongodb().getTable()).getIndexInfo();
        for (IndexInfo index : indexes) {
            if (index.getName().startsWith("expiredAt_")) {
                return;
            }
        }
        mongoTemplate.indexOps(properties.getMongodb().getTable()) // collection name string or .class
                .ensureIndex(
                        new Index().on("expiredAt", Sort.Direction.DESC).expire(properties.getTokenExpireTime(), TimeUnit.SECONDS)
                );
    }

    @Override
    public void saveUserPayload(String secretKey, String token, Map<String, Object> params, Long tokenExpireTime) {
        String identify = getIdentify(secretKey, token);

        Date expiredAt = new Date();
        expiredAt.setTime(expiredAt.getTime() + tokenExpireTime * 1000);

        Update ups = new Update();
        ups.set("_id", identify);
        ups.set("expiredAt", expiredAt);
        for (Map.Entry<String, Object> pair : params.entrySet()) {
            ups.set(pair.getKey(), pair.getValue());
        }

        Query query = new Query(Criteria.where("_id").is(identify));
        mongoTemplate.upsert(query, ups, properties.getMongodb().getTable());
    }

    @Override
    public UserPayload getUserPayload(String secretKey, String token) throws IllegalSessionTokenException {
        String identify = getIdentify(secretKey, token);
        Query query = new Query(Criteria.where("_id").is(identify));
        JSONObject store = mongoTemplate.findOne(query, JSONObject.class, properties.getMongodb().getTable());
        UserPayload payload = null;
        if (store != null) {
            String userid = store.getString("userid");
            if (store.containsKey("type")) {
                payload = UserPayload.build(userid, store.get("type"), store);
            } else {
                payload = UserPayload.build(userid, store);
            }
        }
        if (payload == null) {
            throw new IllegalSessionTokenException();
        }
        return payload;
    }

}
