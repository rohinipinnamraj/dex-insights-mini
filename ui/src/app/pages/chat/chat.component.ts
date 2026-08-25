import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  ApiService,
  ChatResponse
} from '../../services/api.service';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css'
})
export class ChatComponent {

  question = '';
  storeId = '';

  response?: ChatResponse;

  loading = false;
  errorMessage = '';

  sampleQuestions = [
    'Summarize store 10001 health and recent activity',
    'Which stores have the highest offline pumps and what incidents are associated with them?',
    'Any stores with low tank levels that look like runout risk?'
  ];

  constructor(private apiService: ApiService) {}

  askQuestion(): void {

    if (!this.question.trim()) {
      this.errorMessage = 'Please enter a question.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.response = undefined;

    this.apiService.askQuestion({
      question: this.question,
      storeId: this.storeId.trim() || undefined
    }).subscribe({
      next: (response) => {
        this.response = response;
        this.loading = false;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage =
          'Unable to answer the question. Please try again.';
        this.loading = false;
      }
    });
  }

  useSampleQuestion(question: string): void {
    this.question = question;
  }

  clear(): void {
    this.question = '';
    this.storeId = '';
    this.response = undefined;
    this.errorMessage = '';
  }
}