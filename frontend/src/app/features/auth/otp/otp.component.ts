import { Component, OnDestroy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NgOtpInputComponent } from 'ng-otp-input';

@Component({
  selector: 'app-otp',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, NgOtpInputComponent],
  templateUrl: './otp.component.html',
  styleUrls: ['./otp.component.scss']
})
export class OtpComponent implements OnDestroy {
  form: FormGroup;
  email: string;
  username: string;

  loading = signal(false);
  error = signal('');
  resendCooldown = signal(0);

  timeLeft = signal<string>('5:00');
  isExpiring = signal<boolean>(false);

  success = signal(false);
  countdown = signal(5);
  private redirectInterval: ReturnType<typeof setInterval> | null = null;

  private cooldownInterval: ReturnType<typeof setInterval> | null = null;
  private expiryInterval: ReturnType<typeof setInterval> | null = null;
  private secondsLeft = 300;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {
    this.email = this.router.currentNavigation()?.extras.state?.['email']
      ?? history.state?.email;

    this.username = this.router.currentNavigation()?.extras.state?.['username']
      ?? history.state?.username;

    if (!this.email) {
      this.router.navigate(['/register']);
    }

    this.form = this.fb.group({
      code: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6), Validators.pattern(/^\d{6}$/)]]
    });

    this.startExpiryTimer();
  }

  submit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading.set(true);
    this.error.set('');

    this.auth.otpVerify({ email: this.email, otp: this.form.value.code }).subscribe({
      next: () => {
        this.success.set(true);
        this.startRedirectCountdown();
      },
      error: (err) => {
        this.error.set(err.error?.message ?? 'Nieprawidłowy lub wygasły kod.');
        this.form.get('code')?.reset();
        this.loading.set(false);
      }
    });
  }

  resend() {
    this.auth.otpResend(this.email).subscribe({
      next: () => {
        this.secondsLeft = 300;
        this.isExpiring.set(false);
        this.form.enable();
        this.error.set('');
        clearInterval(this.expiryInterval!);
        this.startExpiryTimer();
        this.startCooldown(60);
      },
      error: () => this.error.set('Nie udało się wysłać kodu.')
    });
  }

  private startExpiryTimer() {
    this.expiryInterval = setInterval(() => {
      this.secondsLeft--;
      if (this.secondsLeft <= 0) {
        clearInterval(this.expiryInterval!);
        this.error.set('Kod wygasł. Wyślij nowy.');
        this.form.disable();
        return;
      }
      const m = Math.floor(this.secondsLeft / 60);
      const s = this.secondsLeft % 60;
      this.timeLeft.set(`${m}:${s.toString().padStart(2, '0')}`);
      this.isExpiring.set(this.secondsLeft <= 60);
    }, 1000);
  }

  private startCooldown(seconds: number) {
    this.resendCooldown.set(seconds);
    this.cooldownInterval = setInterval(() => {
      this.resendCooldown.update(v => {
        if (v <= 1) { clearInterval(this.cooldownInterval!); return 0; }
        return v - 1;
      });
    }, 1000);
  }

  private startRedirectCountdown() {
    this.redirectInterval = setInterval(() => {
      this.countdown.update(v => {
        if (v <= 1) {
          clearInterval(this.redirectInterval!);
          this.router.navigate(['/login']);
          return 0;
        }
        return v - 1;
      });
    }, 1000);
  }

  ngOnDestroy() {
    if (this.cooldownInterval) clearInterval(this.cooldownInterval);
    if (this.expiryInterval) clearInterval(this.expiryInterval);
    if (this.redirectInterval) clearInterval(this.redirectInterval);
  }

  get f() { return this.form.controls; }
}
