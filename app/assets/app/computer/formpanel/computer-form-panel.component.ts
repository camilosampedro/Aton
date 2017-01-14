/**
 * Created by camilosampedro on 2/01/17.
 */
import { Component, ViewChild, Input, OnInit, HostBinding, EventEmitter, Output }          from '@angular/core';
import {Router} from '@angular/router';
import {ComputerService} from "../computer.service";
import {Computer} from "../computer.model";
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'computer-form-panel',
    templateUrl: 'assets/app/computer/formpanel/computer-form-panel.component.html',
    styleUrls: ['assets/app/computer/formpanel/computer-form-panel.component.css'],
})
export class ComputerFormPanelComponent implements OnInit{
    @ViewChild('computerFormModal') computerFormModal: any;
    @Input() roomID: number;
    @Input() title: string;
    @Output() onSubmit: EventEmitter<Computer> = new EventEmitter();
    ip: string;
    name: string;
    SSHUser: string;
    SSHPassword: string;
    description: string;

    ngOnInit() {}


    constructor(private computerService: ComputerService, private router: Router){}

    submit(){
        let computer: Computer = new Computer(this.ip, this.name, this.SSHUser, this.SSHPassword, this.description, this.roomID);
        this.onSubmit.emit(computer);
    }

    hide(){
        this.computerFormModal.hide();
    }

    show(){
        this.computerFormModal.show({inverted: true});
    }
}