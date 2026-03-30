import { Component, inject } from '@angular/core';
import { I18nService, AppLocale, LOCALE_NAMES } from '../../../core/services/i18n.service';

@Component({
  selector: 'app-language-switcher',
  standalone: true,
  template: `
    <div class="flex items-center gap-1">
      @for (loc of locales; track loc) {
        <button
          (click)="switchLocale(loc)"
          [class]="locale() === loc
            ? 'px-2 py-1 text-xs font-bold text-faktum-accent border-b-2 border-faktum-accent'
            : 'px-2 py-1 text-xs text-gray-400 hover:text-white transition-colors'"
        >
          {{ loc.toUpperCase() }}
        </button>
      }
    </div>
  `
})
export class LanguageSwitcherComponent {
  private readonly i18n = inject(I18nService);
  readonly locale = this.i18n.locale;
  readonly locales: AppLocale[] = ['fr', 'nl', 'de', 'en'];

  switchLocale(locale: AppLocale): void {
    this.i18n.setLocale(locale);
  }
}
