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
  totalTransaksiKeseluruhan: number;
  totalBerhasilKeseluruhan: number;
  totalGagalKeseluruhan: number;
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
  namaMerchant: string | null;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

export interface AdminStats {
  totalMerchant: number;
  totalMerchantAktif: number;
  totalMerchantNonAktif: number;
  totalTransaksiHariIni: number;
  totalBerhasilHariIni: number;
  totalGagalHariIni: number;
  totalTransaksiKeseluruhan: number;
  totalBerhasilKeseluruhan: number;
  totalGagalKeseluruhan: number;
}

export interface MerchantListItem {
  merchantId: string;
  kodeMerchant: string;
  namaMerchant: string;
  status: string;
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

  getTransactions(page = 0, size = 20, startDate?: string, endDate?: string): Observable<PageResponse<Transaction>> {
    let url = `/gateway/transaction?page=${page}&size=${size}`;
    if (startDate && endDate) url += `&startDate=${startDate}&endDate=${endDate}`;
    return this.http
      .get<ApiResponse<PageResponse<Transaction>>>(url)
      .pipe(map(res => res.data));
  }

  getMerchantStats(): Observable<MerchantStats> {
    return this.http
      .get<ApiResponse<MerchantStats>>('/gateway/stats/merchant')
      .pipe(map(res => res.data));
  }

  getAdminStats(): Observable<AdminStats> {
    return this.http
      .get<ApiResponse<AdminStats>>('/gateway/stats/admin')
      .pipe(map(res => res.data));
  }

  getAllMerchants(): Observable<MerchantListItem[]> {
    return this.http
      .get<ApiResponse<MerchantListItem[]>>(this.BASE)
      .pipe(map(res => res.data));
  }

  updateProfile(merchantId: string, req: UpdateProfileRequest): Observable<MerchantProfile> {
    return this.http
      .put<ApiResponse<MerchantProfile>>(`${this.BASE}/${merchantId}`, req)
      .pipe(map(res => res.data));
  }

  deleteMerchant(merchantId: string): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.BASE}/${merchantId}`)
      .pipe(map(() => void 0));
  }
}
