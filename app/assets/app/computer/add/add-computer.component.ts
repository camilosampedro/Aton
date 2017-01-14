/**
 * Created by camilosampedro on 2/01/17.
 */
import { Component, ViewChild, Input, OnInit, HostBinding }          from '@angular/core';
import {Router} from '@angular/router';
import {ComputerService} from "../computer.service";
import {Computer} from "../computer.model";
import {ComputerFormPanelComponent} from "../formpanel/computer-form-panel.component";
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'aton-add-computer',
    templateUrl: 'assets/app/computer/add/add-computer.component.html',
    styleUrls: ['assets/app/computer/add/add-computer.component.css'],
})
export class AddComputerComponent implements OnInit{
    @ViewChild('computerFormPanel') computerFormPanel: ComputerFormPanelComponent;
    @Input() roomID: number;
    @Input() isDropdown: boolean;
    @HostBinding('class.item') uiClass: boolean = this.isDropdown;
    ip: string;
    name: string;
    SSHUser: string;
    SSHPassword: string;
    description: string;

    ngOnInit(){
        this.uiClass = this.isDropdown;
    }


    constructor(private computerService: ComputerService, private router: Router){}

    submit(computer: Computer){
        let result = this.computerService.addNew(computer).subscribe(
            result =>{
                console.log(result);
                this.computerFormPanel.hide()
            },
            err => {
                console.error(err);
                alert(err)
            }
        );
    }

    showModal() {
        this.computerFormPanel.show();
    }
}