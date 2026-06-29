import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.model';
import { User } from '../models/user.model';
import { TokenStorageService } from './token-storage.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private tokenStorage = inject(TokenStorageService);
  private router = inject(Router);

  private base = environment.apiUrl;
  readonly currentEmail = signal<string | null>(this.tokenStorage.getEmail());

  login(req: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.base}/auth/login`, req).pipe(
      tap((res) => {
        this.tokenStorage.saveToken(res.token);
        this.currentEmail.set(this.tokenStorage.getEmail());
      }),
    );
  }

  register(req: RegisterRequest): Observable<User> {
    return this.http.post<User>(`${this.base}/auth/register`, req);
  }

  logout(): void {
    this.tokenStorage.removeToken();
    this.currentEmail.set(null);
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return this.tokenStorage.isLoggedIn();
  }
}
