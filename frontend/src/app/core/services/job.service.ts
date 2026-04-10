import { Injectable, signal, computed } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Job, JobDTO, JobFilter } from '../models/job.model';

@Injectable({ providedIn: 'root' })
export class JobService {
  private readonly API = 'http://localhost:8080/api/v1/jobs';

  // Local state
  private _jobs = signal<Job[]>([]);

  // Derived stats
  totalActive = computed(() => this._jobs().filter(j => !j.archived).length);
  totalPending = computed(() => this._jobs().filter(j => !j.archived && j.status === 'PENDING').length);
  totalRejected = computed(() => this._jobs().filter(j => !j.archived && j.status === 'REJECTED').length);
  totalGhosted = computed(() => this._jobs().filter(j => !j.archived && j.status === 'GHOSTED').length);
  totalArchived = computed(() => this._jobs().filter(j => j.archived).length);

  constructor(private http: HttpClient) {}

  loadJobs(filter: JobFilter = {}): Observable<Job[]> {
    let params = new HttpParams();
    if (filter.status)   params = params.set('status', filter.status);
    if (filter.position) params = params.set('position', filter.position);
    if (filter.includeArchived) params = params.set('includeArchived', 'true');

    return this.http.get<Job[]>(this.API, { params }).pipe(
      tap(jobs => this._jobs.set(jobs))
    );
  }

  getJobs = this._jobs.asReadonly();

  addJob(dto: JobDTO): Observable<Job> {
    return this.http.post<Job>(this.API, dto).pipe(
      tap(job => this._jobs.update(list => [...list, job]))
    );
  }

  updateJob(id: number, dto: JobDTO): Observable<Job> {
    return this.http.put<Job>(`${this.API}/${id}/update`, dto).pipe(
      tap(updated => this._jobs.update(list => list.map(j => j.id === id ? updated : j)))
    );
  }

  archiveJob(id: number): Observable<void> {
    return this.http.patch<void>(`${this.API}/${id}/archive`, {}).pipe(
      tap(() => this._jobs.update(list => list.map(j => j.id === id ? { ...j, archived: true } : j)))
    );
  }

  unArchiveJob(id: number): Observable<void> {
    return this.http.patch<void>(`${this.API}/${id}/unarchive`, {}).pipe(
      tap(() => this._jobs.update(list => list.map(j => j.id === id ? { ...j, archived: false } : j)))
    );
  }

  updateNotes(id: number, notes: string): Observable<Job> {
    return this.http.patch<Job>(`${this.API}/${id}/notes`, notes, {
      headers: { 'Content-Type': 'text/plain' }
    }).pipe(
      tap(updated => this._jobs.update(list => list.map(j => j.id === id ? updated : j)))
    );
  }

  // Local filter helper
  filterLocally(jobs: Job[], filter: JobFilter): Job[] {
    return jobs.filter(j => {
      if (j.archived !== !!filter.includeArchived) return false;
      if (filter.status && j.status !== filter.status) return false;
      if (filter.position && j.position !== filter.position) return false;
      if (filter.contract && j.contract !== filter.contract) return false;
      if (filter.search) {
        const q = filter.search.toLowerCase();
        if (!j.company.toLowerCase().includes(q)) return false;
      }
      return true;
    });
  }
}
