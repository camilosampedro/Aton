/**
 * Created by camilosampedro on 31/12/16.
 */
import { Component, HostBinding, OnInit, Input, Output, EventEmitter }          from '@angular/core';
import {Computer} from "./computer.model";
import {ComputerState} from "../computerstate/computer-state.model";
import {ConnectedUser} from "../computerstate/connected-user.model";
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

    @Output() messageClicked = new EventEmitter();
    @HostBinding('class.column') column: boolean = true;

    ngOnInit(){}

    sendMessageClick() {
        console.log("Clicked");
        this.messageClicked.emit({
            value: this.computer
        })
    }

    toggleSelection() {
        this.isSelected = !this.isSelected;
    }
}