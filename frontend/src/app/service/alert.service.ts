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
    public webhookUrl: string,
    public matchingString: string
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
}
