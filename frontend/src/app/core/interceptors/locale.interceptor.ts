import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { I18nService } from '../services/i18n.service';

export const localeInterceptor: HttpInterceptorFn = (req, next) => {
  const i18n = inject(I18nService);
  const cloned = req.clone({
    headers: req.headers.set('Accept-Language', i18n.locale())
  });
  return next(cloned);
};
