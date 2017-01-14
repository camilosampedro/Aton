/**
 * Created by camilosampedro on 11/12/16.
 */
import { Component, OnInit }          from '@angular/core';
import {Router} from '@angular/router';
import {Laboratory} from "../laboratory/laboratory.model";
import {LaboratoryService} from "../laboratory/laboratory.service";
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';
import {LoginService} from "../login/login.service";
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'aton-home',
    templateUrl: 'assets/app/home/home.component.html',
    styleUrls: ['assets/app/home/home.component.css'],
})
export class HomeComponent implements OnInit {
    laboratories: Laboratory[] = [];

    constructor(private router: Router,
                private laboratoryService: LaboratoryService,
                private loginService: LoginService) {}

    ngOnInit(): void {
        this.laboratoryService.getAll().subscribe(laboratories=>{
            console.log(laboratories);
            this.laboratories = laboratories;
        },
        (err: any)=>{
            console.error("Error retrieving laboratories");
            console.log(err);
        });

        if(this.isLoggedIn()){
            console.log("It is logged in")
        }
    }

    deleteLaboratory(id: number){
        this.laboratoryService.deleteLaboratory(id).subscribe(result=>{
            console.log(result);
        },err=>console.error(err));
    }

    isLoggedIn() {
        return LoginService.isLoggedIn();
    }
}