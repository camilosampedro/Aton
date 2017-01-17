/**
 * Created by camilosampedro on 2/01/17.
 */
import {Injectable} from '@angular/core';
import {Http, Headers} from '@angular/http';
import {Router} from '@angular/router';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';
import {Computer} from "./computer.model";
import {LoginService} from "../login/login.service";

@Injectable()
export class ComputerService {
    constructor(private http: Http, private router: Router){}

    addNew(computer: Computer) {
        return this.http.post('/api/computer',computer);
    }

    deleteComputer(ip: number){
        return this.http.delete(`/api/computer/${ip}`).map(res=>res.json());
    }

    getComputer(ip: number) {
        return this.http.get(`/api/computer/${ip}`).map(res=>res.json())
    }

    editComputer(computer: Computer){
        return this.http.put('/api/computer', computer).map(res=>{
            if(res.status == 403){
                LoginService.deleteToken()
            }
            return res
        })
    }
}