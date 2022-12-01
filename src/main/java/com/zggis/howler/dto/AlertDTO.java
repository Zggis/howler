package com.zggis.howler.dto;

import com.zggis.howler.entity.AlertEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class AlertDTO implements Serializable {

    private static final long serialVersionUID = 4735709064513370781L;

    private Long id;

    private String name;

    private Long dataSourceId;

    private String matchingString;

    private boolean enabled = true;

    private String type;

    //Discord
    private String webhookUrl;

    private String color = Color.GREEN.toString();

    private String username = "Howler";

    //Gotify
    private String serverUrl;

    private String token;

    public AlertDTO(AlertEntity entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.dataSourceId = entity.getDataSourceId();
        this.matchingString = entity.getMatchingString();
        this.webhookUrl = entity.getWebhookUrl();
        this.enabled = entity.isEnabled();
        this.type = entity.getType();
        this.color = entity.getColor();
        this.username = entity.getUsername();
        this.serverUrl = entity.getServerUrl();
        this.token = entity.getToken();
    }
}
