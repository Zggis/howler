import { Component, OnInit } from '@angular/core';
import { faPlus, faFolder } from '@fortawesome/free-solid-svg-icons';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DataSource, DatasourceService } from '../service/datasource.service';

@Component({
  selector: 'app-datasource',
  templateUrl: './datasource.component.html',
  styleUrls: ['./datasource.component.css']
})
export class DatasourceComponent implements OnInit {

  faPlus = faPlus;
  faFolder = faFolder;
  newDataSource: DataSource = new DataSource(-1, '');

  dataSources: DataSource[] = [];

  constructor(private dataSourceService: DatasourceService, private modalService: NgbModal) { }

  ngOnInit(): void {
    this.dataSourceService.currentDatasources.subscribe(datasources => this.dataSources = datasources);
    this.dataSourceService.getDataSources();
  }

  addDataSource() {
    this.dataSourceService.addDataSource(this.newDataSource);
    this.modalService.dismissAll();
  }

  deleteDataSource(id: number) {
    this.dataSourceService.deleteDataSource(id);
  }

  openModal(content: any) {
    this.reset();
    this.modalService.open(content);
  }

  reset() {
    this.newDataSource = new DataSource(-1, '');
  }

}
