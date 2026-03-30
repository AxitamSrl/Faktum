import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Question, QuestionPage, SubmitQuestionRequest } from '../models/question.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class QuestionService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/questions`;

  list(params?: {
    status?: string;
    locale?: string;
    page?: number;
    size?: number;
  }): Observable<QuestionPage> {
    let httpParams = new HttpParams();
    if (params?.status) httpParams = httpParams.set('status', params.status);
    if (params?.locale) httpParams = httpParams.set('locale', params.locale);
    if (params?.page !== undefined) httpParams = httpParams.set('page', params.page.toString());
    if (params?.size) httpParams = httpParams.set('size', params.size.toString());
    return this.http.get<QuestionPage>(this.baseUrl, { params: httpParams });
  }

  submit(request: SubmitQuestionRequest): Observable<Question> {
    return this.http.post<Question>(this.baseUrl, request);
  }
}
