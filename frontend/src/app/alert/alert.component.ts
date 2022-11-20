import { Component, OnInit } from '@angular/core';
import { faPlus } from '@fortawesome/free-solid-svg-icons';
import { Alert, AlertService } from 'src/app/service/alert.service';
import { DataSource, DatasourceService } from 'src/app/service/datasource.service';

@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.css']
})
export class AlertComponent implements OnInit {

  alerts: Alert[] = [];

  dataSources: DataSource[] = [];

  faPlus = faPlus;

  constructor(private alertService: AlertService, private dataSourceService: DatasourceService) { }

  ngOnInit(): void {
    this.dataSourceService.currentDatasources.subscribe(datasources => this.dataSources = datasources);
    this.dataSourceService.getDataSources();
    this.alertService.currentAlerts.subscribe(alerts =>
      alerts.forEach(alert =>
        this.dataSources.forEach(ds => {
          if (ds.id == alert.dataSourceId) {
            this.alerts.push(new Alert(alert.name, alert.id, alert.dataSourceId, ds.path, alert.webhookUrl, alert.matchingString));
          }
        }
        )
      )
    );
    this.alerts = [];
    this.alertService.getAlerts();
  }

}
