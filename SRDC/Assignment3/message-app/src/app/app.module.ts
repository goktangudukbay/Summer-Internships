import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule} from '@angular/material/input';
import { MatTableModule} from '@angular/material/table';
import { MatDatepickerModule} from '@angular/material/datepicker';
import { MatNativeDateModule} from '@angular/material/core';



import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { MenuComponent } from './components/menu/menu.component';
import { ReadMessageComponent } from './components/read-message/read-message.component';
import { SendMessageComponent } from './components/send-message/send-message.component';
import { AddUpdateComponent } from './components/add-update/add-update.component';
import { ListComponent } from './components/list/list.component';
import { AccessLogComponent } from './components/access-log/access-log.component';
import { RemoveComponent } from './components/remove/remove.component';
import {MatSort, MatSortModule} from '@angular/material/sort';


import { HttpClientModule } from '@angular/common/http';

import { ApiService } from './service/api.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';



@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    MenuComponent,
    ReadMessageComponent,
    SendMessageComponent,
    AddUpdateComponent,
    ListComponent,
    AccessLogComponent,
    RemoveComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatInputModule,
    MatTableModule,
    BrowserAnimationsModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSortModule
  ],
  providers: [ApiService],
  bootstrap: [AppComponent]
})
export class AppModule { }
