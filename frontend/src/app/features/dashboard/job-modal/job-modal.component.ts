import { Component, input, output, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Job, JobDTO } from '../../../core/models/job.model';

@Component({
  selector: 'app-job-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './job-modal.component.html',
  styleUrls: ['./job-modal.component.scss']
})
export class JobModalComponent {
  isOpen  = input.required<boolean>();
  editJob = input<Job | null>(null);

  save   = output<{ id?: number; dto: Job }>();
  closed = output<void>();

  form: FormGroup;
  isEdit = signal(false);

  constructor(private fb: FormBuilder) {
    this.form = this.fb.group({
      company:       ['', Validators.required],
      location:      ['', Validators.required],
      url:           ['', Validators.required],
      position:      [null, Validators.required],
      contract:      [null, Validators.required],
      salaryLowest:  [null],
      salaryHighest: [null],
      status:        [null, Validators.required],
      dateApplied:   [new Date().toISOString().split('T')[0], Validators.required],
      dateClosing:   [null],
      notes:         ['', [Validators.maxLength(255)]],
    });

    effect(() => {
      const job = this.editJob();
      if (job) {
        this.isEdit.set(true);
        this.form.patchValue({ ...job });
      } else {
        this.isEdit.set(false);
        this.form.reset({
          dateApplied: new Date().toISOString().split('T')[0],
        });
      }
    });

  }

  submit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const val = this.form.value;
    const dto: JobDTO = {
      company:       val.company,
      location:      val.location,
      url:           val.url,
      position:      val.position,
      contract:      val.contract,
      salaryLowest:  val.salaryLowest ? +val.salaryLowest : null,
      salaryHighest: val.salaryHighest ? +val.salaryHighest : null,
      status:        val.status,
      dateApplied:   val.dateApplied,
      dateClosing:   val.dateClosing || undefined,
      notes:         val.notes || '',
      archived:      this.editJob()?.archived ?? false,
    };
    this.save.emit({ id: this.editJob()?.id, dto });
  }

  close() { this.closed.emit(); }
  onOverlayClick(e: MouseEvent) {
    if ((e.target as HTMLElement).classList.contains('modal-overlay')) this.close();
  }

  get f() { return this.form.controls; }
}
