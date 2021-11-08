import { Component, OnInit } from '@angular/core';
import { ApiService } from './../../service/api.service';


@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css']
})
export class ListComponent implements OnInit {
  User:any = [];

  constructor(private apiService: ApiService) { 
    this.readUser();
  }

  ngOnInit(): void {
  }

  readUser(){
    this.apiService.getUsers(JSON.parse(sessionStorage.userInfo).authToken).subscribe((data) => {
     console.log(data)
      this.User = data;
    })    
  }


  removeUser(user, index) {
    if(window.confirm('Are you sure?')) {
        this.apiService.deleteUser(user.username, (JSON.parse(sessionStorage.userInfo).authToken)).subscribe((data) => {
          this.User.splice(index, 1);
        }
      )    
    }
  }
}


  


