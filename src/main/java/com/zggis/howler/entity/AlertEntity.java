package com.zggis.howler.entity;

import com.zggis.howler.dto.AlertDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    @Column(columnDefinition = "boolean default false")
    private boolean regularExp = false;

    @Column(columnDefinition = "boolean default true")
    private boolean enabled = true;

    private String type;

    //Discord
    private String webhookUrl;

    private String color = Color.GREEN.toString();

    private String username = "Howler";

    //Gotify
    private String serverUrl;

    private String token;

    public AlertEntity(AlertDTO alertDTO) {
        this.name = alertDTO.getName();
        this.dataSourceId = alertDTO.getDataSourceId();
        this.matchingString = alertDTO.getMatchingString();
        this.regularExp = alertDTO.isRegularExp();
        this.webhookUrl = alertDTO.getWebhookUrl();
        this.enabled = alertDTO.isEnabled();
        this.type = alertDTO.getType();
        this.color = alertDTO.getColor();
        this.username = alertDTO.getUsername();
        this.serverUrl=alertDTO.getServerUrl();
        this.token = alertDTO.getToken();
    }
}
