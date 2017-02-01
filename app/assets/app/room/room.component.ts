/**
 * Created by camilosampedro on 31/12/16.
 */
/**
 * Created by camilosampedro on 30/12/16.
 */
import {Component, HostBinding, Input, Output, EventEmitter}          from '@angular/core';
import {Room} from "./room.model";
import {Computer} from "../computer/computer.model";
import {ComputerState} from "../computerstate/computer-state.model";
import {ConnectedUser} from "../computerstate/connected-user.model";
import {LoginService} from "../login/login.service";
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'aton-room',
    templateUrl: 'assets/app/room/room.component.html',
    styleUrls: ['assets/app/room/room.component.css'],
})
export class RoomComponent {
    @Input() laboratoryId: number;
    @Input() room: Room;
    @Input() computers: [Computer, ComputerState, ConnectedUser[]][];

    @Output() computerSelected: EventEmitter<[boolean,Computer]> = new EventEmitter();
    @Output() editComputerClicked: EventEmitter<Computer> = new EventEmitter();
    @Output() singleSendMessageClicked: EventEmitter<Computer> = new EventEmitter();
    @Output() singleSendOrderClicked: EventEmitter<Computer> = new EventEmitter();
    @Output() sendOnSelectedEvent: EventEmitter<boolean> = new EventEmitter();
    @Output() deleteSelectedEvent: EventEmitter<boolean> = new EventEmitter();
    @Output() addANewComputerEvent: EventEmitter<number> = new EventEmitter();

    //@HostBinding('class.ui') uiClass: boolean = true;
    //@HostBinding('class.center') centerClass: boolean = true;
    //@HostBinding('class.aligned') alignedClass: boolean = true;
    //@HostBinding('class.segment') segmentClass: boolean = true;

    someMessagesClicked(computer: Computer) {
        this.singleSendMessageClicked.emit(computer);
    }

    someSendOrderClicked(computer: Computer) {
        this.singleSendOrderClicked.emit(computer);
    }

    selectComputer(event: [boolean, Computer]) {
        console.log(event);
        this.computerSelected.emit(event);
    }

    sendOnSelected() {
        this.sendOnSelectedEvent.emit(true);
    }

    deleteSelected() {
        this.deleteSelectedEvent.emit(true);
    }

    isLoggedIn() {
        return LoginService.isLoggedIn();
    }

    addANewComputer() {
        this.addANewComputerEvent.emit(this.room.id)
    }

    editComputer(computer: Computer){
        this.editComputerClicked.emit(computer);
    }


}