package com.zggis.howler.controllers;

import com.zggis.howler.dto.DataSourceDTO;
import com.zggis.howler.entity.DataSourceEntity;
import com.zggis.howler.services.DataSourceService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/datasource")
public class DataSourceController {

    @Autowired
    private DataSourceService dataSourceService;

    @Operation(summary = "Fetch all data sources")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<DataSourceDTO>> fetchAllDataSources() {
        List<DataSourceDTO> result = new ArrayList<>();
        for (DataSourceEntity entity : dataSourceService.findAll()) {
            result.add(new DataSourceDTO(entity));
        }
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Fetch a data source")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<DataSourceDTO> fetchDataSource(@PathVariable Long id) {
        Optional<DataSourceEntity> findById = dataSourceService.findById(id);
        if (findById.isPresent()) {
            return ResponseEntity.ok(new DataSourceDTO(findById.get()));
        }
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Add new data source")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<DataSourceDTO> addDataSource(@RequestBody DataSourceDTO newDataSource) {
        DataSourceEntity newEntity = new DataSourceEntity(newDataSource);
        DataSourceEntity result = dataSourceService.add(newEntity);
        return ResponseEntity.ok(new DataSourceDTO(result));
    }

    @Operation(summary = "Remove data source")
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<DataSourceDTO> removeDataSource(@PathVariable Long id) {
        dataSourceService.deleteById(id);
        return ResponseEntity.ok(null);
    }
}
