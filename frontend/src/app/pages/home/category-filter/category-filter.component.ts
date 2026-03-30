import { Component, EventEmitter, inject, Input, OnInit, Output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { TranslatePipe } from '../../../shared/pipes/translate.pipe';
import { I18nService, AppLocale } from '../../../core/services/i18n.service';
import { Category } from '../../../core/models/category.model';
import { Fiche } from '../../../core/models/fiche.model';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-category-filter',
  standalone: true,
  imports: [FormsModule, TranslatePipe],
  template: `
    <div class="flex flex-col sm:flex-row gap-4 items-center">
      <input
        type="text"
        [placeholder]="'search' | translate"
        [(ngModel)]="searchTerm"
        (ngModelChange)="searchChange.emit($event)"
        class="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-faktum-accent focus:border-transparent outline-none" />

      <div class="flex gap-2 flex-wrap">
        <button
          (click)="selectCategory('')"
          [class]="!selectedCategory()
            ? 'px-3 py-1.5 text-sm font-medium rounded-full bg-faktum-dark text-white'
            : 'px-3 py-1.5 text-sm font-medium rounded-full bg-gray-200 text-gray-700 hover:bg-gray-300'">
          {{ 'categories' | translate }}
        </button>
        @for (cat of categories(); track cat.id) {
          <button
            (click)="selectCategory(cat.slug)"
            [class]="selectedCategory() === cat.slug
              ? 'px-3 py-1.5 text-sm font-medium rounded-full bg-faktum-dark text-white'
              : 'px-3 py-1.5 text-sm font-medium rounded-full bg-gray-200 text-gray-700 hover:bg-gray-300'">
            {{ getCategoryName(cat) }}
          </button>
        }
      </div>
    </div>
  `
})
export class CategoryFilterComponent implements OnInit {
  @Input() fiches: Fiche[] = [];
  @Output() searchChange = new EventEmitter<string>();
  @Output() categoryChange = new EventEmitter<string>();

  private readonly http = inject(HttpClient);
  private readonly i18n = inject(I18nService);

  categories = signal<Category[]>([]);
  selectedCategory = signal('');
  searchTerm = '';

  ngOnInit(): void {
    this.http.get<Category[]>(`${environment.apiUrl}/categories`)
      .subscribe(cats => this.categories.set(cats));
  }

  selectCategory(slug: string): void {
    this.selectedCategory.set(slug);
    this.categoryChange.emit(slug);
  }

  getCategoryName(cat: Category): string {
    const locale = this.i18n.locale();
    switch (locale) {
      case 'nl': return cat.nameNl;
      case 'de': return cat.nameDe;
      case 'en': return cat.nameEn;
      default: return cat.nameFr;
    }
  }
}
