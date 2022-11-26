import { Component } from '@angular/core';
import { faCoffee, faBell, faFolder, faGear, faAt } from '@fortawesome/free-solid-svg-icons';
import { faGithub } from '@fortawesome/free-brands-svg-icons';
import packageJson from '../../package.json';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'howler';
  faCoffee = faCoffee;
  faBell = faBell;
  faFolder = faFolder;
  faGear = faGear;
  faGithub = faGithub;
  faAt = faAt;
  public version: string = packageJson.version;
}
