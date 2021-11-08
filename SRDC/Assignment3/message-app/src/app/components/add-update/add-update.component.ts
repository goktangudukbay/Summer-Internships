import { Component, OnInit, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from './../../service/api.service';
import { FormGroup, FormBuilder, Validators } from "@angular/forms";
import { ActivatedRoute} from "@angular/router";
import { User} from './../../model/User';

@Component({
  selector: 'app-add-update',
  templateUrl: './add-update.component.html',
  styleUrls: ['./add-update.component.css']
})
export class AddUpdateComponent implements OnInit {
  userData: User[];
  submitted = false;
  editForm: FormGroup;


  constructor(
    public fb: FormBuilder,
    public router: Router,
    private actRoute: ActivatedRoute,
    public ngZone: NgZone,
    public apiService: ApiService
    
  ) { 
    this.mainForm();
    console.log(this.router.url);
    

    if(this.router.url.localeCompare('/create-user') != 0){
      let username = this.actRoute.snapshot.paramMap.get('username');
      console.log("daskjasdjkdsajk" +username);
      this.apiService.getUser(JSON.parse(sessionStorage.userInfo).authToken, username).subscribe(
        (data) => { 
          var gen;
          var adm;
          if(data['gender'].substring(0, 1).localeCompare("M") == 0)
            gen = "Male";
          else
            gen = "Female";

          if(data['isAdmin'])
            adm = "true";
          else
            adm = "false";
          this.editForm.setValue({
            username: data['username'],
            firstname: data['firstname'],
            lastname: data['lastname'],
            birthdate: data['birthdate'],
            gender: gen,
            email: data['email'],
            password: data['password'],
            isAdmin: adm
          });
        }
      );

    }

   }

  ngOnInit(): void {
  }
  mainForm() {
    this.editForm = this.fb.group({
      username: ['', [Validators.required]],
      firstname: ['', [Validators.required]],
      lastname: ['', [Validators.required]],
      birthdate: [new Date()],
      email: ['', [Validators.required]],
      password: ['', [Validators.required]],
      gender: ['', [Validators.required]], // this line missing in your code
      isAdmin: ['', [Validators.required]],
    })
  }

 

      // Getter to access form control
  get myForm(){
     return this.editForm.controls;
  }
    
  onSubmit() {
    this.submitted = true;
    if (!this.editForm.valid) {
      return false;
    } 
    else {
      var adm = false;
      if(this.editForm.value.isAdmin.localeCompare('true') == 0)
        adm = true
      var user = 
      {
          "username": this.editForm.value.username,
          "firstname": this.editForm.value.firstname,
          "lastname": this.editForm.value.lastname,
          "birthdate": new Date(this.editForm.value.birthdate),
          "email": this.editForm.value.email,
          "gender": this.editForm.value.gender,
          "password": this.editForm.value.password,
          "isAdmin": adm,
      }; 

      if(this.router.url.includes('/edit-user')){
        this.apiService.updateUser(user, JSON.parse(sessionStorage.userInfo).authToken).subscribe(
          (res) => {
            console.log(res);
            this.ngZone.run(() => this.router.navigateByUrl('menu'))
          }, (error) => {
            console.log("dssads" + error);
          });
      }
      else{
        console.log(this.editForm.value.birthdate); 
        this.apiService.createUser(user, JSON.parse(sessionStorage.userInfo).authToken).subscribe(
          (res) => {
            console.log(res);
            this.ngZone.run(() => this.router.navigateByUrl('menu'))
          }, (error) => {
            console.log("dssads" + error);
          });
      }
      
    }
  }

}





  


