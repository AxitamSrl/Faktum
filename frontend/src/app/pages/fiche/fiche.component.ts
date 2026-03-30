import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DatePipe } from '@angular/common';
import { FicheService } from '../../core/services/fiche.service';
import { QuestionService } from '../../core/services/question.service';
import { I18nService } from '../../core/services/i18n.service';
import { AuthService } from '../../core/services/auth.service';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';
import { Fiche, Argument } from '../../core/models/fiche.model';
import { ArgumentCardComponent } from './argument-card/argument-card.component';
import { SectionCardComponent } from './section-card/section-card.component';
import { RecipeMetaComponent } from './recipe-meta/recipe-meta.component';

@Component({
  selector: 'app-fiche',
  standalone: true,
  imports: [DatePipe, TranslatePipe, ArgumentCardComponent, SectionCardComponent, RecipeMetaComponent],
  template: `
    @if (fiche(); as f) {
      <article class="max-w-5xl mx-auto px-4 py-8">
        <!-- Header -->
        <div class="mb-8">
          <span class="text-xs font-medium px-2 py-0.5 rounded-full bg-gray-200 text-gray-700">
            {{ f.categoryName }}
          </span>
          <h1 class="text-3xl md:text-4xl font-serif font-bold mt-3">
            {{ f.latestVersion?.title }}
          </h1>
          @if (f.latestVersion?.subtitle) {
            <p class="text-xl text-gray-600 mt-2">{{ f.latestVersion?.subtitle }}</p>
          }
          <div class="flex items-center gap-4 text-sm text-gray-500 mt-4">
            @if (f.latestVersion?.publishedAt) {
              <span>{{ 'publishedOn' | translate }} {{ f.latestVersion?.publishedAt | date:'mediumDate' }}</span>
            }
            <span>{{ 'version' | translate }} {{ f.latestVersion?.version }}</span>
          </div>
        </div>

        <!-- Summary -->
        <div class="faktum-card mb-8">
          <p class="text-lg leading-relaxed">{{ f.latestVersion?.summary }}</p>
        </div>

        <!-- Context -->
        @if (f.latestVersion?.context) {
          <div class="mb-8">
            <h2 class="text-xl font-serif font-bold mb-4">{{ 'context' | translate }}</h2>
            <p class="text-gray-700 leading-relaxed">{{ f.latestVersion?.context }}</p>
          </div>
        }

        <!-- ANALYSE layout: PRO vs CONTRA -->
        @if (f.ficheType === 'ANALYSE') {
          <div class="grid grid-cols-1 md:grid-cols-2 gap-8 mb-8">
            <div>
              <h2 class="text-xl font-serif font-bold mb-4 text-faktum-pro">{{ 'pro' | translate }}</h2>
              @for (arg of proArgs(); track $index) {
                <app-argument-card [argument]="arg" [index]="$index" type="pro" />
              }
            </div>
            <div>
              <h2 class="text-xl font-serif font-bold mb-4 text-faktum-contra">{{ 'contra' | translate }}</h2>
              @for (arg of contraArgs(); track $index) {
                <app-argument-card [argument]="arg" [index]="$index" type="contra" />
              }
            </div>
          </div>
        }

        <!-- RECETTE layout -->
        @if (f.ficheType === 'RECETTE') {
          <app-recipe-meta [data]="f.latestVersion?.data" />
          <div class="mb-8">
            <h2 class="text-xl font-serif font-bold mb-4">{{ 'ingredients' | translate }}</h2>
            @for (item of proArgs(); track $index) {
              <app-section-card [section]="item" [index]="$index" />
            }
          </div>
        }

        <!-- PRATIQUE/GUIDE layout -->
        @if (f.ficheType === 'PRATIQUE' || f.ficheType === 'GUIDE') {
          <div class="mb-8">
            <h2 class="text-xl font-serif font-bold mb-4">{{ 'sections' | translate }}</h2>
            @for (section of proArgs(); track $index) {
              <app-section-card [section]="section" [index]="$index" />
            }
          </div>
        }

        <!-- Verdict -->
        @if (f.latestVersion?.verdict) {
          <div class="bg-faktum-blue text-white rounded-lg p-6 mb-8">
            <h2 class="text-xl font-serif font-bold mb-3">{{ 'verdict' | translate }}</h2>
            <p class="leading-relaxed">{{ f.latestVersion?.verdict }}</p>
          </div>
        }

        <!-- Sources -->
        @if (sources().length > 0) {
          <div class="mb-8">
            <h2 class="text-xl font-serif font-bold mb-4">{{ 'sources' | translate }}</h2>
            <ul class="space-y-2">
              @for (src of sources(); track $index) {
                <li class="text-sm">
                  <a [href]="src.url" target="_blank" rel="noopener"
                     class="text-blue-600 hover:underline">
                    {{ src.name || src.url }}
                  </a>
                </li>
              }
            </ul>
          </div>
        }

        <!-- Refresh request (authenticated users, article older than 25 days) -->
        <!-- @if (auth.isAuthenticated()) { -->
          <div class="border border-amber-300 bg-amber-50 rounded-lg p-4 flex items-center justify-between">
            <span class="text-sm text-amber-800">
              {{ daysSinceUpdate() }} {{ 'refreshExplain' | translate }}
            </span>
            @if (refreshRequested()) {
              <span class="text-sm text-green-700 font-medium">{{ 'refreshRequested' | translate }}</span>
            } @else {
              <button (click)="requestRefresh()"
                      class="text-sm font-medium bg-amber-500 text-white px-4 py-2 rounded hover:bg-amber-600 transition-colors">
                {{ 'refreshFiche' | translate }}
              </button>
            }
          </div>
        <!-- } -->
      </article>
    }
  `
})
export class FicheComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly ficheService = inject(FicheService);
  private readonly questionService = inject(QuestionService);
  private readonly i18n = inject(I18nService);
  readonly auth = inject(AuthService);

  fiche = signal<Fiche | null>(null);
  proArgs = signal<Argument[]>([]);
  contraArgs = signal<Argument[]>([]);
  sources = signal<{ name?: string; url: string }[]>([]);
  refreshRequested = signal(false);

  daysSinceUpdate = computed(() => {
    const f = this.fiche();
    if (!f) return 0;
    const updated = new Date(f.updatedAt).getTime();
    return Math.floor((Date.now() - updated) / (1000 * 60 * 60 * 24));
  });

  ngOnInit(): void {
    const slug = this.route.snapshot.paramMap.get('slug')!;
    this.ficheService.getBySlug(slug, this.i18n.dbLocale()).subscribe(f => {
      this.fiche.set(f);
      this.proArgs.set(this.parseJson(f.latestVersion?.proArgs));
      this.contraArgs.set(this.parseJson(f.latestVersion?.contraArgs));
      this.sources.set(this.parseJson(f.latestVersion?.sources));
    });
  }

  requestRefresh(): void {
    const f = this.fiche();
    if (!f) return;
    this.questionService.submit({
      text: `[Demande d'actualisation] ${f.latestVersion?.title ?? f.slug}`,
      locale: this.i18n.dbLocale() as any,
      ficheSlug: f.slug,
      authorName: this.auth.userName() ?? undefined,
    }).subscribe(() => this.refreshRequested.set(true));
  }

  private parseJson(json?: string): any[] {
    if (!json) return [];
    try { return JSON.parse(json); } catch { return []; }
  }
}
