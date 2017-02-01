/**
 * Created by camilosampedro on 1/01/17.
 */
import { Injectable } from '@angular/core';
import { Router, CanActivate } from '@angular/router';
import { LoginService } from './login.service';

@Injectable()
export class LoggedInGuard implements CanActivate {
    constructor() {}

    canActivate() {
        return LoginService.isLoggedIn();
    }
}