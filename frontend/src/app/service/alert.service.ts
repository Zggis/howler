import { Inject, Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { DOCUMENT } from '@angular/common';
import { environment } from 'src/environments/environment';
import { HttpClient } from '@angular/common/http';

export class Alert {
  constructor(
    public name: string,
    public id: number,
    public dataSourceId: number,
    public dataSourcePath: string,
    public matchingString: string,
    public enabled: boolean,
    public type: string,
    public webhookUrl: string,
    public color: string
  ) { }
}

@Injectable({
  providedIn: 'root'
})
export class AlertService {

  private alerts = new BehaviorSubject<Alert[]>([]);
  currentAlerts = this.alerts.asObservable();

  private port: string;
  private host: string = this.document.location.hostname;

  constructor(
    private httpClient: HttpClient,
    @Inject(DOCUMENT) private document: any
  ) {
    if (environment.production) {
      this.port = this.document.location.port;
    } else {
      this.port = "8080";
    }
  }

  getHostAddress() {
    return 'http://' + this.host + ':' + this.port;
  }

  getAlerts() {
    this.httpClient.get<Alert[]>('http://' + this.host + ':' + this.port + '/rest/alert').subscribe(
      response => this.alerts.next(response)
    );
  }

  addAlert(request: Alert) {
    this.httpClient.post<Alert>('http://' + this.host + ':' + this.port + '/rest/alert', request).subscribe(
      response => response != null ? this.alerts.next([...this.alerts.value, response]) : {}
    );
  }

  deleteAlert(id: number) {
    this.httpClient.delete<Alert>('http://' + this.host + ':' + this.port + '/rest/alert/' + id).subscribe(
      response => this.removeAlert(id)
    );
  }

  removeAlert(id: number) {
    this.alerts.next(
      this.alerts.value.filter(
        (alert: Alert) => alert.id != id
      )
    );
  }

  updateAlert(incomingAlert: Alert) {
    const updatedAlerts = this.alerts.getValue().map((alert) => {
      if (alert.id === incomingAlert.id) {
        return { ...incomingAlert }
      }
      return alert;
    })
    this.alerts.next(updatedAlerts);
  }

  enableAlert(id: number) {
    this.httpClient.put<Alert>('http://' + this.host + ':' + this.port + '/rest/alert/enable/' + id, null).subscribe(
      response => this.updateAlert(response)
    );
  }

  disableAlert(id: number) {
    this.httpClient.put<Alert>('http://' + this.host + ':' + this.port + '/rest/alert/disable/' + id, null).subscribe(
      response => this.updateAlert(response)
    );
  }

  testAlert(id: number) {
    this.httpClient.post('http://' + this.host + ':' + this.port + '/rest/alert/test/' + id, null).subscribe();
  }
}
