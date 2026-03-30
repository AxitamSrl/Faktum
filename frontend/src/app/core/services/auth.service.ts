import { Injectable, inject, signal, computed } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private oauthService = inject(OAuthService);
  private discoveryDone = false;

  isAuthenticated = signal(false);
  userName = signal<string | null>(null);
  userRoles = signal<string[]>([]);

  isAdmin = computed(() =>
    this.userRoles().some(g => g.toLowerCase().includes('admin'))
  );

  isEditor = computed(() =>
    this.userRoles().some(g =>
      g.toLowerCase().includes('editor') || g.toLowerCase().includes('admin')
    )
  );

  init(): Promise<void> {
    const origin = window.location.origin;
    this.oauthService.configure({
      issuer: environment.auth.issuer,
      clientId: environment.auth.clientId,
      redirectUri: `${origin}/callback`,
      postLogoutRedirectUri: origin,
      scope: environment.auth.scope,
      responseType: 'code',
      disablePKCE: false,
      requireHttps: false,
      showDebugInformation: !environment.production,
      strictDiscoveryDocumentValidation: false,
      skipIssuerCheck: true,
    });

    this.oauthService.events.subscribe(e => {
      console.debug('OAuthEvent:', e);
      this.updateAuthState();
    });

    return this.oauthService.loadDiscoveryDocumentAndTryLogin()
      .then(() => {
        this.discoveryDone = true;
        this.updateAuthState();
        if (this.oauthService.hasValidAccessToken()) {
          this.oauthService.setupAutomaticSilentRefresh();
        }
      })
      .catch(err => {
        console.error('OIDC discovery failed:', err);
      });
  }

  private updateAuthState(): void {
    const loggedIn = this.oauthService.hasValidAccessToken();
    this.isAuthenticated.set(loggedIn);
    if (loggedIn) {
      const claims = this.oauthService.getIdentityClaims() as Record<string, unknown>;
      this.userName.set(
        (claims?.['name'] as string) || (claims?.['preferred_username'] as string) || null
      );
      this.userRoles.set((claims?.['groups'] as string[]) || []);
    } else {
      this.userName.set(null);
      this.userRoles.set([]);
    }
  }

  get accessToken(): string | null {
    return this.oauthService.getAccessToken() || null;
  }

  login(): void {
    if (this.discoveryDone) {
      this.oauthService.initCodeFlow();
    } else {
      this.oauthService.loadDiscoveryDocument().then(() => {
        this.discoveryDone = true;
        this.oauthService.initCodeFlow();
      }).catch(err => console.error('OIDC discovery failed on login:', err));
    }
  }

  logout(): void {
    this.oauthService.logOut();
  }
}
