/**
 * Created by camilosampedro on 9/01/17.
 */
import {Component, ViewChild}          from '@angular/core';
import {Computer} from "../computer/computer.model";
import {MessageService} from "./message.service";

/**
 * A dialog used to send a message to different computers at the same time. There's an alternative under showForComputer
 * for a single computer.
 */
@Component({
    selector: 'aton-message',
    templateUrl: 'assets/app/message/message.component.html',
    styleUrls: ['assets/app/message/message.component.css'],
})
export class MessageComponent {
    computers: string[];
    text: string;
    chosenComputers: string[];

    @ViewChild("sendMessageModal") sendMessageModal: any;

    constructor(private messageService: MessageService) {
    }

    /**
     * Show a "send message to computers" dialog that allows to select the computers that will receive the message.
     * @param computers Computers available to be chosen.
     */
    showForSelected(computers: Computer[]) {
        this.computers = [];
        for (let computer of computers) {
            this.computers.push(computer.ip);
        }

        this.sendMessageModal.show();
    }

    /**
     * TODO: Implement this method
     * @param computer Computer to be used in this modal.
     */
    showForComputer(computer: Computer) {

    }

    /**
     * Perform the message sending
     */
    submit() {
        this.messageService.sendMessage([this.chosenComputers, this.text]).subscribe(res =>
            console.log(res),
            err => console.error(err)
        );
    }
}