export type FicheType = 'ANALYSE' | 'PRATIQUE' | 'RECETTE' | 'GUIDE';
export type FicheStatus = 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';
export type Locale = 'FR' | 'NL' | 'DE' | 'EN';

export interface Argument {
  text: string;
  source: string;
  date: string;
}

export interface RecipeData {
  prepTime?: string;
  cookTime?: string;
  servings?: string;
  difficulty?: string;
}

export interface FicheVersion {
  id: string;
  locale: Locale;
  version: number;
  title: string;
  subtitle?: string;
  summary: string;
  context?: string;
  verdict?: string;
  proArgs?: string;
  contraArgs?: string;
  data?: string;
  sources?: string;
  publishedAt?: string;
  createdAt: string;
}

export interface Fiche {
  id: string;
  slug: string;
  categorySlug: string;
  categoryName: string;
  ficheType: FicheType;
  status: FicheStatus;
  createdAt: string;
  updatedAt: string;
  latestVersion?: FicheVersion;
  versions?: FicheVersion[];
}

export interface FichePage {
  content: Fiche[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
