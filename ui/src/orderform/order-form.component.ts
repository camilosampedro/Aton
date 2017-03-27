/**
 * Created by camilosampedro on 2/01/17.
 */
import { Component, ViewChild, Input, OnInit, HostBinding, EventEmitter, Output }          from '@angular/core';
import {Router} from '@angular/router';
import {SSHOrder, MinimalSSHOrder} from "./ssh-order.model";
import {SSHOrderService} from "./ssh-order.service";
import {Computer} from "../computer/computer.model";
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'aton-order-form-panel',
    templateUrl: './order-form.component.html',
    styleUrls: ['./order-form.component.css'],
})
export class OrderFormComponent implements OnInit{

    @ViewChild('computerFormModal') computerFormModal: any;
    @Input() roomID: number;
    @Output() onAlert: EventEmitter<[string, string]> = new EventEmitter();
    title: string = "Add a new computer";
    isEdit: boolean = false;
    superUser: boolean;
    interrupt: boolean;
    command: string;
    private _computer: Computer;
    private _sshOrder: MinimalSSHOrder;

    ngOnInit() {}

    public get sshOrder(): MinimalSSHOrder {
        return this._sshOrder
    }

    public set sshOrder(sshOrder: MinimalSSHOrder){
        this.superUser = sshOrder.superUser;
        this.interrupt = sshOrder.interrupt;
        this.command = sshOrder.command;
        this._sshOrder = sshOrder;
    }

    get computer(): Computer {
        return this._computer;
    }

    set computer(value: Computer) {
        this._computer = value;
    }


    constructor(private sshOrderService: SSHOrderService, private router: Router){}

    submit(){
        this._sshOrder = new MinimalSSHOrder(this.superUser, this.interrupt, this.command);

            this.sshOrderService.sendOrder(this._sshOrder, this.computer).subscribe(
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

    hide(){
        this._sshOrder = new MinimalSSHOrder(false, false, "");
        this.superUser = false;
        this.command = "";
        this.interrupt = false;
        this.computerFormModal.hide();
    }

    show(computer: Computer){
        this.computer = computer;
        this.title = "Send an SSH order";
        this.computerFormModal.show({inverted: true});
    }

    showError(err: any){
        this.onAlert.emit(["Error", err])
    }
}