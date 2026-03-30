import { Component, Input } from '@angular/core';
import { Argument } from '../../../core/models/fiche.model';

@Component({
  selector: 'app-argument-card',
  standalone: true,
  template: `
    <div class="mb-4 p-4 rounded-lg border-l-4"
         [class]="type === 'pro' ? 'border-faktum-pro bg-emerald-50' : 'border-faktum-contra bg-red-50'">
      <div class="flex items-start gap-3">
        <span class="flex-shrink-0 w-7 h-7 rounded-full flex items-center justify-center text-white text-sm font-bold"
              [class]="type === 'pro' ? 'bg-faktum-pro' : 'bg-faktum-contra'">
          {{ index + 1 }}
        </span>
        <div>
          <p class="text-gray-800 leading-relaxed">{{ argument.text }}</p>
          <p class="text-xs text-gray-500 mt-2">{{ argument.source }} — {{ argument.date }}</p>
        </div>
      </div>
    </div>
  `
})
export class ArgumentCardComponent {
  @Input({ required: true }) argument!: Argument;
  @Input({ required: true }) index!: number;
  @Input({ required: true }) type!: 'pro' | 'contra';
}
