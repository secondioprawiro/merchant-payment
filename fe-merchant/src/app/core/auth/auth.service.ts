import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  namaMerchant: string;
}

interface AuthResponse {
  message: string;
  data: { email: string; token: string };
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);

  private readonly TOKEN_KEY = 'auth_token';
  private readonly BASE = '/gateway/auth';

  login(payload: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.BASE}/login`, payload).pipe(
      tap(res => this.saveToken(res.data.token))
    );
  }

  register(payload: RegisterRequest) {
    return this.http.post<AuthResponse>(`${this.BASE}/register`, payload).pipe(
      tap(res => this.saveToken(res.data.token))
    );
  }

  logout() {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem(this.TOKEN_KEY);
    }
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    if (!isPlatformBrowser(this.platformId)) return null;
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private decodePayload(token: string): Record<string, any> | null {
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch {
      return null;
    }
  }

  isTokenExpired(): boolean {
    const token = this.getToken();
    if (!token) return true;
    const payload = this.decodePayload(token);
    if (!payload || !payload['exp']) return true;
    return payload['exp'] * 1000 < Date.now();
  }

  isLoggedIn(): boolean {
    if (!this.getToken()) return false;
    if (this.isTokenExpired()) {
      this.logout();
      return false;
    }
    return true;
  }

  getRole(): string | null {
    const token = this.getToken();
    if (!token) return null;
    const payload = this.decodePayload(token);
    return payload?.['role'] ?? null;
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }

  private saveToken(token: string): void {
    if (!isPlatformBrowser(this.platformId)) return;
    localStorage.setItem(this.TOKEN_KEY, token);
  }
}
