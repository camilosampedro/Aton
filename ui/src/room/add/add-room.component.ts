/**
 * Created by camilosampedro on 2/01/17.
 */
import { Component, ViewChild, Input }          from '@angular/core';
import {Router} from '@angular/router';
import {Room} from "../room.model";
import {RoomService} from "../room.service";
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'aton-add-room',
    templateUrl: './add-room.component.html',
    styleUrls: ['./add-room.component.css'],
})
export class AddRoomComponent{
    @ViewChild('newRoomModal') newRoomModal: any;
    name: string = "";
    @Input() laboratoryID: number;

    constructor(private roomService: RoomService, private router: Router){}

    submit(){
        console.log("submitting");
        console.log(this.name);
        let room: Room = new Room(0, this.name, this.laboratoryID);
        console.log(room);
        let result = this.roomService.addNew(room).subscribe(
            result =>{
                console.log(result);
                this.newRoomModal.hide()
            },
            err => {
                console.error(err);
                alert(err)
            }
        );
    }
}