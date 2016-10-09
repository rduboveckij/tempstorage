package com.rdu.temp.storage.service.gridfs;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;


/**
 * @author rdu
 * @since 05.10.2016
 */
@Configuration
public class GridFsDataSourceConfig extends AbstractMongoConfiguration {
    private final GridFsProperties gridFsProperties;

    @Autowired
    public GridFsDataSourceConfig(GridFsProperties gridFsProperties) {
        this.gridFsProperties = gridFsProperties;
    }

    @Override
    protected String getDatabaseName() {
        return gridFsProperties.getDatabaseName();
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient(gridFsProperties.getDatabaseHost());
    }

    @Bean
    public GridFsTemplate gridFsTemplate() throws Exception {
        return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory());
    }
}
