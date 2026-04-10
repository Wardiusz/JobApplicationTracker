import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);

  const authReq = req.clone({ withCredentials: true });

  if (req.url.includes('/auth/')) {
    return next(authReq);
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status !== 401) {
        return throwError(() => error);
      }

      return auth.refresh().pipe(
        switchMap(() => {
          return next(authReq);
        }),
        catchError(refreshError => {
          if (refreshError.status === 401) {
            auth.clearSession();
          }
          return throwError(() => refreshError);
        })
      );
    })
  );
};
