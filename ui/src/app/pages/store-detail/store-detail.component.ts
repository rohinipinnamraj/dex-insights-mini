import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { ApiService, Store } from '../../services/api.service';

@Component({
  selector: 'app-store-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './store-detail.component.html',
  styleUrl: './store-detail.component.css'
})
export class StoreDetailComponent implements OnInit {

  store?: Store;
  loading = false;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private apiService: ApiService
  ) {}

  ngOnInit(): void {
    const storeId = this.route.snapshot.paramMap.get('storeId');

    if (storeId) {
      this.loadStore(storeId);
    }
  }

  loadStore(storeId: string): void {
    this.loading = true;

    this.apiService.getStore(storeId).subscribe({
      next: (store) => {
        this.store = store;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Store not found.';
        this.loading = false;
      }
    });
  }
}