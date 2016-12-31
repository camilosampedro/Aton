/**
 * Created by camilosampedro on 31/12/16.
 */
/**
 * Created by camilosampedro on 30/12/16.
 */
import { Component, HostBinding, OnInit }          from '@angular/core';
import {Computer} from "./computer.model";
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'aton-computer',
    templateUrl: 'assets/app/computer/computer.component.html',
    styleUrls: ['assets/app/computer/computer.component.css'],
})
export class ComputerComponent{
    computer: Computer;


    @HostBinding('class.column') column: boolean = true;

    ngOnInit(){
        this.computer = new Computer("10.10.0.1","Computer X", "", "", "",1);
    }
}