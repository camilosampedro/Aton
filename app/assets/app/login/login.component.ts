/**
 * Created by camilosampedro on 17/12/16.
 */

import { Component }          from '@angular/core';
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'aton-login',
    templateUrl: 'assets/app/login/login.component.html',
    styleUrls: ['assets/app/login/login.component.css'],
})
export class LoginComponent{
    awww:boolean = false;
    formSubmitted: boolean = false;
    password: string = "";
    username: string = "";

    constructor(){
    }

    a() {
        console.log("clicked!");
        this.awww=true;
    }

    submit(){
        console.log(`username ${this.username}, password ${this.password}`);
    }
}