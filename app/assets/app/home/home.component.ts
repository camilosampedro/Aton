/**
 * Created by camilosampedro on 11/12/16.
 */
/**
 * Created by camilosampedro on 30/12/16.
 */
import { Component }          from '@angular/core';
import {Router} from '@angular/router';
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'aton-home',
    templateUrl: 'assets/app/home/home.component.html',
    styleUrls: ['assets/app/home/home.component.css'],
})
export class HomeComponent{
    constructor(private router: Router) {}
}