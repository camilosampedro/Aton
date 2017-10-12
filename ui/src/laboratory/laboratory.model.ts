/**
 * Created by camilosampedro on 2/01/17.
 */
export class Laboratory {
    id: number;
    name: string;
    location: string;
    administration: string;

    constructor(id: number, name: string, location: string, administration: string) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.administration = administration;
    }
}