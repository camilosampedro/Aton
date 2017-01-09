/**
 * Created by camilosampedro on 2/01/17.
 */
import { Component, ViewChild, Input }          from '@angular/core';
import {Router} from '@angular/router';
import {ComputerService} from "../computer.service";
import {Computer} from "../computer.model";
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'aton-add-computer',
    templateUrl: 'assets/app/computer/add/add-computer.component.html',
    styleUrls: ['assets/app/computer/add/add-computer.component.css'],
})
export class AddComputerComponent{
    @ViewChild('newComputerModal') newComputerModal: any;
    @Input() roomID: number;
    ip: string;
    name: string;
    SSHUser: string;
    SSHPassword: string;
    description: string;


    constructor(private computerService: ComputerService, private router: Router){}

    submit(){
        console.log("submitting");
        console.log(this.name);
        let computer: Computer = new Computer(this.ip, this.name, this.SSHUser, this.SSHPassword, this.description, this.roomID);
        console.log(computer);
        let result = this.computerService.addNew(computer).subscribe(
            result =>{
                console.log(result);
                this.newComputerModal.hide()
            },
            err => {
                console.error(err);
                alert(err)
            }
        );
    }
}