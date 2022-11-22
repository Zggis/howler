import { Inject, Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { DOCUMENT } from '@angular/common';
import { environment } from 'src/environments/environment';
import { HttpClient } from '@angular/common/http';

export class DataSource {
  constructor(
    public id: number,
    public path: string
  ) { }
}

@Injectable({
  providedIn: 'root'
})
export class DatasourceService {

  private datasources = new BehaviorSubject<DataSource[]>([]);
  currentDatasources = this.datasources.asObservable();

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

  getDataSources() {
    this.httpClient.get<DataSource[]>('http://' + this.host + ':' + this.port + '/rest/datasource').subscribe(
      response => this.datasources.next(response)
    );
  }

  addDataSource(request: DataSource) {
    this.httpClient.post<DataSource>('http://' + this.host + ':' + this.port + '/rest/datasource', request).subscribe(
      response => response != null ? this.datasources.next([...this.datasources.value, response]) : {}
    );
  }

  deleteDataSource(id: number) {
    this.httpClient.delete<DataSource>('http://' + this.host + ':' + this.port + '/rest/datasource/' + id).subscribe(
      response => this.removeDataSource(id)
    );
  }

  removeDataSource(id: number) {
    this.datasources.next(
      this.datasources.value.filter(
        (ds: DataSource) => ds.id != id
      )
    );
  }
}
