import { Component, OnInit } from '@angular/core';
import { faPlus, faBell, faTriangleExclamation, faFaceFrown } from '@fortawesome/free-solid-svg-icons';
import { faDiscord } from '@fortawesome/free-brands-svg-icons';
import { Alert, AlertService } from 'src/app/service/alert.service';
import { DataSource, DatasourceService } from 'src/app/service/datasource.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.css']
})
export class AlertComponent implements OnInit {

  alerts: Alert[] = [];
  dataSources: DataSource[] = [];
  newAlert: Alert = new Alert('', -1, -1, '', '', '', true, 'DISCORD');

  faPlus = faPlus;
  faBell = faBell;
  faDiscord = faDiscord;
  faTriangleExclamation = faTriangleExclamation;
  faFaceFrown = faFaceFrown;

  constructor(private alertService: AlertService, private dataSourceService: DatasourceService, private modalService: NgbModal) { }

  ngOnInit(): void {
    this.dataSourceService.currentDatasources.subscribe(datasources => this.dataSources = datasources);
    this.dataSourceService.getDataSources();
    this.alertService.currentAlerts.subscribe(alerts => {
      this.alerts = [];
      alerts.forEach(alert =>
        this.dataSources.forEach(ds => {
          if (ds.id == alert.dataSourceId) {
            this.alerts.push(new Alert(alert.name, alert.id, alert.dataSourceId, ds.path, alert.webhookUrl, alert.matchingString, alert.enabled, alert.type));
          }
        }
        )
      )
    }
    );
    this.alertService.getAlerts();
  }

  addAlert() {
    this.alertService.addAlert(this.newAlert);
    this.modalService.dismissAll();
  }

  deleteAlert(id: number) {
    this.alertService.deleteAlert(id);
  }

  enableAlert(id: number) {
    this.alertService.enableAlert(id);
  }

  disableAlert(id: number) {
    this.alertService.disableAlert(id);
  }

  openModal(content: any) {
    this.reset();
    this.modalService.open(content);
  }

  reset() {
    this.newAlert = new Alert('', -1, -1, '', '', '', true, 'DISCORD');
    if (this.dataSources.length > 0) {
      this.newAlert.dataSourceId = this.dataSources[0].id;
    }
  }

}
