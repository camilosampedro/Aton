/**
 * Created by camilosampedro on 1/01/17.
 */
import 'rxjs/add/operator/switchMap';
import { Component, OnInit }          from '@angular/core';
import {Room} from "../room/room.model";
import {ActivatedRoute, Params} from '@angular/router';
import {LaboratoryService} from "./laboratory.service";
import {Laboratory} from "./laboratory.model";
import {Computer} from "../computer/computer.model";
import {ComputerState} from "../computerstate/computer-state.model";
import {ConnectedUser} from "../computerstate/connected-user.model";
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'aton-laboratory',
    templateUrl: 'assets/app/laboratory/laboratory.component.html',
    styleUrls: ['assets/app/laboratory/laboratory.component.css'],
})
export class LaboratoryComponent implements OnInit{
    rooms: [Room, [Computer, ComputerState, ConnectedUser[]][]][] = [];
    laboratory: Laboratory = new Laboratory(0,"Loading...","","");

    constructor(
        private route: ActivatedRoute,
        private laboratoryService: LaboratoryService
    ) {}

    ngOnInit(): void {
        this.route.params
            .switchMap((params: Params) => this.laboratoryService.getLaboratory(+params['id']))
            .subscribe(laboratoryWithRooms => {
                console.log(laboratoryWithRooms);
                this.laboratory = laboratoryWithRooms.laboratory;
                this.rooms = [];
                for(let r of laboratoryWithRooms.rooms){
                    console.log("room");
                    console.log(r.room);
                    console.log("computers");
                    console.log(r.computers);
                    let computers: [Computer, ComputerState, ConnectedUser[]][] = [];
                    for( let c of r.computers) {
                        console.log("inside element");
                        console.log(c);
                        computers.push([c.computer, c.state.state, c.state.users]);
                    }
                    this.rooms.push([r.room, computers]);
                }
                console.log(this.rooms);
            });
    }

}