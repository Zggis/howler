import { Inject, Injectable } from '@angular/core';
import { BehaviorSubject, catchError, of, throwError } from 'rxjs';
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
    public color: string,
    public serverUrl:string,
    public token:string
  ) { }
}

@Injectable({
  providedIn: 'root'
})
export class AlertService {

  private alerts = new BehaviorSubject<Alert[]>([]);
  currentAlerts = this.alerts.asObservable();

  private alertError = new BehaviorSubject<String>("");
  currentAlertError = this.alertError.asObservable();

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
    this.httpClient.post<Alert>('http://' + this.host + ':' + this.port + '/rest/alert', request).pipe(
      catchError(error => {
        if (error.status == 410) {
          this.alertError.next("Alert was not created because an Alert with the same name, data source, and trigger event exists.");
        } else if (error.status == 411) {
          this.alertError.next("Alert was not created because the selected Data Source no longer exists.");
        } else if (error.status == 412) {
          this.alertError.next("Alert was not added because the Discord webhook is not valid.");
        } else if (error.status == 413) {
          this.alertError.next("Alert was not added because the Gotify server url is not valid.");
        }else if (error.status == 414) {
          this.alertError.next("Alert was not added because the Gotify API key was empty.");
        }else {
          this.alertError.next("An unexpected error occured while trying to add the alert.");
        }
        return throwError(() => new Error("Failed to add alert"));
      })
    ).subscribe(
      response => {
        if (response != null) {
          this.alerts.next([...this.alerts.value, response]);
          this.alertError.next("");
        }
      }
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
