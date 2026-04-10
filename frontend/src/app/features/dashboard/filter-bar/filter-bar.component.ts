import { Component, input, output, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { JobFilter, JobStatus, JobPosition, JobContract } from '../../../core/models/job.model';

@Component({
  selector: 'app-filter-bar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './filter-bar.component.html',
  styleUrls: ['./filter-bar.component.scss']
})
export class FilterBarComponent {
  initialFilter = input<JobFilter>({});
  filterChange = output<JobFilter>();

  search   = signal('');
  status   = signal<JobStatus | ''>('');
  position = signal<JobPosition | ''>('');
  contract = signal<JobContract | ''>('');

  readonly STATUS_CHIPS: { label: string; value: JobStatus | '' }[] = [
    { label: 'Wszystkie', value: '' },
    { label: 'Oczekujące', value: 'PENDING' },
    { label: 'Odrzucone',  value: 'REJECTED' },
    { label: 'Ghostowane', value: 'GHOSTED' },
  ];

  setStatus(v: JobStatus | '') {
    this.status.set(v);
    this.emit();
  }

  onSearchInput(e: Event) {
    this.search.set((e.target as HTMLInputElement).value);
    this.emit();
  }

  onSelectChange() { this.emit(); }

  private emit() {
    this.filterChange.emit({
      search:   this.search(),
      status:   this.status(),
      position: this.position(),
      contract: this.contract(),
    });
  }
}
