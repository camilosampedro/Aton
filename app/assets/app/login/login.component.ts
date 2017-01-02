/**
 * Created by camilosampedro on 17/12/16.
 */

import { Component, ViewChild, ElementRef }          from '@angular/core';
import {LoginService} from "./login.service";
import { Router } from '@angular/router';
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'aton-login',
    templateUrl: 'assets/app/login/login.component.html',
    styleUrls: ['assets/app/login/login.component.css'],
})
export class LoginComponent{
    @ViewChild('loginModal') loginModal: any;
    password: string = "";
    username: string = "";

    constructor(private  loginService: LoginService, private router: Router){}

    submit(){
        let result = this.loginService.login(this.username, this.password).subscribe(response => {
                localStorage.setItem('token', response.headers.get("PLAY2AUTH_SESS_ID"));
                this.loginModal.hide();
                this.router.navigate(['home']);
            },
            error => {
                alert(error.text());
                console.log(error.text());
            });
        if (result) {

        }
    }
}