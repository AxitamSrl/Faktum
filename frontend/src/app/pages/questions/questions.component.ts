import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { QuestionService } from '../../core/services/question.service';
import { I18nService } from '../../core/services/i18n.service';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';
import { Question } from '../../core/models/question.model';
import { QuestionFormComponent } from './question-form/question-form.component';

@Component({
  selector: 'app-questions',
  standalone: true,
  imports: [DatePipe, RouterLink, TranslatePipe, QuestionFormComponent],
  template: `
    <section class="max-w-3xl mx-auto px-4 py-8">
      <h1 class="text-3xl font-serif font-bold mb-8">{{ 'questions' | translate }}</h1>

      <app-question-form (submitted)="loadQuestions()" />

      <div class="mt-12 space-y-6">
        @for (q of questions(); track q.id) {
          <div class="faktum-card">
            <p class="font-medium text-lg mb-2">{{ q.text }}</p>
            @if (q.answer) {
              <div class="mt-3 pl-4 border-l-2 border-faktum-accent">
                <p class="text-gray-700">{{ q.answer }}</p>
              </div>
            }
            <div class="flex items-center gap-4 text-xs text-gray-500 mt-3">
              @if (q.authorName) {
                <span>{{ q.authorName }}</span>
              }
              <span>{{ q.createdAt | date:'mediumDate' }}</span>
              @if (q.ficheSlug) {
                <a [routerLink]="['/fiche', q.ficheSlug]" class="text-blue-600 hover:underline">
                  {{ 'readMore' | translate }}
                </a>
              }
            </div>
          </div>
        }
      </div>
    </section>
  `
})
export class QuestionsComponent implements OnInit {
  private readonly questionService = inject(QuestionService);
  private readonly i18n = inject(I18nService);

  questions = signal<Question[]>([]);

  ngOnInit(): void {
    this.loadQuestions();
  }

  loadQuestions(): void {
    this.questionService.list({
      status: 'ANSWERED',
      locale: this.i18n.dbLocale(),
      size: 20
    }).subscribe(page => this.questions.set(page.content));
  }
}
