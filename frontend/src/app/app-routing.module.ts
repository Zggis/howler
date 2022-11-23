import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AlertComponent } from './alert/alert.component';
import { DatasourceComponent } from './datasource/datasource.component';
import { SettingsComponent } from './settings/settings.component';

const routes: Routes = [
  { path: '', component: AlertComponent },
  { path: 'datasources', component: DatasourceComponent },
  { path: 'settings', component: SettingsComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
