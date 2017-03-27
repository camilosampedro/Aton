/**
 * Created by camilosampedro on 29/01/17.
 */
export class MinimalSSHOrder {
    superUser: boolean;
    interrupt: boolean;
    command: string;

    constructor(superUser: boolean, interrupt: boolean, command: string) {
        this.superUser = superUser;
        this.interrupt = interrupt;
        this.command = command;
    }
}

export class SSHOrder extends MinimalSSHOrder {
    id: number;
    sentDatetime: Date;
    webUser: string;

    constructor(id: number, sentDatetime: Date, superUser: boolean, interrupt: boolean, command: string, webUser: string) {
        super(superUser, interrupt, command)
        this.id = id;
        this.sentDatetime = sentDatetime;
        this.webUser = webUser;
    }
}

