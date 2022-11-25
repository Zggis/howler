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
  newAlert: Alert = new Alert('', -1, -1, '', '', true, 'DISCORD', '', 'GREEN');
  error: String = "";

  faPlus = faPlus;
  faBell = faBell;
  faDiscord = faDiscord;
  faTriangleExclamation = faTriangleExclamation;
  faFaceFrown = faFaceFrown;

  constructor(private alertService: AlertService, private dataSourceService: DatasourceService, private modalService: NgbModal) { }

  ngOnInit(): void {
    this.alertService.currentAlertError.subscribe(error => this.error = error);
    this.error = "";
    this.dataSourceService.currentDatasources.subscribe(datasources => this.dataSources = datasources);
    this.dataSourceService.getDataSources();
    this.alertService.currentAlerts.subscribe(alerts => {
      this.alerts = [];
      alerts.forEach(alert =>
        this.dataSources.forEach(ds => {
          if (ds.id == alert.dataSourceId) {
            this.alerts.push(new Alert(alert.name, alert.id, alert.dataSourceId, ds.path, alert.matchingString, alert.enabled, alert.type, alert.webhookUrl, alert.color));
          }
        }
        )
      )
    }
    );
    this.alertService.getAlerts();
  }

  addAlert(nameInput: HTMLInputElement) {
    if (this.newAlert.name !== '') {
      nameInput.classList.remove('is-invalid');
      this.alertService.addAlert(this.newAlert);
      this.modalService.dismissAll();
    } else {
      nameInput.classList.add('is-invalid');
    }
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

  testAlert(id: number) {
    this.alertService.testAlert(id);
  }

  openModal(content: any) {
    this.reset();
    this.modalService.open(content, { size: 'lg' });
  }

  reset() {
    this.newAlert = new Alert('', -1, -1, '', '', true, 'DISCORD', '', 'GREEN');
    if (this.dataSources.length > 0) {
      this.newAlert.dataSourceId = this.dataSources[0].id;
    }
  }

}
