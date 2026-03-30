import { Component } from '@angular/core';
import { TranslatePipe } from '../../pipes/translate.pipe';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [TranslatePipe],
  template: `
    <footer class="bg-faktum-dark text-gray-400 py-8 mt-16">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <p class="font-serif text-lg text-white mb-2">{{ 'siteName' | translate }}</p>
        <p class="text-sm">{{ 'tagline' | translate }}</p>
        <p class="text-xs mt-4">&copy; {{ year }} FAKTUM</p>
      </div>
    </footer>
  `
})
export class FooterComponent {
  year = new Date().getFullYear();
}
