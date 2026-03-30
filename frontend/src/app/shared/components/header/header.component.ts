import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { I18nService } from '../../../core/services/i18n.service';
import { AuthService } from '../../../core/services/auth.service';
import { TranslatePipe } from '../../pipes/translate.pipe';
import { LanguageSwitcherComponent } from '../language-switcher/language-switcher.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, TranslatePipe, LanguageSwitcherComponent],
  template: `
    <header class="bg-faktum-dark text-white shadow-lg">
      <div class="max-w-7xl mx-auto px-4 py-4 flex items-center justify-between">
        <a routerLink="/" class="flex items-center gap-3">
          <span class="text-2xl font-serif font-bold tracking-wide">{{ 'siteName' | translate }}</span>
          <span class="hidden sm:inline text-sm text-gray-400 font-sans">{{ 'tagline' | translate }}</span>
        </a>
        <nav class="flex items-center gap-6">
          <a routerLink="/" routerLinkActive="text-faktum-accent" [routerLinkActiveOptions]="{exact: true}"
             class="text-sm font-medium hover:text-faktum-accent transition-colors">
            {{ 'home' | translate }}
          </a>
          <a routerLink="/questions" routerLinkActive="text-faktum-accent"
             class="text-sm font-medium hover:text-faktum-accent transition-colors">
            {{ 'questions' | translate }}
          </a>
          <app-language-switcher />
          @if (auth.isAuthenticated()) {
            <span class="text-sm text-gray-300">{{ auth.userName() }}</span>
            <button (click)="auth.logout()"
                    class="text-sm font-medium hover:text-faktum-accent transition-colors">
              {{ 'logout' | translate }}
            </button>
          } @else {
            <button (click)="auth.login()"
                    class="text-sm font-medium bg-faktum-blue px-3 py-1.5 rounded hover:opacity-90 transition-opacity">
              {{ 'login' | translate }}
            </button>
          }
        </nav>
      </div>
    </header>
  `
})
export class HeaderComponent {
  readonly auth = inject(AuthService);
}
