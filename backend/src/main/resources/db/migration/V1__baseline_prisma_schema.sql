-- ============================================================
-- BASELINE: This migration documents the existing Prisma schema.
-- It will NOT be executed (Flyway baseline-on-migrate = true).
-- ============================================================

-- Enums (created by Prisma with quoted names)
CREATE TYPE "Locale" AS ENUM ('FR', 'NL', 'DE', 'EN');
CREATE TYPE "FicheStatus" AS ENUM ('DRAFT', 'PUBLISHED', 'ARCHIVED');
CREATE TYPE "FicheType" AS ENUM ('ANALYSE', 'PRATIQUE', 'RECETTE', 'GUIDE');
CREATE TYPE "QuestionStatus" AS ENUM ('PENDING', 'APPROVED', 'ANSWERED', 'REJECTED');

-- Categories
CREATE TABLE categories (
    id TEXT PRIMARY KEY,
    slug TEXT NOT NULL UNIQUE,
    "nameFr" TEXT NOT NULL,
    "nameNl" TEXT NOT NULL,
    "nameDe" TEXT NOT NULL,
    "nameEn" TEXT NOT NULL
);

-- Fiches
CREATE TABLE fiches (
    id TEXT PRIMARY KEY,
    slug TEXT NOT NULL UNIQUE,
    "categoryId" TEXT NOT NULL REFERENCES categories(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    status "FicheStatus" NOT NULL DEFAULT 'DRAFT'::"FicheStatus",
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,
    "ficheType" "FicheType" NOT NULL DEFAULT 'ANALYSE'::"FicheType"
);

-- Fiche Versions
CREATE TABLE fiche_versions (
    id TEXT PRIMARY KEY,
    "ficheId" TEXT NOT NULL REFERENCES fiches(id) ON UPDATE CASCADE ON DELETE CASCADE,
    locale "Locale" NOT NULL,
    version INTEGER NOT NULL,
    title TEXT NOT NULL,
    subtitle TEXT,
    summary TEXT NOT NULL,
    context TEXT NOT NULL,
    "proArgs" JSONB NOT NULL,
    "contraArgs" JSONB NOT NULL,
    data JSONB,
    verdict TEXT NOT NULL,
    sources JSONB NOT NULL,
    "publishedAt" TIMESTAMP(3),
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "fiche_versions_ficheId_locale_version_key" UNIQUE ("ficheId", locale, version)
);

-- Questions
CREATE TABLE questions (
    id TEXT PRIMARY KEY,
    "ficheId" TEXT REFERENCES fiches(id) ON UPDATE CASCADE ON DELETE SET NULL,
    locale "Locale" NOT NULL DEFAULT 'FR'::"Locale",
    "authorName" TEXT,
    "authorEmail" TEXT,
    text TEXT NOT NULL,
    status "QuestionStatus" NOT NULL DEFAULT 'PENDING'::"QuestionStatus",
    "rejectReason" TEXT,
    answer TEXT,
    "answerSources" JSONB,
    "approvedAt" TIMESTAMP(3),
    "answeredAt" TIMESTAMP(3),
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Crawl Logs
CREATE TABLE crawl_logs (
    id TEXT PRIMARY KEY,
    source TEXT NOT NULL,
    url TEXT NOT NULL,
    status TEXT NOT NULL,
    "dataHash" TEXT,
    "ficheSlug" TEXT,
    error TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP
);
