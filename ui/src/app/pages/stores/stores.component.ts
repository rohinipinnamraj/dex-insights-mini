import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ApiService, Store } from '../../services/api.service';

@Component({
  selector: 'app-stores',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule
  ],
  templateUrl: './stores.component.html',
  styleUrl: './stores.component.css'
})
export class StoresComponent implements OnInit {

  stores: Store[] = [];
  allStores: Store[] = [];

  brand = '';
  status = '';
  sortOfflinePumpsDesc = false;

  loading = false;
  errorMessage = '';

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadSummary();
    this.loadStores();
  }

  loadSummary(): void {
    this.apiService.getStores().subscribe({
      next: (stores) => {
        this.allStores = stores;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  loadStores(): void {
    this.loading = true;
    this.errorMessage = '';

    this.apiService.getStores(
      this.brand || undefined,
      this.status || undefined,
      this.sortOfflinePumpsDesc
    ).subscribe({
      next: (stores) => {
        this.stores = stores;
        this.loading = false;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to load stores.';
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.loadStores();
  }

  clearFilters(): void {
    this.brand = '';
    this.status = '';
    this.sortOfflinePumpsDesc = false;
    this.loadStores();
  }

  get totalStores(): number {
    return this.allStores.length;
  }

  get onlineStores(): number {
    return this.allStores.filter(
      store => store.status === 'ONLINE'
    ).length;
  }

  get degradedStores(): number {
    return this.allStores.filter(
      store => store.status === 'DEGRADED'
    ).length;
  }

  get offlineStores(): number {
    return this.allStores.filter(
      store => store.status === 'OFFLINE'
    ).length;
  }
}