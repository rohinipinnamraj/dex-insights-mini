import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {
  ApiService,
  OverviewResponse
} from '../../services/api.service';

@Component({
  selector: 'app-insights',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule
  ],
  templateUrl: './insights.component.html',
  styleUrl: './insights.component.css'
})
export class InsightsComponent implements OnInit {

  insights?: OverviewResponse;

  loading = false;
  errorMessage = '';

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadInsights();
  }

  loadInsights(): void {
    this.loading = true;
    this.errorMessage = '';

    this.apiService.getInsights().subscribe({
      next: (response) => {
        this.insights = response;
        this.loading = false;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to load operational insights.';
        this.loading = false;
      }
    });
  }

  get highIncidents(): number {
    return this.insights?.incidentsBySeverity['HIGH'] || 0;
  }

  get mediumIncidents(): number {
    return this.insights?.incidentsBySeverity['MEDIUM'] || 0;
  }

  get lowIncidents(): number {
    return this.insights?.incidentsBySeverity['LOW'] || 0;
  }

  get totalIncidents(): number {
    return this.highIncidents
      + this.mediumIncidents
      + this.lowIncidents;
  }

  get highestOfflinePumps(): number {
    return this.insights?.topOfflinePumpStores[0]?.value || 0;
  }

  get highestAnomalies(): number {
    return this.insights?.topAnomalyStores[0]?.value || 0;
  }

  getSeverityWidth(count: number): number {
    if (this.totalIncidents === 0) {
      return 0;
    }

    return (count / this.totalIncidents) * 100;
  }

  getOfflineWidth(value: number): number {
    if (this.highestOfflinePumps === 0) {
      return 0;
    }

    return (value / this.highestOfflinePumps) * 100;
  }

  getAnomalyWidth(value: number): number {
    if (this.highestAnomalies === 0) {
      return 0;
    }

    return (value / this.highestAnomalies) * 100;
  }
}