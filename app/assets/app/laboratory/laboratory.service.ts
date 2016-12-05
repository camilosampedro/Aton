/**
 * Created by camilosampedro on 4/12/16.
 */
import { Injectable }    from '@angular/core';
import { Headers, Http } from '@angular/http';

import 'rxjs/add/operator/toPromise';

import { Laboratory } from './laboratory';

@Injectable()
export class LaboratoryService {

    private headers = new Headers({'Content-Type': 'application/json'});
    private heroesUrl = 'api/heroes';

    constructor(private http: Http) { }

    public getLaboratories(): Promise<Laboratory[]> {
        return this.http.get(this.heroesUrl)
            .toPromise()
            .then(response => response.json().data as Laboratory[])
            .catch(this.handleError);
    }

    public getLaboratory(id: number): Promise<Laboratory> {
        return this.getLaboratories()
            .then(heroes => heroes.find(hero => hero.id === id));
    }

    public deleteLaboratory(id: number): Promise<void> {
        const url = `${this.heroesUrl}/${id}`;
        return this.http.delete(url, {headers: this.headers})
            .toPromise()
            .then(() => null)
            .catch(this.handleError);
    }

    public create(name: string): Promise<Laboratory> {
        return this.http
            .post(this.heroesUrl, JSON.stringify({name: name}), {headers: this.headers})
            .toPromise()
            .then(res => res.json().data)
            .catch(this.handleError);
    }

    public update(hero: Laboratory): Promise<Laboratory> {
        const url = `${this.heroesUrl}/${hero.id}`;
        return this.http
            .put(url, JSON.stringify(hero), {headers: this.headers})
            .toPromise()
            .then(() => hero)
            .catch(this.handleError);
    }

    private handleError(error: any): Promise<any> {
//    console.error('An error occurred', error); // for demo purposes only
        return Promise.reject(error.message || error);
    }
}

/*
 Copyright 2016 Google Inc. All Rights Reserved.
 Use of this source code is governed by an MIT-style license that
 can be found in the LICENSE file at http://angular.io/license
 */
