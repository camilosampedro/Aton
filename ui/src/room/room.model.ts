/**
 * Created by camilosampedro on 31/12/16.
 */
export class Room {
    id: number;
    name: string;
    laboratoryID: number;

    constructor(id: number, name: string, laboratoryID: number){
        this.id = id;
        this.name = name;
        this.laboratoryID = laboratoryID;
    }
}