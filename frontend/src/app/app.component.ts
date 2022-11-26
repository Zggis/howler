import { Component } from '@angular/core';
import { faCoffee, faBell, faFolder, faGear } from '@fortawesome/free-solid-svg-icons';
import { faGithub } from '@fortawesome/free-brands-svg-icons';

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
}
