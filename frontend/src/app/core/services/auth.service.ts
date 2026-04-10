import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import {map, tap} from 'rxjs/operators';
import { Observable} from 'rxjs';
import {LoginRequest, OtpRequest, RegisterRequest} from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly API = 'http://localhost:8080/api/v1/auth';
  private readonly USER_KEY = 'jat_user';

  isLoggedIn = signal<boolean>(this.getSavedUser() !== null);
  currentUser = signal<string | null>(this.getSavedUser());

  constructor(private http: HttpClient, private router: Router) {}

  login(req: LoginRequest): Observable<void> {
    return this.http.post<void>(`${this.API}/login`, req, {
      withCredentials: true
    }).pipe(
      tap(() => this.saveSession(req.username))
    );
  }

  register(req: RegisterRequest): Observable<void> {
    return this.http.post<void>(`${this.API}/register`, req, {
      withCredentials: true
    });
  }

  refresh(): Observable<void> {
    return this.http.post<void>(`${this.API}/refresh`, {}, {
      withCredentials: true
    });
  }

  logout(): void {
    this.http.post(`${this.API}/logout`, {}, {
      withCredentials: true
    }).subscribe({
      complete: () => this.clearSession()
    });
  }

  clearSession(): void {
    localStorage.removeItem(this.USER_KEY);
    this.isLoggedIn.set(false);
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  private saveSession(username: string): void {
    localStorage.setItem(this.USER_KEY, username);
    this.isLoggedIn.set(true);
    this.currentUser.set(username);
  }

  private getSavedUser(): string | null {
    return localStorage.getItem(this.USER_KEY);
  }

  otpVerify(req: OtpRequest): Observable<void> {
    return this.http.post(`${this.API}/otp-verify`, req, {
      withCredentials: true,
      responseType: 'text'
    }).pipe(
      map(() => void 0)
    );
  }

  otpResend(email: string): Observable<void> {
    return this.http.post<void>(`${this.API}/otp-resend`, { email }, {
      withCredentials: true
    });
  }
}
