import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { JobStatus } from '../../../core/models/job.model';

export type StatType = 'all' | JobStatus;

@Component({
  selector: 'app-stat-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './stat-card.component.html',
  styleUrls: ['./stat-card.component.scss']
})
export class StatCardComponent {
  label = input.required<string>();
  value = input.required<number>();
  sub   = input<string>('');
  type  = input<StatType>('all');

  clicked = output<StatType>();
}
