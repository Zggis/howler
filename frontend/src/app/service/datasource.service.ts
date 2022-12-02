import { Inject, Injectable } from '@angular/core';
import { BehaviorSubject, catchError, throwError } from 'rxjs';
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

  private dsError = new BehaviorSubject<String>("");
  currentDSError = this.dsError.asObservable();

  private fileExtensions = new BehaviorSubject<String[]>([]);
  currentFileExtensions = this.fileExtensions.asObservable();

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

  getFileExtensions() {
    this.httpClient.get<String[]>('http://' + this.host + ':' + this.port + '/rest/datasource/extensions').subscribe(
      response => this.fileExtensions.next(response)
    );
  }

  addDataSource(request: DataSource) {
    this.httpClient.post<DataSource>('http://' + this.host + ':' + this.port + '/rest/datasource', request).pipe(
      catchError(error => {
        if (error.status == 410) {
          this.dsError.next("Data source was not created because a data source with the same path already exists.");
        } else if (error.status == 411) {
          this.dsError.next("Data source was not created because the path does not exist.");
        } else {
          this.dsError.next("An unexpected error occured while trying to add the data source.");
        }
        return throwError(() => new Error("Failed to add data source"));
      })
    ).subscribe(
      response => {
        if (response != null) {
          this.datasources.next([...this.datasources.value, response]);
          this.dsError.next("");
        }
      }
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
