/**
 * Created by camilosampedro on 2/01/17.
 */
import { Component, ViewChild, Input, OnInit, HostBinding, EventEmitter, Output }          from '@angular/core';
import {Router} from '@angular/router';
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'aton-dialog',
    templateUrl: 'assets/app/dialog/dialog.component.html',
    styleUrls: ['assets/app/dialog/dialog.component.css'],
})
export class DialogComponent implements OnInit{
    @ViewChild('dialogModal') dialogModal: any;
    title: string = "Add a new computer";
    message: string = "";

    ngOnInit() {}


    constructor(){}

    show(title: string, message: string){
        this.title = title;
        this.message = message;
        this.dialogModal.show({inverted: true});
    }
}