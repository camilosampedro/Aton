/**
 * Created by camilosampedro on 2/01/17.
 */
import { Component, ViewChild }          from '@angular/core';
import {LaboratoryService} from "../laboratory.service";
import {Router} from '@angular/router';
import {Laboratory} from "../laboratory.model";
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'aton-add-laboratory',
    templateUrl: 'assets/app/laboratory/add/add-laboratory.component.html',
    styleUrls: ['assets/app/laboratory/add/add-laboratory.component.css'],
})
export class AddLaboratoryComponent{
    @ViewChild('newLaboratoryModal') newLaboratoryModal: any;
    name: string = "";
    location: string = "";
    administration: string = "";

    constructor(private laboratoryService:LaboratoryService, private router: Router){}

    submit(){
        console.log("submitting");
        console.log(this.name);
        let laboratory: Laboratory = new Laboratory(0,this.name, this.location, this.administration);
        console.log(laboratory);
        let result = this.laboratoryService.addNew(laboratory).subscribe(
            result =>{
                console.log(result);
                this.newLaboratoryModal.hide()
            },
            err => {
                console.error(err);
                alert(err)
            }
        );
    }
}