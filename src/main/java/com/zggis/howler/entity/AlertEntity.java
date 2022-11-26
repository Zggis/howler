package com.zggis.howler.entity;

import com.zggis.howler.dto.AlertDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.awt.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class AlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long dataSourceId;

    private String matchingString;

    private boolean enabled = true;

    private String type;

    //Discord
    private String webhookUrl;

    private String color = Color.GREEN.toString();

    //Gotify
    private String serverUrl;

    private String token;

    public AlertEntity(AlertDTO alertDTO) {
        this.name = alertDTO.getName();
        this.dataSourceId = alertDTO.getDataSourceId();
        this.matchingString = alertDTO.getMatchingString();
        this.webhookUrl = alertDTO.getWebhookUrl();
        this.enabled = alertDTO.isEnabled();
        this.type = alertDTO.getType();
        this.color = alertDTO.getColor();
        this.serverUrl=alertDTO.getServerUrl();
        this.token = alertDTO.getToken();
    }
}
