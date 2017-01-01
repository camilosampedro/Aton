/**
 * Created by camilosampedro on 31/12/16.
 */
/**
 * Created by camilosampedro on 30/12/16.
 */
import { Component, HostBinding, Input }          from '@angular/core';
import {Room} from "./room.model";
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'aton-room',
    templateUrl: 'assets/app/room/room.component.html',
    styleUrls: ['assets/app/room/room.component.css'],
})
export class RoomComponent{
    @Input() room: Room;

    @HostBinding('class.ui') uiClass: boolean = true;
    @HostBinding('class.center') centerClass: boolean = true;
    @HostBinding('class.aligned') alignedClass: boolean = true;
    @HostBinding('class.segment') segmentClass: boolean = true;

    static someMessagesClicked(event: Event) {
        console.log(event);
    }
}