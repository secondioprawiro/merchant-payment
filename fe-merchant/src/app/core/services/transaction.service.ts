import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

interface ApiResponse<T> {
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private http = inject(HttpClient);
  private readonly BASE = '/gateway/transaction';

  buyProduct(productId: string, nomorTujuan: string){
    return this.http.post<ApiResponse<any>>(`${this.BASE}`, {
      productId,
      nomorTujuan
    })
  }
}
