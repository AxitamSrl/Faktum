import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { environment } from '../../../environments/environment';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  if (req.url.includes('/api/') && authService.isAuthenticated() && authService.accessToken) {
    const cloned = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${authService.accessToken}`)
    });
    return next(cloned);
  }
  return next(req);
};
