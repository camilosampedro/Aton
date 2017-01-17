/**
 * Created by camilosampedro on 31/12/16.
 */
import { Component, HostBinding, OnInit, Input, Output, EventEmitter }          from '@angular/core';
import {Computer} from "./computer.model";
import {ComputerState} from "../computerstate/computer-state.model";
import {ConnectedUser} from "../computerstate/connected-user.model";
import {LoginService} from "../login/login.service";
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'aton-computer',
    templateUrl: 'assets/app/computer/computer.component.html',
    styleUrls: ['assets/app/computer/computer.component.css'],
})
export class ComputerComponent extends OnInit {
    @Input() computer: Computer;
    @Input() state: ComputerState;
    @Input() connectedUsers: ConnectedUser[];
    isSelected: boolean = false;

    @Output() messageClicked: EventEmitter<Computer> = new EventEmitter();
    @Output() computerSelected: EventEmitter<[boolean,Computer]> = new EventEmitter();
    @Output() editComputerClicked: EventEmitter<Computer> = new EventEmitter();
    @HostBinding('class.column') column: boolean = true;

    ngOnInit(){}

    sendMessageClick() {
        this.messageClicked.emit(this.computer)
    }

    toggleSelection() {
        this.isSelected = !this.isSelected;
        this.computerSelected.emit([this.isSelected,this.computer]);
    }

    stateLabel() {
        switch (this.state.state) {
            case 1:
                return "Connected";
            case 2:
                return "Not connected";
            case 3:
                return "Auth failed";
            default:
                return "Unknown error";
        }
    }

    isLoggedIn() {
        return LoginService.isLoggedIn();
    }

    editComputerClick(){
        this.editComputerClicked.emit(this.computer)
    }
}