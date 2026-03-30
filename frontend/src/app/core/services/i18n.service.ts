import { Injectable, signal, computed } from '@angular/core';

import frTranslations from '../../i18n/fr.json';
import nlTranslations from '../../i18n/nl.json';
import deTranslations from '../../i18n/de.json';
import enTranslations from '../../i18n/en.json';

export type AppLocale = 'fr' | 'nl' | 'de' | 'en';

const TRANSLATIONS: Record<AppLocale, Record<string, string>> = {
  fr: frTranslations,
  nl: nlTranslations,
  de: deTranslations,
  en: enTranslations,
};

export const LOCALE_NAMES: Record<AppLocale, string> = {
  fr: 'Fran\u00e7ais',
  nl: 'Nederlands',
  de: 'Deutsch',
  en: 'English',
};

@Injectable({ providedIn: 'root' })
export class I18nService {
  private readonly _locale = signal<AppLocale>(this.getInitialLocale());

  readonly locale = this._locale.asReadonly();
  readonly dbLocale = computed(() => this._locale().toUpperCase() as 'FR' | 'NL' | 'DE' | 'EN');

  private translations = computed(() => TRANSLATIONS[this._locale()]);

  setLocale(locale: AppLocale): void {
    this._locale.set(locale);
    localStorage.setItem('faktum-locale', locale);
  }

  t(key: string): string {
    return this.translations()[key] ?? key;
  }

  private getInitialLocale(): AppLocale {
    const stored = localStorage.getItem('faktum-locale') as AppLocale | null;
    if (stored && stored in TRANSLATIONS) return stored;
    const browserLang = navigator.language.substring(0, 2) as AppLocale;
    return browserLang in TRANSLATIONS ? browserLang : 'fr';
  }
}
