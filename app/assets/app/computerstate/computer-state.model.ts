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

    static stateLabel(state: number) {
        if (state == 1) {
            return "Connected";
        } else if (state == 2) {
            return "Not connected"
        } else if (state == 3) {
            return "Auth failed"
        } else {
            return "Unknown error"
        }
    }
}