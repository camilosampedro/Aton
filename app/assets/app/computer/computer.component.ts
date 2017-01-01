/**
 * Created by camilosampedro on 31/12/16.
 */
/**
 * Created by camilosampedro on 30/12/16.
 */
import { Component, HostBinding, OnInit, Output, EventEmitter }          from '@angular/core';
import {Computer} from "./computer.model";
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'aton-computer',
    templateUrl: 'assets/app/computer/computer.component.html',
    styleUrls: ['assets/app/computer/computer.component.css'],
})
export class ComputerComponent extends OnInit {
    computer: Computer;
    @Output() messageClicked = new EventEmitter();
    @HostBinding('class.column') column: boolean = true;

    ngOnInit(){
        this.computer = new Computer("10.10.0.1","Computer X", "", "", "",1);
    }

    sendMessageClick() {
        console.log("Clicked");
        this.messageClicked.emit({
            value: this.computer
        })
    }
}