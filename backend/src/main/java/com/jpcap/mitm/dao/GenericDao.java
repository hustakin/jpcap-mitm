/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.dao;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


/**
 * Generic query for all entities
 *
 * @author Frankie
 */
@Repository
public class GenericDao<T> {
    @Autowired
    @Qualifier("mongoTemplate")
    private MongoTemplate mongoTemplate;

    public void insert(T t) {
        mongoTemplate.insert(t);
    }

    public void save(T t) {
        mongoTemplate.save(t);
    }

    public void insertAll(List<T> ts) {
        mongoTemplate.insertAll(ts);
    }

    public <T> T findById(String id, Class<T> clazz) {
        Query query = new Query(Criteria.where("id").is(id));
        T t = mongoTemplate.findOne(query, clazz);
        return t;
    }

    public List<T> findAll(Class<T> clazz) {
        List<T> ts = mongoTemplate.findAll(clazz);
        return ts;
    }

    public <T> T findOne(Query query, Class<T> clazz) {
        T t = mongoTemplate.findOne(query, clazz);
        return t;
    }

    public List<T> find(Query query, Class<T> clazz) {
        List<T> ts = mongoTemplate.find(query, clazz);
        return ts;
    }

    public long count(Query query, Class<T> clazz) {
        long count = mongoTemplate.count(query, clazz);
        return count;
    }

    public boolean exist(Query query, Class<T> clazz) {
        return mongoTemplate.exists(query, clazz);
    }

    public long updateColumnById(String id, String column, Object value, Class<T> clazz) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = Update.update("updated", new Date());
        update.set(column, value);
        UpdateResult rs = mongoTemplate.updateFirst(query, update, clazz);
        return rs.getModifiedCount();
    }

    public long updateById(String id, Update update, Class<T> clazz) {
        Query query = new Query(Criteria.where("id").is(id));
        update.set("updated", new Date());
        UpdateResult rs = mongoTemplate.updateFirst(query, update, clazz);
        return rs.getModifiedCount();
    }

    /**
     * When id field is String type
     *
     * @param id
     * @param clazz
     * @return
     */
    public long deleteById(String id, Class<T> clazz) {
        Query query = new Query(Criteria.where("_id").is(id));
        DeleteResult rs = mongoTemplate.remove(query, clazz);
        return rs.getDeletedCount();
    }

    /**
     * When id field is OID type
     *
     * @param id
     * @param clazz
     * @return
     */
    public long deleteByOid(String id, Class<T> clazz) {
        Query query = new Query(Criteria.where("id").is(id));
        DeleteResult rs = mongoTemplate.remove(query, clazz);
        return rs.getDeletedCount();
    }

    public long delete(Query query, Class<T> clazz) {
        DeleteResult rs = mongoTemplate.remove(query, clazz);
        return rs.getDeletedCount();
    }

    public void clean(Class<T> clazz) {
        mongoTemplate.dropCollection(clazz);
    }
}
