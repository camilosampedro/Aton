/**
 * Created by camilosampedro on 9/01/17.
 */
export class ComputerState {
    computerIp: string;
    registeredDate: number;
    state: number;
    operatingSystem: string;
    mac: string

    constructor(computerIp: string, registeredDate: number, state: number, operatingSystem: string, mac: string) {
        this.computerIp = computerIp;
        this.registeredDate = registeredDate;
        this.state = state;
        this.operatingSystem = operatingSystem;
        this.mac = mac;
    }


}