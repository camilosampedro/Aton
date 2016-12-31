/**
 * Created by camilosampedro on 31/12/16.
 */
export class Computer {
    ip: string;
    name: string;
    SSHUser: string;
    SSHPassword: string;
    description: string;
    roomID: number;


    constructor(ip: string, name: string, SSHUser: string, SSHPassword: string, description: string, roomID: number) {
        this.ip = ip;
        this.name = name;
        this.SSHUser = SSHUser;
        this.SSHPassword = SSHPassword;
        this.description = description;
        this.roomID = roomID;
    }
}