import { Component, OnInit } from '@angular/core';
import { faPlus } from '@fortawesome/free-solid-svg-icons';
import { Alert, AlertService } from 'src/app/service/alert.service';

@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.css']
})
export class AlertComponent implements OnInit {

  alerts: Alert[] = [];
  faPlus = faPlus;

  constructor(private alertService: AlertService) { }

  ngOnInit(): void {
    this.alertService.currentAlerts.subscribe(alerts => this.alerts = alerts);
    this.alertService.getAlerts();
  }

}
