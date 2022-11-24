package com.zggis.howler.controllers;

import com.zggis.howler.dto.AlertDTO;
import com.zggis.howler.entity.AlertEntity;
import com.zggis.howler.services.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/rest/alert")
public class AlertController {

    private static final Logger logger = LoggerFactory.getLogger(AlertController.class);

    private static final Pattern pattern = Pattern.compile("^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

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
        if (pattern.matcher(newAlert.getWebhookUrl()).matches()) {
            AlertEntity newEntity = new AlertEntity(newAlert);
            AlertEntity result = alertService.add(newEntity);
            if (result != null) {
                return ResponseEntity.ok(new AlertDTO(result));
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
        logger.error("Alert was not added because {} is not a valid Discord webhook", newAlert.getWebhookUrl());
        return ResponseEntity.badRequest().build();
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
