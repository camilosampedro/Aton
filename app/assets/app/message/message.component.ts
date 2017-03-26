/**
 * Created by camilosampedro on 9/01/17.
 */
import { Component, ViewChild }          from '@angular/core';
import {Computer} from "../computer/computer.model";
import {MessageService} from "./message.service";
//import {Validators, FormBuilder, FormGroup} from '@angular/forms';

@Component({
//  moduleId: module.id,
    selector: 'aton-message',
    templateUrl: 'assets/app/message/message.component.html',
    styleUrls: ['assets/app/message/message.component.css'],
})
export class MessageComponent{
    computers: string[];
    text: string;
    chosenComputers: string[];

    @ViewChild("sendMessageModal") sendMessageModal: any;

    constructor(private messageService: MessageService){}

    showForSelected(computers: Computer[]){
        this.computers = [];
        for(let computer of computers) {
            this.computers.push(computer.ip)
        }

        this.sendMessageModal.show();
    }

    /**
     * TODO: Implement this method
     * @param computer
     */
    showForComputer(computer: Computer) {

    }

    submit() {
        this.messageService.sendMessage([this.chosenComputers, this.text]).subscribe(res=>console.log(res),err=> console.error(err))
    }
}