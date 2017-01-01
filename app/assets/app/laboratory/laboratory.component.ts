/**
 * Created by camilosampedro on 1/01/17.
 */
/**
 * Created by camilosampedro on 30/12/16.
 */
import { Component, OnInit }          from '@angular/core';
import {Room} from "../room/room.model";
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'aton-laboratory',
    templateUrl: 'assets/app/laboratory/laboratory.component.html',
    styleUrls: ['assets/app/laboratory/laboratory.component.css'],
})
export class LaboratoryComponent extends OnInit{
    ngOnInit(): void {
        this.rooms = [new Room(1,"Room 1")]
    }
    rooms: Room[] = [];
}