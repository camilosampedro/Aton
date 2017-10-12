/**
 * Created by camilosampedro on 2/01/17.
 */
import {Injectable} from '@angular/core';
import {Http, Headers} from '@angular/http';
import {Router} from '@angular/router';
import {Laboratory} from "./laboratory.model";
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';

@Injectable()
export class LaboratoryService {
    constructor(private http: Http, private router: Router){}

    getAll() {
        return this.http
            .get('/api/laboratories')
            .map(res=>res.json())
    }

    addNew(laboratory:Laboratory) {
        return this.http.post('/api/laboratory',laboratory);
    }

    deleteLaboratory(id: number){
        return this.http.delete(`/api/laboratory/${id}`).map(res=>res.json());
    }

    getLaboratory(id: number) {
        return this.http.get(`/api/laboratory/${id}`).map(res=>res.json())
    }
}