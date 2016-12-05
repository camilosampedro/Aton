import {NgModule}             from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {HomeComponent}   from './home/home.component';
import {HeroesComponent}      from './heroes.component';
import {HeroDetailComponent}  from './hero-detail.component';

const routes: Routes = [
    {path: '', redirectTo: '/laboratories', pathMatch: 'full'},
    {path: 'laboratories', component: HomeComponent},
    {path: 'detail/:id', component: HeroDetailComponent},
    {path: 'heroes', component: HeroesComponent},

];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule],
})
export class AppRoutingModule {
}

/*
 Copyright 2016 Google Inc. All Rights Reserved.
 Use of this source code is governed by an MIT-style license that
 can be found in the LICENSE file at http://angular.io/license
 */
