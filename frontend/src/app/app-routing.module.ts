import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AlertComponent } from './alert/alert.component';
import { DatasourceComponent } from './datasource/datasource.component';

const routes: Routes = [
  { path: '', component: AlertComponent },
  { path: 'datasources', component: DatasourceComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
