import { Component }          from '@angular/core';

@Component({
//  moduleId: module.id,
  selector: 'my-app',
  templateUrl: 'assets/app/app.component.html',
  styleUrls: ['assets/app/app.component.css'],
})
export class AppComponent {
  public title = 'Tour of Heroes';
  public username: string;
  public isAdmin: boolean;
}

/*
Copyright 2016 Google Inc. All Rights Reserved.
Use of this source code is governed by an MIT-style license that
can be found in the LICENSE file at http://angular.io/license
*/
