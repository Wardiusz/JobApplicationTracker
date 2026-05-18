import { Component, computed, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { JobService } from '../../core/services/job.service';
import { Job, JobDTO, JobFilter } from '../../core/models/job.model';
import { StatCardComponent, StatType } from './stat-card/stat-card.component';
import { FilterBarComponent } from './filter-bar/filter-bar.component';
import { JobTableComponent } from './job-table/job-table.component';
import { JobModalComponent } from './job-modal/job-modal.component';
import { ConfirmationService } from 'primeng/api';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ButtonModule } from 'primeng/button'

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, StatCardComponent, FilterBarComponent, JobTableComponent, JobModalComponent, ConfirmDialogModule, ButtonModule],
  providers: [ConfirmationService],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  private auth = inject(AuthService);
  private jobSvc = inject(JobService);
  private confirmationService = inject(ConfirmationService);

  username = this.auth.currentUser;

  // Modal state
  modalOpen = signal(false);
  editingJob = signal<Job | null>(null);

  // View state
  showArchived = signal(false);
  activeFilter = signal<JobFilter>({});

  // Toast
  toastMsg  = signal('');
  toastShow = signal(false);
  toastType = signal<'success' | 'error'>('success');
  private toastTimer: any;

  // Stats from service
  totalActive = this.jobSvc.totalActive;
  totalScreening = this.jobSvc.totalScreening;
  totalInterview = this.jobSvc.totalInterview;
  totalOffer = this.jobSvc.totalOffer;
  totalPending = this.jobSvc.totalPending;
  totalRejected = this.jobSvc.totalRejected;
  totalGhosted = this.jobSvc.totalGhosted;
  totalArchived = this.jobSvc.totalArchived;

  // Filtered view
  filteredJobs = computed(() => {
    const filter = { ...this.activeFilter(), includeArchived: this.showArchived() };
    return this.jobSvc.filterLocally(this.jobSvc.getJobs(), filter);
  });

  ngOnInit() {
    this.loadJobs();
  }

  loadJobs() {
    this.jobSvc.loadJobs({ includeArchived: true }).subscribe({
      error: () => this.toast('Błąd ładowania danych', 'error')
    });
  }

  onFilterChange(f: JobFilter) {
    this.activeFilter.set(f);
  }

  onStatClick(type: StatType) {
    this.activeFilter.update(f => ({ ...f, status: type === 'all' ? '' : type }));
    this.showArchived.set(false);
  }

  openAdd() {
    this.editingJob.set(null);
    this.modalOpen.set(true);
  }

  openEdit(job: Job) {
    this.editingJob.set(job);
    this.modalOpen.set(true);
  }

  closeModal() {
    this.modalOpen.set(false);
    this.editingJob.set(null);
  }

  onSave(event: { id?: number; dto: JobDTO }) {
    const obs = event.id
      ? this.jobSvc.updateJob(event.id, event.dto)
      : this.jobSvc.addJob(event.dto);

    obs.subscribe({
      next: () => {
        this.closeModal();
        this.toast(event.id ? 'Oferta zaktualizowana' : 'Oferta dodana');
      },
      error: () => this.toast('Błąd zapisu rekordu', 'error')
    });
  }

  onArchive(id: number) {
    this.jobSvc.archiveJob(id).subscribe({
      next: () => this.toast('Oferta zarchiwizowana rekordu'),
      error: () => this.toast('Błąd archiwizacji', 'error')
    });
  }

  onRestore(id: number) {
    const job = this.jobSvc.getJobs().find(j => j.id === id);
    if (!job) return;
    this.jobSvc.unArchiveJob(id).subscribe({
      next: () => this.toast('Przywrócono rekord z archiwum'),
      error: () => this.toast('Błąd przywracania rekordu z archiwum', 'error')
    });
  }
  onDelete(id: number): void {
    const job = this.jobSvc.getJobs().find(j => j.id === id);
    if (!job) return;

    this.confirmationService.confirm({
      header: 'Usuń ofertę',
      message: `Czy na pewno chcesz usunąć? Tej operacji nie można cofnąć.`,
      accept: () => {
        this.jobSvc.deleteJob(id).subscribe({
          next: () => this.toast('Usunięto rekord'),
          error: () => this.toast('Błąd usuwania rekordu', 'error')
        });
      }
    });
  }

  toggleArchive() {
    this.showArchived.update(v => !v);
  }

  logout() { this.auth.logout(); }

  private toast(msg: string, type: 'success' | 'error' = 'success') {
    clearTimeout(this.toastTimer);
    this.toastMsg.set(msg);
    this.toastType.set(type);
    this.toastShow.set(true);
    this.toastTimer = setTimeout(() => this.toastShow.set(false), 2800);
  }

  get viewLabel() {
    return this.showArchived() ? 'Archiwum' : 'Aktywne oferty';
  }
}
