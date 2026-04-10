export type JobStatus = 'PENDING' | 'REJECTED' | 'GHOSTED' | 'SCREENING' | 'INTERVIEW' | 'OFFER';
export type JobPosition = 'INTERN' | 'JUNIOR' | 'MID' | 'SENIOR';
export type JobContract = 'B2B' | 'UOP' | 'UOZ' | 'UOD' | 'INTERN';

export interface Job {
  id?: number;
  company: string;
  location: string;
  url: string;
  position: JobPosition;
  contract: JobContract;
  salaryLowest: number | null;
  salaryHighest: number | null;
  status: JobStatus;
  dateApplied: string;
  dateClosing?: string;
  notes?: string;
  archived: boolean;
}

export interface JobFilter {
  search?: string;
  status?: JobStatus | '';
  position?: JobPosition | '';
  contract?: JobContract | '';
  includeArchived?: boolean;
}

export interface JobDTO extends Omit<Job, 'id'> {}
