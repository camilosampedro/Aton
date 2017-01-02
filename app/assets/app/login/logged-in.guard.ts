/**
 * Created by camilosampedro on 1/01/17.
 */
import { Injectable } from '@angular/core';
import { Router, CanActivate } from '@angular/router';
import { LoginService } from './login.service';
import { CookieService } from 'angular2-cookie/services/cookies.service';

@Injectable()
export class LoggedInGuard implements CanActivate {
    constructor(private _cookieService: CookieService) {}

    canActivate() {
        return this._cookieService.getObject("");
    }
}