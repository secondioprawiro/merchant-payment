import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface MerchantProfile {
  merchantId: string;
  kodeMerchant: string;
  namaMerchant: string;
  email: string;
}

export interface MerchantStats {
  totalTransaksiHariIni: number;
  totalBerhasilHariIni: number;
  totalGagalHariIni: number;
}

export interface Transaction {
  transactionId: string;
  refId: string;
  productName: string;
  nomorTujuan: string;
  amount: number;
  status: 'SUCCESS' | 'FAILED';
  failureReason: string | null;
  transactionDate: string;
}

export interface UpdateProfileRequest {
  namaMerchant?: string;
  email?: string;
  password?: string;
}

interface ApiResponse<T> {
  message: string;
  data: T;
}

@Injectable({ providedIn: 'root' })
export class MerchantService {
  private http = inject(HttpClient);
  private readonly BASE = '/gateway/merchant';

  getProfile(): Observable<MerchantProfile> {
    return this.http
      .get<ApiResponse<MerchantProfile>>(`${this.BASE}/me`)
      .pipe(map(res => res.data));
  }

  getTransactions(): Observable<Transaction[]> {
    return this.http
      .get<ApiResponse<Transaction[]>>('/gateway/transaction')
      .pipe(map(res => res.data));
  }

  getMerchantStats(): Observable<MerchantStats> {
    return this.http
      .get<ApiResponse<MerchantStats>>('/gateway/stats/merchant')
      .pipe(map(res => res.data));
  }

  updateProfile(merchantId: string, req: UpdateProfileRequest): Observable<MerchantProfile> {
    return this.http
      .put<ApiResponse<MerchantProfile>>(`${this.BASE}/${merchantId}`, req)
      .pipe(map(res => res.data));
  }
}
