import {NgModule}             from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from "./home/home.component";
import {LaboratoryComponent} from "./laboratory/laboratory.component";

const routes: Routes = [
    {path: 'home', component: HomeComponent, pathMatch: 'full'},
    {path: '', redirectTo: '/home', pathMatch: 'full'},
    {path: 'laboratory/:id', component: LaboratoryComponent}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule],
})
export class AppRoutingModule {
}