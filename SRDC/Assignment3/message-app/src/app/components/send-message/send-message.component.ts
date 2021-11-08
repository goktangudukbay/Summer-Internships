import { Component, OnInit, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from './../../service/api.service';
import { FormGroup, FormBuilder, Validators } from "@angular/forms";


@Component({
  selector: 'app-send-message',
  templateUrl: './send-message.component.html',
  styleUrls: ['./send-message.component.css']
})

export class SendMessageComponent implements OnInit {

  submitted = false;
  sendMessageForm: FormGroup;

  constructor(
    public fb: FormBuilder,
    public router: Router,
    public ngZone: NgZone,
    public apiService: ApiService
  ) { 
    this.mainForm();
  }

  
  mainForm() {
    this.sendMessageForm = this.fb.group({
      receiver: ['', [Validators.required]],
      title: ['', [Validators.required]],
      message: ['', [Validators.required]]
    })
  }



  ngOnInit(): void {
  }

    // Getter to access form control
  get myForm(){
    return this.sendMessageForm.controls;
  }


  onSubmit() {
    this.submitted = true;
    if (!this.sendMessageForm.valid) {
      return false;
    } else {
      var mes = 
      {
          "senderUsername": JSON.parse(sessionStorage.userInfo).username,
          "receiverUsername": this.sendMessageForm.value.receiver,
          "title": this.sendMessageForm.value.title,
          "message": this.sendMessageForm.value.message
      }; 
      console.log(JSON.parse(sessionStorage.userInfo).authToken);
      this.apiService.sendMessage(mes, JSON.parse(sessionStorage.userInfo).authToken).subscribe(
        (res) => {
          console.log(res);
          this.ngZone.run(() => this.router.navigateByUrl('menu'))
        }, (error) => {
          console.log("dssads" + error);
        });
    }
  }

}
