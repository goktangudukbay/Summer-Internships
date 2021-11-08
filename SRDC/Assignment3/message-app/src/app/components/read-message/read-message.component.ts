
 import { ApiService } from './../../service/api.service';
 import { Router} from '@angular/router';
 import { Message } from './../../model/message';
 import { MatTableDataSource, MatTable } from '@angular/material/table';
 import { Component, ViewChild, OnInit } from '@angular/core';
 import {MatSort} from '@angular/material/sort';
 
 @Component({
   selector: 'app-read-message',
   templateUrl: './read-message.component.html',
   styleUrls: ['./read-message.component.css']
 })
 export class ReadMessageComponent implements OnInit {
   @ViewChild('dataTable') dataTable: MatTable<any>;
   dataSource: MatTableDataSource<Message> ;
   @ViewChild(MatSort, {static: true}) sort: MatSort;

   columnsToDisplay = ['senderUsername', 'senderFirstName', 'senderLastName', 'receiverUsername', 'receiverFirstName', 'receiverLastName', 'time', 'title', 'message'];
   constructor(private apiService: ApiService, private router: Router) { 
     this.dataSource = new MatTableDataSource<Message>();
     
     if(this.router.url.localeCompare('/inbox') == 0)
       this.readInbox();
     else
       this.readOutbox();
     
   }
   
   ngOnInit(): void {
    if(this.router.url.localeCompare('/inbox') == 0)
    this.readInbox();
   else
    this.readOutbox();
   }

   //ngAfterViewInit(): void{
     //console.log("abcde");
     //this.dataSource.sort = this.sort;
   

   
 
   readInbox(){
    let dataSamples: Message[];
    this.apiService.readInbox(JSON.parse(sessionStorage.userInfo).authToken, 
    JSON.parse(sessionStorage.userInfo).username).subscribe((data) => {
    dataSamples = new Array(data.length);
     for(var i = 0; i < data.length; i++){
      dataSamples[i] = {
        senderUsername: data[i].senderUsername,
        senderFirstName : data[i].senderFirstName,
        senderLastName : data[i].senderLastName,
        receiverUsername : data[i].receiverUsername,
        receiverFirstName : data[i].receiverFirstName,
        receiverLastName : data[i].receiverLastName,
        time : data[i].time,
        title : data[i].title,
        message : data[i].message,
      };
     }
    this.dataSource = new MatTableDataSource<Message>(dataSamples);    
    this.dataSource.sort = this.sort;

    if(this.dataSource){
      this.dataTable.renderRows();
    }
    })
    
  }
 
   readOutbox(){
     let dataSamples: Message[];
     console.log("outbox");
     this.apiService.readOutbox(JSON.parse(sessionStorage.userInfo).authToken, 
     JSON.parse(sessionStorage.userInfo).username).subscribe((data) => {
     console.log(data.length);
     dataSamples = new Array(data.length);
      for(var i = 0; i < data.length; i++){
       dataSamples[i] = {
         senderUsername: data[i].senderUsername,
         senderFirstName : data[i].senderFirstName,
         senderLastName : data[i].senderLastName,
         receiverUsername : data[i].receiverUsername,
         receiverFirstName : data[i].receiverFirstName,
         receiverLastName : data[i].receiverLastName,
         time : data[i].time,
         title : data[i].title,
         message : data[i].message,
       };
      }
      console.log("asd" + dataSamples);
     this.dataSource = new MatTableDataSource<Message>(dataSamples);    
     this.dataSource.sort = this.sort;
     if(this.dataSource){
       this.dataTable.renderRows();
     }
     })
     
   }
 }
 