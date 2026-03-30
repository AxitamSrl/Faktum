import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-section-card',
  standalone: true,
  template: `
    <div class="mb-4 p-4 rounded-lg bg-white border border-gray-200">
      <div class="flex items-start gap-3">
        <span class="flex-shrink-0 w-7 h-7 rounded-full bg-faktum-blue flex items-center justify-center text-white text-sm font-bold">
          {{ index + 1 }}
        </span>
        <div>
          <p class="text-gray-800 leading-relaxed">{{ section.text }}</p>
          @if (section.source) {
            <p class="text-xs text-gray-500 mt-2">{{ section.source }}</p>
          }
        </div>
      </div>
    </div>
  `
})
export class SectionCardComponent {
  @Input({ required: true }) section!: { text: string; source?: string; date?: string };
  @Input({ required: true }) index!: number;
}
