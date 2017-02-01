/**
 * Created by camilosampedro on 2/01/17.
 */
import { Component, ViewChild, Input, OnInit, HostBinding, EventEmitter, Output }          from '@angular/core';
import {Router} from '@angular/router';
import {ComputerService} from "../computer.service";
import {Computer} from "../computer.model";
import {LoginService} from "../../login/login.service";
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'aton-computer-form-panel',
    templateUrl: 'assets/app/computer/formpanel/computer-form-panel.component.html',
    styleUrls: ['assets/app/computer/formpanel/computer-form-panel.component.css'],
})
export class ComputerFormPanelComponent implements OnInit{
    @ViewChild('computerFormModal') computerFormModal: any;
    @Input() roomID: number;
    @Output() onAlert: EventEmitter<[string, string]> = new EventEmitter();
    title: string = "Add a new computer";
    isEdit: boolean = false;
    label = "Add";
    ip: string;
    name: string;
    SSHUser: string;
    SSHPassword: string;
    description: string;
    _computer: Computer;

    ngOnInit() {}

    public get computer(): Computer {
        return this._computer
    }

    public set computer(computer: Computer){
        console.log(computer);
        this.ip = computer.ip;
        this.name = computer.name;
        this.SSHUser = computer.SSHUser;
        this.SSHPassword = computer.SSHPassword;
        this.description = computer.description;
        this._computer = computer;
    }


    constructor(private computerService: ComputerService, private router: Router){}

    submit(){
        this._computer = new Computer();
        this._computer.ip = this.ip;
        this._computer.name = this.name;
        this._computer.description = this.description;
        this._computer.roomID = this.roomID;
        this._computer.SSHUser = this.SSHUser;
        this._computer.SSHPassword = this.SSHPassword;
        if(this.isEdit){
            this.computerService.editComputer(this._computer).subscribe(
                result =>{
                    console.log(result);
                    this.computerFormModal.hide()
                },
                err => {
                    console.error("Error editing computer");
                    console.error(err);
                    this.showError(err.result);
                }
            );
        } else {
            this.computerService.addNew(this._computer).subscribe(
                result =>{
                    console.log(result);
                    this.computerFormModal.hide()
                },
                err => {
                    console.error("Error adding computer");
                    console.error(err);
                    this.showError(err);
                    alert(err)
                }
            );
        }
    }

    hide(){
        this._computer = new Computer();
        this.ip = "";
        this.SSHUser = "";
        this.SSHPassword = "";
        this.roomID = 0;
        this.description = "";
        this.name = "";
        this.computerFormModal.hide();
    }

    show(isEdit: boolean, roomID: number){
        this.roomID = roomID;
        this.isEdit = isEdit;
        if(isEdit){
            this.title = "Edit computer";
            this.label = "Update";
        } else {
            this.title = "Add a new computer";
            this.label = "Add";
        }
        this.computerFormModal.show({inverted: true});
    }

    showError(err: any){
        this.onAlert.emit(["Error", err])
    }
}