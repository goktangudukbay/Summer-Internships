import { Component, OnInit, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from './../../service/api.service';
import { FormGroup, FormBuilder, Validators } from "@angular/forms";
import { HttpClient  } from '@angular/common/http';  
import { HeroService } from './../../hero.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  submitted = false;
  loginForm: FormGroup;

  constructor(
    public fb: FormBuilder,
    public router: Router,
    public ngZone: NgZone,
    public apiService: ApiService,
    public http: HttpClient,
    public nav: HeroService
  ) { 
    this.mainForm();
  }

  ngOnInit(): void {
  }

  mainForm() {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    })
  }

  // Getter to access form control
  get myForm(){
    return this.loginForm.controls;
  }

  public getIPAddress()  
  {  
    return this.http.get("http://api.ipify.org/?format=json");  
  }  

  public getBrowserName() {
    const agent = window.navigator.userAgent.toLowerCase()
    switch (true) {
      case agent.indexOf('edge') > -1:
        return 'edge';
      case agent.indexOf('opr') > -1 && !!(<any>window).opr:
        return 'opera';
      case agent.indexOf('chrome') > -1 && !!(<any>window).chrome:
        return 'chrome';
      case agent.indexOf('trident') > -1:
        return 'ie';
      case agent.indexOf('firefox') > -1:
        return 'firefox';
      case agent.indexOf('safari') > -1:
        return 'safari';
      default:
        return 'other';
    }
}

  onSubmit() {
    this.submitted = true;
    if (!this.loginForm.valid) {
      return false;
    } else {
      this.apiService.login(this.loginForm.value).subscribe(
        (res) => {
          console.log(res);
          sessionStorage.setItem("userInfo", JSON.stringify(res));
          console.log(sessionStorage.userInfo);
          console.log(JSON.parse(sessionStorage.userInfo).isAdmin);
          if(JSON.parse(sessionStorage.userInfo).isAdmin)
            this.nav.show();
          else
            this.nav.hide();
          this.getIPAddress().subscribe((ipResponse:any) =>{
            console.log(ipResponse.ip);
            sessionStorage.setItem("ip", ipResponse.ip);
          });
          console.log(sessionStorage.getItem("ip"));

          sessionStorage.setItem("browserName", (this.getBrowserName()));
          var dt = new Date();
          console.log(dt);
          sessionStorage.setItem("loginTime", dt.toString());
          console.log(sessionStorage.getItem("browserName"));
          this.ngZone.run(() => this.router.navigateByUrl('menu'))
        }, (error) => {
          console.log("dssads" + error);
        });
    }
  }

}
