import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface StoreAddress {
  state: string;
  city: string;
}

export interface Store {
  storeId: string;
  brand: string;
  status: string;
  totalPumps: number;
  activePumps: number;
  offlinePumps: number;
  hyperCare: boolean;
  lastUpdatedTime: string;
  storeAddress: StoreAddress;
  latitude: string;
  longitude: string;
  anomalyCount: number;
}

export interface StoreInsight {
  storeId: string;
  value: number;
}

export interface OverviewResponse {
  topOfflinePumpStores: StoreInsight[];
  incidentsBySeverity: Record<string, number>;
  topAnomalyStores: StoreInsight[];
}

export interface ChatRequest {
  question: string;
  storeId?: string;
}

export interface Citation {
  recordType: string;
  recordId: string;
  storeId: string;
  timestamp: string;
}

export interface ChatResponse {
  answer: string;
  citations: Citation[];
  retrievedContextSummary: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private readonly apiUrl = 'http://localhost:8080/v1';

  constructor(private http: HttpClient) {}

  getStores(
    brand?: string,
    status?: string,
    sortOfflinePumpsDesc = false
  ): Observable<Store[]> {

    let params = new HttpParams();

    if (brand) {
      params = params.set('brand', brand);
    }

    if (status) {
      params = params.set('status', status);
    }

    if (sortOfflinePumpsDesc) {
      params = params.set(
        'sortOfflinePumpsDesc',
        'true'
      );
    }

    return this.http.get<Store[]>(
      `${this.apiUrl}/stores`,
      { params }
    );
  }

  getStore(storeId: string): Observable<Store> {
    return this.http.get<Store>(
      `${this.apiUrl}/stores/${storeId}`
    );
  }

  getInsights(): Observable<OverviewResponse> {
    return this.http.get<OverviewResponse>(
      `${this.apiUrl}/insights/overview`
    );
  }

  askQuestion(request: ChatRequest): Observable<ChatResponse> {
    return this.http.post<ChatResponse>(
      `${this.apiUrl}/chat`,
      request
    );
  }
}