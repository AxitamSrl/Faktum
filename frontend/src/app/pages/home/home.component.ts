import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FicheService } from '../../core/services/fiche.service';
import { I18nService } from '../../core/services/i18n.service';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';
import { Fiche } from '../../core/models/fiche.model';
import { CategoryFilterComponent } from './category-filter/category-filter.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, TranslatePipe, CategoryFilterComponent],
  template: `
    <section class="max-w-7xl mx-auto px-4 py-8">
      <!-- Hero -->
      <div class="text-center mb-12">
        <h1 class="text-4xl md:text-5xl font-serif font-bold text-faktum-dark mb-4">
          {{ 'siteName' | translate }}
        </h1>
        <p class="text-xl text-gray-600">{{ 'tagline' | translate }}</p>
      </div>

      <!-- Search & Filters -->
      <app-category-filter
        [fiches]="fiches()"
        (searchChange)="onSearch($event)"
        (categoryChange)="onCategoryFilter($event)" />

      <!-- Fiche Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mt-8">
        @for (fiche of filteredFiches(); track fiche.id) {
          <a [routerLink]="['/fiche', fiche.slug]" class="faktum-card block group">
            <div class="flex items-center gap-2 mb-3">
              <span class="text-xs font-medium px-2 py-0.5 rounded-full"
                    [class]="getCategoryColor(fiche.categorySlug)">
                {{ fiche.categoryName }}
              </span>
              @if (fiche.ficheType === 'ANALYSE') {
                <span class="faktum-badge-pro">5 PRO</span>
                <span class="faktum-badge-contra">5 CONTRA</span>
              }
            </div>
            <h3 class="font-serif text-lg font-semibold group-hover:text-faktum-accent transition-colors mb-2">
              {{ fiche.latestVersion?.title }}
            </h3>
            <p class="text-sm text-gray-600 line-clamp-3">
              {{ fiche.latestVersion?.summary }}
            </p>
          </a>
        }
      </div>

      @if (filteredFiches().length === 0) {
        <p class="text-center text-gray-500 py-12">{{ 'search' | translate }}</p>
      }
    </section>
  `
})
export class HomeComponent implements OnInit {
  private readonly ficheService = inject(FicheService);
  private readonly i18n = inject(I18nService);

  fiches = signal<Fiche[]>([]);
  filteredFiches = signal<Fiche[]>([]);
  private searchTerm = '';
  private categoryFilter = '';

  ngOnInit(): void {
    this.loadFiches();
  }

  loadFiches(): void {
    this.ficheService.list({
      status: 'PUBLISHED',
      locale: this.i18n.dbLocale(),
      size: 50
    }).subscribe(page => {
      this.fiches.set(page.content);
      this.applyFilters();
    });
  }

  onSearch(term: string): void {
    this.searchTerm = term.toLowerCase();
    this.applyFilters();
  }

  onCategoryFilter(category: string): void {
    this.categoryFilter = category;
    this.applyFilters();
  }

  private applyFilters(): void {
    let result = this.fiches();
    if (this.categoryFilter) {
      result = result.filter(f => f.categorySlug === this.categoryFilter);
    }
    if (this.searchTerm) {
      result = result.filter(f =>
        f.latestVersion?.title?.toLowerCase().includes(this.searchTerm) ||
        f.latestVersion?.summary?.toLowerCase().includes(this.searchTerm)
      );
    }
    this.filteredFiches.set(result);
  }

  getCategoryColor(slug: string): string {
    const colors: Record<string, string> = {
      politique: 'bg-purple-100 text-purple-800',
      economie: 'bg-amber-100 text-amber-800',
      societe: 'bg-blue-100 text-blue-800',
      technologie: 'bg-cyan-100 text-cyan-800',
      europe: 'bg-indigo-100 text-indigo-800',
      environnement: 'bg-green-100 text-green-800',
    };
    return colors[slug] ?? 'bg-gray-100 text-gray-800';
  }
}
