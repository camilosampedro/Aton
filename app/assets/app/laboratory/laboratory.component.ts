/**
 * Created by camilosampedro on 1/01/17.
 */
import 'rxjs/add/operator/switchMap';
import {Component, OnInit, ViewChild}          from '@angular/core';
import {Room} from "../room/room.model";
import {ActivatedRoute, Params} from '@angular/router';
import {LaboratoryService} from "./laboratory.service";
import {Laboratory} from "./laboratory.model";
import {Computer} from "../computer/computer.model";
import {ComputerState} from "../computerstate/computer-state.model";
import {ConnectedUser} from "../computerstate/connected-user.model";
import {LoginService} from "../login/login.service";
import {ComputerFormPanelComponent} from "../computer/formpanel/computer-form-panel.component";
import {DialogComponent} from "../dialog/dialog.component";
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'aton-laboratory',
    templateUrl: 'assets/app/laboratory/laboratory.component.html',
    styleUrls: ['assets/app/laboratory/laboratory.component.css'],
})
export class LaboratoryComponent implements OnInit {
    rooms: [Room, [Computer, ComputerState, ConnectedUser[]][]][] = [];
    laboratory: Laboratory = new Laboratory(0, "Loading...", "", "");
    selectedComputers: Computer[] = [];

    @ViewChild("messageModal") messageModal: any;
    @ViewChild('computerFormPanel') computerFormPanel: ComputerFormPanelComponent;
    @ViewChild('dialogModal') dialogModal: DialogComponent;


    constructor(private route: ActivatedRoute,
                private laboratoryService: LaboratoryService) {
    }

    ngOnInit(): void {
        this.route.params
            .switchMap((params: Params) => this.laboratoryService.getLaboratory(+params['id']))
            .subscribe(laboratoryWithRooms => {
                console.log(laboratoryWithRooms);
                this.laboratory = laboratoryWithRooms.laboratory;
                this.rooms = [];
                for (let r of laboratoryWithRooms.rooms) {
                    console.log("room");
                    console.log(r.room);
                    console.log("computers");
                    console.log(r.computers);
                    let computers: [Computer, ComputerState, ConnectedUser[]][] = [];
                    for (let c of r.computers) {
                        console.log("inside element");
                        console.log(c);
                        computers.push([c.computer, c.state.state, c.state.users]);
                    }
                    this.rooms.push([r.room, computers]);
                }
                console.log(this.rooms);
            });
    }

    selectComputer(event: [boolean, Computer]) {
        console.log(event);
        if (event[0]) {
            this.selectedComputers.push(event[1]);
        } else {
            let index = this.selectedComputers.indexOf(event[1], 0);
            if (index > -1) {
                this.selectedComputers.splice(index, 1);
            }
        }
    }

    showMessageForSelected() {
        this.messageModal.showForSelected(this.selectedComputers);
    }

    showMessageForComputer(computer: Computer) {
        this.messageModal.showForComputer(computer);
    }

    showOrderPanelForComputer(computer: Computer){
        console.log(computer);
    }

    addANewComputer(roomID: number) {
        this.computerFormPanel.show(false, roomID)
    }

    editComputer(computer: Computer) {
        this.computerFormPanel.computer = computer;
        this.computerFormPanel.show(true, computer.roomID);
    }


    isLoggedIn() {
        return LoginService.isLoggedIn();
    }

    showAlert(message: [string, string]) {
        this.dialogModal.show(message[0], message[1])
    }

}