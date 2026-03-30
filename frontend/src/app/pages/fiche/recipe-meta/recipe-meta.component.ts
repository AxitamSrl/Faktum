import { Component, Input, signal, OnChanges } from '@angular/core';
import { TranslatePipe } from '../../../shared/pipes/translate.pipe';
import { RecipeData } from '../../../core/models/fiche.model';

@Component({
  selector: 'app-recipe-meta',
  standalone: true,
  imports: [TranslatePipe],
  template: `
    @if (recipeData()) {
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
        @if (recipeData()!.prepTime) {
          <div class="faktum-card text-center">
            <p class="text-sm text-gray-500">{{ 'prepTime' | translate }}</p>
            <p class="text-lg font-bold">{{ recipeData()!.prepTime }}</p>
          </div>
        }
        @if (recipeData()!.cookTime) {
          <div class="faktum-card text-center">
            <p class="text-sm text-gray-500">{{ 'cookTime' | translate }}</p>
            <p class="text-lg font-bold">{{ recipeData()!.cookTime }}</p>
          </div>
        }
        @if (recipeData()!.servings) {
          <div class="faktum-card text-center">
            <p class="text-sm text-gray-500">{{ 'servings' | translate }}</p>
            <p class="text-lg font-bold">{{ recipeData()!.servings }}</p>
          </div>
        }
        @if (recipeData()!.difficulty) {
          <div class="faktum-card text-center">
            <p class="text-sm text-gray-500">{{ 'difficulty' | translate }}</p>
            <p class="text-lg font-bold">{{ recipeData()!.difficulty }}</p>
          </div>
        }
      </div>
    }
  `
})
export class RecipeMetaComponent implements OnChanges {
  @Input() data?: string;
  recipeData = signal<RecipeData | null>(null);

  ngOnChanges(): void {
    if (this.data) {
      try {
        this.recipeData.set(JSON.parse(this.data));
      } catch {
        this.recipeData.set(null);
      }
    }
  }
}
