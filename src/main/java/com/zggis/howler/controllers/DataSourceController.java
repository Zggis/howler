package com.zggis.howler.controllers;

import com.zggis.howler.dto.DataSourceDTO;
import com.zggis.howler.entity.DataSourceEntity;
import com.zggis.howler.exceptions.InvalidDataSourceException;
import com.zggis.howler.services.DataSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/datasource")
public class DataSourceController {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceController.class);

    @Autowired
    private DataSourceService dataSourceService;

    @Value("${file.types}")
    private String fileExtensions;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<DataSourceDTO>> fetchAllDataSources() {
        List<DataSourceDTO> result = new ArrayList<>();
        for (DataSourceEntity entity : dataSourceService.findAll()) {
            result.add(new DataSourceDTO(entity));
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<DataSourceDTO> addDataSource(@RequestBody DataSourceDTO newDataSource) {
        Path file = new File(newDataSource.getPath()).toPath();
        boolean exists = Files.exists(file);
        boolean isDirectory = Files.isDirectory(file);
        if (exists && isDirectory) {
            DataSourceEntity newEntity = new DataSourceEntity(newDataSource);
            try {
                DataSourceEntity result = dataSourceService.add(newEntity);
                return ResponseEntity.ok(new DataSourceDTO(result));
            } catch (InvalidDataSourceException e) {
                logger.error(e.getMessage());
                return ResponseEntity.status(e.getStatusCode()).build();
            }
        }
        logger.error("Data Source was not created because {} is not a valid directory path", newDataSource.getPath());
        return ResponseEntity.status(411).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<DataSourceDTO> removeDataSource(@PathVariable Long id) {
        dataSourceService.deleteById(id);
        return ResponseEntity.ok(null);
    }

    @RequestMapping(value = "/extensions", method = RequestMethod.GET)
    public ResponseEntity<List<String>> fetchAllFileTypes() {
        List<String> result = new ArrayList<>();
        for (String ext : fileExtensions.split(",")) {
            result.add(ext.toUpperCase());
        }
        return ResponseEntity.ok(result);
    }
}
