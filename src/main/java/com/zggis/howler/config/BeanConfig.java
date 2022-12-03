package com.zggis.howler.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class BeanConfig {

    private static final Logger logger = LoggerFactory.getLogger(BeanConfig.class);

    @Autowired
    private BuildProperties buildProperties;

    @PostConstruct
    public void init() {
        logger.info("Running Howler v{}", buildProperties.getVersion());
    }
}
