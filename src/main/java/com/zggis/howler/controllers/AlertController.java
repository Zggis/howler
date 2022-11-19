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
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<AlertDTO> addDataSource(@RequestBody AlertDTO newAlert) {
        if (pattern.matcher(newAlert.getWebhookUrl()).matches()) {
            AlertEntity newEntity = new AlertEntity(newAlert);
            AlertEntity result = alertService.add(newEntity);
            return ResponseEntity.ok(new AlertDTO(result));
        }
        logger.error("Alert was not added because {} is not a valid Discord webhook", newAlert.getWebhookUrl());
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Remove alert")
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<AlertDTO> removeDataSource(@PathVariable Long id) {
        alertService.deleteById(id);
        return ResponseEntity.ok(null);
    }
}
