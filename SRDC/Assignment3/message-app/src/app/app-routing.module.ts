import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { LoginComponent } from './components/login/login.component';
import { MenuComponent } from './components/menu/menu.component';
import { ReadMessageComponent } from './components/read-message/read-message.component';
import { SendMessageComponent } from './components/send-message/send-message.component';
import { AddUpdateComponent } from './components/add-update/add-update.component';
import { ListComponent } from './components/list/list.component';
import { AccessLogComponent } from './components/access-log/access-log.component';
import { RemoveComponent } from './components/remove/remove.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: 'create-user', component: AddUpdateComponent },
  { path: 'edit-user/:username', component: AddUpdateComponent },
  { path: 'user-list', component: ListComponent },
  { path: 'remove/:username', component: RemoveComponent },
  { path: 'inbox', component: ReadMessageComponent },
  { path: 'outbox', component: ReadMessageComponent },
  { path: 'send-message', component: SendMessageComponent },
  { path: 'access-log', component: AccessLogComponent },
  { path: 'menu', component: MenuComponent},
  { path: 'login', component: LoginComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes), FormsModule, ReactiveFormsModule],
  exports: [RouterModule]
})
export class AppRoutingModule { }


