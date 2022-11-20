package com.zggis.howler.controllers;

import com.zggis.howler.dto.DataSourceDTO;
import com.zggis.howler.entity.DataSourceEntity;
import com.zggis.howler.services.DataSourceService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Operation(summary = "Fetch all data sources")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<DataSourceDTO>> fetchAllDataSources() {
        List<DataSourceDTO> result = new ArrayList<>();
        for (DataSourceEntity entity : dataSourceService.findAll()) {
            result.add(new DataSourceDTO(entity));
        }
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Add new data source")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<DataSourceDTO> addDataSource(@RequestBody DataSourceDTO newDataSource) {
        Path file = new File(newDataSource.getPath()).toPath();
        boolean exists = Files.exists(file);        // Check if the file exists
        boolean isDirectory = Files.isDirectory(file);   // Check if it's a directory
        if (exists && isDirectory) {
            DataSourceEntity newEntity = new DataSourceEntity(newDataSource);
            DataSourceEntity result = dataSourceService.add(newEntity);
            return ResponseEntity.ok(new DataSourceDTO(result));
        }
        logger.error("Data Source was not created because {} is not a valid directory path", newDataSource.getPath());
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Remove data source")
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<DataSourceDTO> removeDataSource(@PathVariable Long id) {
        dataSourceService.deleteById(id);
        return ResponseEntity.ok(null);
    }
}
