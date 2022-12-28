import { Component, OnInit, ViewChild } from '@angular/core';
import { faClone, faPlus, faBell, faTriangleExclamation, faFaceFrown, faCircleExclamation, faXmark, faKiwiBird, faLink, faPalette, faKey, faFolder, faTrashCan, faBellSlash, faUser } from '@fortawesome/free-solid-svg-icons';
import { faDiscord, faSlack } from '@fortawesome/free-brands-svg-icons';
import { Alert, AlertService } from 'src/app/service/alert.service';
import { DataSource, DatasourceService } from 'src/app/service/datasource.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.css']
})
export class AlertComponent implements OnInit {

  @ViewChild("addAlertModal")
  addAlertModal!: NgbModal;

  alerts: Alert[] = [];
  dataSources: DataSource[] = [];
  newAlert: Alert = new Alert('', -1, -1, '', '', true, 'DISCORD', '', 'GREEN', 'Howler', '', '');
  error: String = "";

  faPlus = faPlus;
  faBell = faBell;
  faBellSlash = faBellSlash;
  faXmark = faXmark;
  faDiscord = faDiscord;
  faTriangleExclamation = faTriangleExclamation;
  faFaceFrown = faFaceFrown;
  faKiwiBird = faKiwiBird;
  faCircleExclamation = faCircleExclamation;
  faLink = faLink;
  faPalette = faPalette;
  faKey = faKey;
  faFolder = faFolder;
  faTrashCan = faTrashCan;
  faUser = faUser;
  faSlack = faSlack;
  faClone = faClone;

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
            this.alerts.push(new Alert(alert.name, alert.id, alert.dataSourceId, ds.path, alert.matchingString, alert.enabled, alert.type, alert.webhookUrl, alert.color, alert.username, alert.serverUrl, alert.token));
          }
        }
        )
      )
    }
    );
    this.alertService.getAlerts();
  }

  addAlert(nameInput: HTMLInputElement, triggerEventInput: HTMLInputElement) {
    var valid = true;
    if (this.newAlert.name !== '') {
      nameInput.classList.remove('is-invalid');
    } else {
      valid = false;
      nameInput.classList.add('is-invalid');
    }
    if (this.newAlert.matchingString !== '') {
      triggerEventInput.classList.remove('is-invalid');
    } else {
      valid = false;
      triggerEventInput.classList.add('is-invalid');
    }
    if (valid) {
      this.alertService.addAlert(this.newAlert);
      this.modalService.dismissAll();
    }
  }

  cloneAlert(alert: Alert){
    this.reset();
    this.newAlert = Object.assign({}, alert);
    this.newAlert.name = '';
    this.modalService.open(this.addAlertModal, { size: 'lg' });
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
    this.newAlert = new Alert('', -1, -1, '', '', true, 'DISCORD', '', 'GREEN', 'Howler', '', '');
    if (this.dataSources.length > 0) {
      this.newAlert.dataSourceId = this.dataSources[0].id;
    }
  }

}
