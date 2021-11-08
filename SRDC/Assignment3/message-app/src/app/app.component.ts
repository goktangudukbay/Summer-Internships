import { Component } from '@angular/core';
import { ApiService } from './service/api.service';
import { Router} from '@angular/router';
import {ChangeDetectorRef} from '@angular/core'
import { User } from './model/User';
import { HeroService } from './hero.service';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})



export class AppComponent {
  title = 'message-app';
  adminInfo;

  
  constructor(
    public apiService: ApiService, public router: Router,private cdRef:ChangeDetectorRef, public nav: HeroService
  ) { 
    if(sessionStorage.userInfo)
     this.adminInfo = JSON.parse(sessionStorage.userInfo).isAdmin
    console.log(this.router.url);
    console.log(this.adminInfo);
    //console.log(JSON.parse(sessionStorage.userInfo));


    }

  
  ngOnInit(): void {
    
  }

  ngAfterViewInit(){
    
  }

  logout(){
    //sessionStorage.clear();
    console.log("basti");
    console.log(sessionStorage.getItem("loginTime"));
    console.log("sk");
    var dt = new Date();
    var accessObject = {
      username: JSON.parse(sessionStorage.userInfo).username,
      login_Time: sessionStorage.getItem("loginTime"),
      logout_Time: dt.toString(),
      ip: sessionStorage.getItem("ip"),
     browser: sessionStorage.browserName
    }
    
    this.apiService.newAccess(accessObject, JSON.parse(sessionStorage.userInfo).authToken).subscribe(
      (res) => {
          console.log(res);
      }, (error) => {
        console.log("dssads" + error);
      });
      sessionStorage.clear();
  }

}


