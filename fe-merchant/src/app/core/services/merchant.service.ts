import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface MerchantProfile {
  merchantId: string;
  namaMerchant: string;
  email: string;
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

  updateProfile(merchantId: string, req: UpdateProfileRequest): Observable<MerchantProfile> {
    return this.http
      .put<ApiResponse<MerchantProfile>>(`${this.BASE}/${merchantId}`, req)
      .pipe(map(res => res.data));
  }
}
