import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface Product {
  productId: string;
  productName: string;
  nominal: number,
  price: number;
  type: 'PULSA' | 'PLN';
  status: string;
}

interface ApiResponse<T> {
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private http = inject(HttpClient);
  private readonly BASE = '/gateway/product';

  getProducts(): Observable<Product[]> {
    return this.http.get<ApiResponse<Product[]>>(`${this.BASE}`)
      .pipe(map(res => res.data));
  }
}
