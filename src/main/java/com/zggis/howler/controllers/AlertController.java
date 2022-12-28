package com.zggis.howler.controllers;

import com.zggis.howler.dto.AlertDTO;
import com.zggis.howler.entity.AlertEntity;
import com.zggis.howler.exceptions.InvalidAlertException;
import com.zggis.howler.services.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/alert")
public class AlertController {

    private static final Logger logger = LoggerFactory.getLogger(AlertController.class);

    @Autowired
    private AlertService alertService;

    @Operation(summary = "Fetch all alerts")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<AlertDTO>> fetchAllAlerts() {
        List<AlertDTO> result = new ArrayList<>();
        for (AlertEntity entity : alertService.findAll()) {
            result.add(new AlertDTO(entity));
        }
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Add new alert")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<AlertDTO> addAlert(@RequestBody AlertDTO newAlert) {
        AlertEntity newEntity = new AlertEntity(newAlert);
        try {
            AlertEntity result = alertService.add(newEntity);
            return ResponseEntity.ok(new AlertDTO(result));
        } catch (InvalidAlertException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @Operation(summary = "Edit alert")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<AlertDTO> updateAlert(@RequestBody AlertDTO updatedAlert) {
        try {
            AlertEntity result = alertService.update(updatedAlert.getId(), updatedAlert.getName(), updatedAlert.getMatchingString());
            return ResponseEntity.ok(new AlertDTO(result));
        } catch (InvalidAlertException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @Operation(summary = "Remove alert")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<AlertDTO> removeAlert(@PathVariable Long id) {
        alertService.deleteById(id);
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "Enable alert")
    @RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
    public ResponseEntity<AlertDTO> enableAlert(@PathVariable Long id) {
        AlertEntity alertEntity = alertService.setEnabled(id, true);
        return ResponseEntity.ok(new AlertDTO(alertEntity));
    }

    @Operation(summary = "Disable alert")
    @RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
    public ResponseEntity<AlertDTO> disableAlert(@PathVariable Long id) {
        AlertEntity alertEntity = alertService.setEnabled(id, false);
        return ResponseEntity.ok(new AlertDTO(alertEntity));
    }

    @Operation(summary = "Test alert")
    @RequestMapping(value = "/test/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> testAlert(@PathVariable Long id) {
        alertService.test(id);
        return ResponseEntity.ok(null);
    }
}
