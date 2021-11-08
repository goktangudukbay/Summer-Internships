import { Component, OnInit, ViewChild} from '@angular/core';
import { ApiService } from './../../service/api.service';
import { Router, Data} from '@angular/router';
import {MatSort} from '@angular/material/sort';
import { MatTableDataSource, MatTable } from '@angular/material/table';



@Component({
  selector: 'app-access-log',
  templateUrl: './access-log.component.html',
  styleUrls: ['./access-log.component.css']
})
export class AccessLogComponent implements OnInit {
  
  @ViewChild('dataTable') dataTable: MatTable<any>;
  dataSource: MatTableDataSource<Access> ;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  columnsToDisplay = ['username', 'login_Time', 'logout_Time', 'browser', 'ip'];

  Access:any = [];

  constructor(private apiService: ApiService, private router: Router){
    this.dataSource = new MatTableDataSource<Access>();

    this.readAccess();

  }

  

  ngOnInit(): void {
    this.readAccess();
  }

  readAccess(){
    let dataSamples: Access[];
    this.apiService.listAccess(JSON.parse(sessionStorage.userInfo).authToken).subscribe((data) => {
     dataSamples = new Array(data.length);
     for(var i = 0; i < data.length; i++){
      dataSamples[i] = {
        username: data[i].username,
        login_Time : data[i].login_Time,
        logout_Time : data[i].logout_Time,
        browser : data[i].browser,
        ip : data[i].ip,
      };
     }
     this.dataSource = new MatTableDataSource<Access>(dataSamples);    
    this.dataSource.sort = this.sort;
    if(this.dataSource){
      this.dataTable.renderRows();
    }    
    })    
  }
}

export class Access {
  username: string;
  login_Time: Data;
  logout_Time: Date;
  browser: string;
  ip: string;
}




 