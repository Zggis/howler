import { Component, OnInit } from '@angular/core';
import { faFaceFrown, faFolder, faPlus, faCircleExclamation, faXmark, faTriangleExclamation, faTrashCan } from '@fortawesome/free-solid-svg-icons';
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
  faFaceFrown = faFaceFrown;
  faCircleExclamation = faCircleExclamation;
  faTriangleExclamation = faTriangleExclamation;
  faXmark = faXmark;
  faTrashCan = faTrashCan;
  newDataSource: DataSource = new DataSource(-1, '');
  error: String = "";
  preDeleteId: number = -1;

  dataSources: DataSource[] = [];
  fileExtensions: String[] = [];

  constructor(private dataSourceService: DatasourceService, private modalService: NgbModal) { }

  ngOnInit(): void {
    this.dataSourceService.currentDSError.subscribe(error => this.error = error);
    this.error = "";
    this.dataSourceService.currentDatasources.subscribe(datasources => this.dataSources = datasources);
    this.dataSourceService.getDataSources();
    this.dataSourceService.currentFileExtensions.subscribe(fileExtensions => this.fileExtensions = fileExtensions);
    this.dataSourceService.getFileExtensions();
  }

  addDataSource(nameInput: HTMLInputElement) {
    if (this.newDataSource.path !== '') {
      nameInput.classList.remove('is-invalid');
      this.dataSourceService.addDataSource(this.newDataSource);
      this.modalService.dismissAll();
    } else {
      nameInput.classList.add('is-invalid');
    }
  }

  preDelete(id: number) {
    this.preDeleteId = id;
  }

  deleteDataSource() {
    this.dataSourceService.deleteDataSource(this.preDeleteId);
    this.preDeleteId = -1;
    this.modalService.dismissAll();
  }

  openModal(content: any) {
    this.reset();
    this.modalService.open(content);
  }

  reset() {
    this.newDataSource = new DataSource(-1, '');
  }

}
