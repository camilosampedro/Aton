import '../rxjs-extensions';

import {NgModule}      from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {FormsModule}   from '@angular/forms';
import {HttpModule}    from '@angular/http';
import {NgSemanticModule} from 'ng-semantic';
import {AppRoutingModule} from './app-routing.module';
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
import {ComputerService} from "./computer/computer.service";
import {MessageComponent} from "./message/message.component";
import {MessageService} from "./message/message.service";
import {ComputerFormPanelComponent} from "./computer/formpanel/computer-form-panel.component";
import {DialogComponent} from "./dialog/dialog.component";
import {SSHOrderService} from "./orderform/ssh-order.service";
import {OrderFormComponent} from "./orderform/order-form.component";


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
        MessageComponent,
        ComputerFormPanelComponent,
        DialogComponent,
        OrderFormComponent
    ],
    providers: [{provide: APP_BASE_HREF, useValue: '/'},
        LoginService,
        LaboratoryService,
        RoomService,
        ComputerService,
        MessageService,
        SSHOrderService
    ],
    bootstrap: [AppComponent],
})
export class AppModule {
}
