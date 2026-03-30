import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Fiche, FichePage } from '../models/fiche.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class FicheService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/fiches`;

  list(params?: {
    status?: string;
    category?: string;
    locale?: string;
    page?: number;
    size?: number;
  }): Observable<FichePage> {
    let httpParams = new HttpParams();
    if (params?.status) httpParams = httpParams.set('status', params.status);
    if (params?.category) httpParams = httpParams.set('category', params.category);
    if (params?.locale) httpParams = httpParams.set('locale', params.locale);
    if (params?.page !== undefined) httpParams = httpParams.set('page', params.page.toString());
    if (params?.size) httpParams = httpParams.set('size', params.size.toString());
    return this.http.get<FichePage>(this.baseUrl, { params: httpParams });
  }

  getBySlug(slug: string, locale?: string): Observable<Fiche> {
    let params = new HttpParams();
    if (locale) params = params.set('locale', locale);
    return this.http.get<Fiche>(`${this.baseUrl}/${slug}`, { params });
  }

  search(query: string, locale?: string, category?: string): Observable<any[]> {
    let params = new HttpParams().set('q', query);
    if (locale) params = params.set('locale', locale);
    if (category) params = params.set('category', category);
    return this.http.get<any[]>(`${this.baseUrl}/search`, { params });
  }
}
