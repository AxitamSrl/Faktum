import { Component, EventEmitter, inject, Output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { QuestionService } from '../../../core/services/question.service';
import { I18nService } from '../../../core/services/i18n.service';
import { TranslatePipe } from '../../../shared/pipes/translate.pipe';

@Component({
  selector: 'app-question-form',
  standalone: true,
  imports: [FormsModule, TranslatePipe],
  template: `
    <form (ngSubmit)="onSubmit()" class="faktum-card">
      <h2 class="text-xl font-serif font-bold mb-4">{{ 'askQuestion' | translate }}</h2>

      <textarea
        [(ngModel)]="text"
        name="text"
        rows="3"
        required
        minlength="10"
        [placeholder]="'askQuestion' | translate"
        class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-faktum-accent focus:border-transparent outline-none resize-none mb-4">
      </textarea>

      <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-4">
        <input
          [(ngModel)]="authorName"
          name="authorName"
          type="text"
          placeholder="Nom (optionnel)"
          class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-faktum-accent outline-none" />
        <input
          [(ngModel)]="authorEmail"
          name="authorEmail"
          type="email"
          placeholder="Email (optionnel)"
          class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-faktum-accent outline-none" />
      </div>

      <button
        type="submit"
        [disabled]="isSubmitting()"
        class="px-6 py-2 bg-faktum-accent text-white font-medium rounded-lg hover:bg-red-600 transition-colors disabled:opacity-50">
        {{ 'askQuestion' | translate }}
      </button>

      @if (successMessage()) {
        <p class="text-green-600 text-sm mt-3">{{ successMessage() }}</p>
      }
    </form>
  `
})
export class QuestionFormComponent {
  @Output() submitted = new EventEmitter<void>();

  private readonly questionService = inject(QuestionService);
  private readonly i18n = inject(I18nService);

  text = '';
  authorName = '';
  authorEmail = '';
  isSubmitting = signal(false);
  successMessage = signal('');

  onSubmit(): void {
    if (this.text.length < 10) return;

    this.isSubmitting.set(true);
    this.questionService.submit({
      text: this.text,
      locale: this.i18n.dbLocale(),
      authorName: this.authorName || undefined,
      authorEmail: this.authorEmail || undefined,
    }).subscribe({
      next: () => {
        this.text = '';
        this.authorName = '';
        this.authorEmail = '';
        this.isSubmitting.set(false);
        this.successMessage.set(this.i18n.t('refreshRequested'));
        this.submitted.emit();
        setTimeout(() => this.successMessage.set(''), 3000);
      },
      error: () => this.isSubmitting.set(false)
    });
  }
}
