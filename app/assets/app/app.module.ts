import './rxjs-extensions';

import {NgModule}      from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {FormsModule}   from '@angular/forms';
import {HttpModule}    from '@angular/http';
import {NgSemanticModule} from 'ng-semantic';
import {AppRoutingModule} from './app-routing.module';

import { CookieService } from 'angular2-cookie/services/cookies.service';

import {AppComponent}         from './app.component';
import {LoginComponent} from './login/login.component';
import {APP_BASE_HREF}          from '@angular/common';
import {MenuComponent} from "./menu/menu.component";
import {ComputerComponent} from "./computer/computer.component";
import {RoomComponent} from "./room/room.component";
import {LaboratoryComponent} from "./laboratory/laboratory.component";
import {LoginService} from "./login/login.service";
import {HomeComponent} from "./home/home.component";
import {LaboratoryService} from "./laboratory/laboratory.service";
import {AddLaboratoryComponent} from "./laboratory/add/add-laboratory.component";
import {RoomService} from "./room/room.service";
import {AddRoomComponent} from "./room/add/add-room.component";
import {AddComputerComponent} from "./computer/add/add-computer.component";
import {ComputerService} from "./computer/computer.service";
import {MessageComponent} from "./message/message.component";
import {MessageService} from "./message/message.service";

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        AppRoutingModule,
        NgSemanticModule
    ],
    declarations: [
        AppComponent,
        LoginComponent,
        MenuComponent,
        ComputerComponent,
        RoomComponent,
        LaboratoryComponent,
        HomeComponent,
        AddLaboratoryComponent,
        AddRoomComponent,
        AddComputerComponent,
        MessageComponent
    ],
    providers: [{provide: APP_BASE_HREF, useValue: '/'},
        LoginService,
        CookieService,
        LaboratoryService,
        RoomService,
        ComputerService,
        MessageService
    ],
    bootstrap: [AppComponent],
})
export class AppModule {
}
