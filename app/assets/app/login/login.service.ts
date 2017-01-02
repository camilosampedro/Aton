/**
 * Created by camilosampedro on 1/01/17.
 */
import {Injectable} from '@angular/core';
import {Http, Headers} from '@angular/http';
import {Router} from '@angular/router';

@Injectable()
export class LoginService {
    private loggedIn = false;

    constructor(private http: Http, private router: Router) {
        this.loggedIn = !!localStorage.getItem('auth_token');
    }

    login(username: string, password: string) {
        let headers = new Headers();
        headers.append('Content-Type', 'application/json');

        return this.http
            .post(
                '/api/login',
                JSON.stringify({username, password}),
                {headers}
            )
    }

    logout() {
        this.http
            .get('/api/logout')
            .map(res => res.json())
            .map((res) => {
                if (res.success) {
                    localStorage.removeItem('auth_token')
                }
            });
    }

    isLoggedIn() {
        return this.loggedIn;
    }
}