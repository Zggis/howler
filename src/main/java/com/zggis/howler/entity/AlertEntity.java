package com.zggis.howler.entity;

import com.zggis.howler.dto.AlertDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

    private String webhookUrl;

    private boolean enabled = true;

    public AlertEntity(AlertDTO alertDTO) {
        this.name = alertDTO.getName();
        this.dataSourceId = alertDTO.getDataSourceId();
        this.matchingString = alertDTO.getMatchingString();
        this.webhookUrl = alertDTO.getWebhookUrl();
        this.enabled = alertDTO.isEnabled();
    }
}
