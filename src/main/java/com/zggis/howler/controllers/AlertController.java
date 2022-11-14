package com.zggis.howler.controllers;

import com.zggis.howler.dto.AlertDTO;
import com.zggis.howler.dto.DataSourceDTO;
import com.zggis.howler.entity.AlertEntity;
import com.zggis.howler.entity.DataSourceEntity;
import com.zggis.howler.services.AlertService;
import com.zggis.howler.services.DataSourceService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/alert")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @Operation(summary = "Fetch all alerts")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<AlertDTO>> fetchAllAlerts() {
        List<AlertDTO> result = new ArrayList<>();
        for (AlertEntity entity : alertService.findAll()) {
            result.add(new AlertDTO(entity));
        }
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Fetch a alert")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<AlertDTO> fetchAlert(@PathVariable Long id) {
        Optional<AlertEntity> findById = alertService.findById(id);
        if (findById.isPresent()) {
            return ResponseEntity.ok(new AlertDTO(findById.get()));
        }
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Add new alert")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<AlertDTO> addDataSource(@RequestBody AlertDTO newAlert) {
        AlertEntity newEntity = new AlertEntity(newAlert);
        AlertEntity result = alertService.add(newEntity);
        return ResponseEntity.ok(new AlertDTO(result));
    }

    @Operation(summary = "Remove alert")
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<AlertDTO> removeDataSource(@PathVariable Long id) {
        alertService.deleteById(id);
        return ResponseEntity.ok(null);
    }
}
