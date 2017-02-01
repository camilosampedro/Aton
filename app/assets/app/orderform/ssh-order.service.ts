/**
 * Created by camilosampedro on 29/01/17.
 */
import {Injectable} from '@angular/core';
import {Http, Headers} from '@angular/http';
import {Router} from '@angular/router';
import {SSHOrder, MinimalSSHOrder} from "./ssh-order.model";
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';
import {Computer} from "../computer/computer.model";

@Injectable()
export class SSHOrderService {
    constructor(private http: Http, private router: Router){}

    getAll() {
        return this.http
            .get('/api/laboratories')
            .map(res=>res.json())
    }

    sendOrder(sshOrder: MinimalSSHOrder, computer: Computer) {
        return this.http.post('/api/laboratory',sshOrder);
    }

    deleteLaboratory(id: number){
        return this.http.delete(`/api/laboratory/${id}`).map(res=>res.json());
    }

    getLaboratory(id: number) {
        return this.http.get(`/api/laboratory/${id}`).map(res=>res.json())
    }
}