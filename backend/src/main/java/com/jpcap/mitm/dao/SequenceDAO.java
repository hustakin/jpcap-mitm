/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.dao;

import com.jpcap.mitm.entity.Sequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * @author Frankie
 */
@Repository
public class SequenceDAO {
    public static final String BATCH_ID = "BATCH_ID";

    @Autowired
    private MongoTemplate mongoTemplate;

    public long getNextSequence(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        Update update = new Update().inc("seq", 1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(true).returnNew(true);
        Sequence seq = mongoTemplate.findAndModify(query, update, options, Sequence.class);
        return seq.getSeq();
    }

}
