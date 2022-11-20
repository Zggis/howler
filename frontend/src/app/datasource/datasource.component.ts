import { Component, OnInit } from '@angular/core';
import { faPlus } from '@fortawesome/free-solid-svg-icons';
import { DataSource, DatasourceService } from '../service/datasource.service';

@Component({
  selector: 'app-datasource',
  templateUrl: './datasource.component.html',
  styleUrls: ['./datasource.component.css']
})
export class DatasourceComponent implements OnInit {

  faPlus = faPlus;

  dataSources: DataSource[] = [];

  constructor(private dataSourceService: DatasourceService) { }

  ngOnInit(): void {
    this.dataSourceService.currentDatasources.subscribe(datasources => this.dataSources = datasources);
    this.dataSourceService.getDataSources();
  }

}
