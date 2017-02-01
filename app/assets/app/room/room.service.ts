/**
 * Created by camilosampedro on 2/01/17.
 */
import {Injectable} from '@angular/core';
import {Http, Headers} from '@angular/http';
import {Router} from '@angular/router';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';
import {Room} from "./room.model";

@Injectable()
export class RoomService {
    constructor(private http: Http, private router: Router){}

    addNew(room: Room) {
        return this.http.post('/api/room',room);
    }

    deleteLaboratory(id: number){
        return this.http.delete(`/api/room/${id}`).map(res=>res.json());
    }

    getLaboratory(id: number) {
        return this.http.get(`/api/room/${id}`).map(res=>res.json())
    }
}