import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class HeroService {
  adminOption: boolean;

  
  constructor() {
    this.adminOption = false; 
   }
   show() { this.adminOption = true; }
   hide(){this.adminOption = false;}
}




 
