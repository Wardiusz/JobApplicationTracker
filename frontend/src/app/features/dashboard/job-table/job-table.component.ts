import { Component, input, output, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Job } from '../../../core/models/job.model';

type SortKey = 'company' | 'position' | 'contract' | 'salary' | 'status' | 'date_applied' | 'date_closing';

const STATUS_LABELS: Record<string, string> = {
  PENDING: 'Oczekujące', REJECTED: 'Odrzucone', GHOSTED: 'Bez odpowiedzi', SCREENING: 'Wstępna rozmowa', INTERVIEW: 'Rozmowa rekrutacyjna', OFFER: 'Oferta'
};
const POS_LABELS: Record<string, string> = {
  INTERN: 'Intern', JUNIOR: 'Junior', MID: 'Mid', SENIOR: 'Senior'
};
const CONTRACT_LABELS: Record<string, string> = {
  B2B: 'B2B', UOP: 'UoP', UOZ: 'UoZ', UOD: 'UoD', INTERN: 'Staż'
};

@Component({
  selector: 'app-job-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './job-table.component.html',
  styleUrls: ['./job-table.component.scss']
})
export class JobTableComponent {
  jobs = input.required<Job[]>();

  editJob    = output<Job>();
  archiveJob = output<number>();
  restoreJob = output<number>();

  sortKey = signal<SortKey>('date_applied');
  sortDir = signal<1 | -1>(-1);

  sorted = computed(() => {
    const key = this.sortKey();
    const dir = this.sortDir();
    return [...this.jobs()].sort((a, b) => {
      let va: any, vb: any;
      switch (key) {
        case 'company':      va = a.company;             vb = b.company;            break;
        case 'position':     va = a.position;            vb = b.position;           break;
        case 'contract':     va = a.contract;            vb = b.contract;           break;
        case 'status':       va = a.status;              vb = b.status;             break;
        case 'salary':       va = a.salaryHighest ?? 0;  vb = b.salaryHighest ?? 0; break;
        case 'date_closing': va = a.dateClosing ?? '';   vb = b.dateClosing ?? '';  break;
        default:             va = a.dateApplied ?? '';   vb = b.dateApplied ?? '';
      }
      if (typeof va === 'string') return dir * va.localeCompare(vb);
      return dir * (va - vb);
    });
  });

  sort(key: SortKey) {
    if (this.sortKey() === key) this.sortDir.update(d => d === 1 ? -1 : 1);
    else { this.sortKey.set(key); this.sortDir.set(-1); }
  }

  abbr(name: string): string {
    return name.split(' ').map(w => w[0]).join('').substring(0, 2).toUpperCase();
  }

  fmtSalary(low: number | null, high: number | null): string {
    const fmt = (n: number) => new Intl.NumberFormat('pl-PL').format(n);
    if (low && high) return `${fmt(low)} – ${fmt(high)}`;
    if (high) return fmt(high);
    return '—';
  }

  fmtDate(d: string | undefined): string {
    if (!d) return '—';
    const [y, m, day] = d.split('-');
    return `${day}.${m}.${y}`;
  }

  statusLabel(s: string) {
    return STATUS_LABELS[s] ?? s;
  }

  posLabel(p: string) {
    return POS_LABELS[p] ?? p;
  }

  contractLabel(c: string) {
    return CONTRACT_LABELS[c] ?? c;
  }

  sortIcon(key: SortKey): string {
    if (this.sortKey() !== key) return '';
    return this.sortDir() === -1 ? ' ↓' : ' ↑';
  }

  safeUrl(url: string): string {
    return url.startsWith('http') ? url : 'https://' + url;
  }

  displayUrl(url: string): string {
    try {
      return new URL(this.safeUrl(url)).hostname;
    } catch {
      return url;
    }
  }

  // Główna metoda
  calcNet(gross: number, contract: string): number {
    switch (contract) {
      case 'UOP':  return this.calcUoP(gross);
      case 'B2B':  return this.calcB2B(gross);
      case 'UOZ':  return this.calcUoZ(gross);
      case 'UOD':  return this.calcUoD(gross);
      default:     return gross;
    }
  }

// Umowa o pracę
  private calcUoP(gross: number): number {
    const zus       = gross * 0.1371;                             // emerytalne 9.76% + rentowe 1.5% + chorobowe 2.45%
    const health    = (gross - zus) * 0.09;                       // składka zdrowotna
    const taxBase   = Math.max(0, gross - zus - 250);     // koszty uzyskania przychodu
    const tax       = Math.max(0, taxBase * 0.12 - 300);  // 12% - ulga podatkowa
    return Math.round(gross - zus - health - tax);
  }

// B2B – podatek liniowy 19%, preferencyjny ZUS (pierwsze 24 miesiące)
  private calcB2B(gross: number): number {
    // Preferencyjne składki społeczne (podstawa = 30% minimalnego wynagrodzenia)
    const emerytalna  = 273.24;
    const rentowa     = 111.98;
    const wypadkowa   = 23.38;
    const zusSpołeczny = emerytalna + rentowa + wypadkowa; // bez chorobowego

    // Składka zdrowotna – 4.9% dochodu (podatek liniowy)
    const taxBase  = Math.max(0, gross - zusSpołeczny);
    const health   = taxBase * 0.049;
    const tax      = taxBase * 0.19;

    return Math.round(gross - zusSpołeczny - health - tax);
  }

// Umowa zlecenie (chorobowe dobrowolne – bez)
  private calcUoZ(gross: number): number {
    const zus     = gross * 0.1126;               // emerytalne + rentowe (bez chorobowego)
    const base    = gross - zus;                  // brutto - ZUS
    const health  = base * 0.09;
    const kup     = base * 0.20;
    const taxBase = Math.floor(base - kup);
    const tax     = Math.round(taxBase * 0.12);   // bez ulgi (brak PIT-2)
    return Math.round(gross - zus - health - tax);
  }

// Umowa o dzieło
  private calcUoD(gross: number): number {
    const kup     = gross * 0.20;        // 20% koszty uzyskania przychodu
    const taxBase = gross - kup;         // podstawa opodatkowania
    const tax     = taxBase * 0.12;      // 12% podatek
    return Math.round(gross - tax);
  }

// Formatowanie netto
  fmtNet(low: number | null, high: number | null, contract: string): string {
    const fmt = (n: number) => new Intl.NumberFormat('pl-PL').format(n);
    if (low && high) return `${fmt(this.calcNet(low, contract))} – ${fmt(this.calcNet(high, contract))}`;
    if (high)        return fmt(this.calcNet(high, contract));
    return '—';
  }

  hasNet(contract: string): boolean {
    return ['UOP', 'B2B', 'UOZ', 'UOD'].includes(contract);
  }

}
