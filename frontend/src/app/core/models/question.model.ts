import { Locale } from './fiche.model';

export type QuestionStatus = 'PENDING' | 'APPROVED' | 'ANSWERED' | 'REJECTED';

export interface Question {
  id: string;
  ficheSlug?: string;
  locale: Locale;
  authorName?: string;
  text: string;
  status: QuestionStatus;
  rejectReason?: string;
  answer?: string;
  answerSources?: string;
  approvedAt?: string;
  answeredAt?: string;
  createdAt: string;
}

export interface QuestionPage {
  content: Question[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface SubmitQuestionRequest {
  text: string;
  locale?: Locale;
  ficheSlug?: string;
  authorName?: string;
  authorEmail?: string;
}
