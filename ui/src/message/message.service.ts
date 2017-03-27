/**
 * Created by camilosampedro on 2/01/17.
 */
import {Injectable} from '@angular/core';
import {Http, Headers} from '@angular/http';
import {Router} from '@angular/router';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';

@Injectable()
export class MessageService {
    constructor(private http: Http, private router: Router){}

    sendMessage(message:[string[],string]) {
        let headers = new Headers();
        headers.append('Content-Type', 'application/json');

        let body = JSON.stringify({
            ips: message[0],
            text: message[1]
        });

        console.log(body);
        return this.http.post('/api/computer/send-message',body, {headers});
    }
}