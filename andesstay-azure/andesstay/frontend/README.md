# AndesStay — Frontend (React + Vite)

Pantallas: Dashboard, Reservas, Catálogo, Reportería y Auditoría, con
control de acceso por rol (Admin, Recepcionista, Huésped, Auditor).
Habla únicamente con el BFF (`VITE_API_BASE_URL`, por defecto `http://localhost:8080`).

## Correrlo

```bash
npm install
cp .env.example .env     # en Windows: copy .env.example .env
npm run dev              # http://localhost:5173
```

Por defecto arranca en **modo simulado**: pantalla de login donde eliges
nombre y rol, y botón "Cerrar sesión". No necesita Azure.

## Activar el login real con Azure AD (MSAL)

1. En **Azure Portal → Microsoft Entra ID → App registrations → tu app**:
   - Copia el **Application (client) ID** y el **Directory (tenant) ID**.
   - **Authentication → Add a platform → Single-page application** →
     Redirect URI: `http://localhost:5173` → Save.
   - **App roles**: crea roles con *Value* exacto: `Admin`, `Recepcionista`,
     `Huesped`, `Auditor` (también se aceptan `Operador` y `Cliente`).
2. En **Enterprise applications → tu app → Users and groups**: asigna a cada
   usuario su rol.
3. En `frontend/.env`:
   ```
   VITE_AUTH_MODE=msal
   VITE_AZURE_CLIENT_ID=<client id>
   VITE_AZURE_TENANT_ID=<tenant id>
   VITE_AZURE_REDIRECT_URI=http://localhost:5173
   ```
4. Reinicia `npm run dev`. El botón "Iniciar sesión con Microsoft" te lleva a
   Microsoft (correo + contraseña, y la pantalla de consentimiento la primera
   vez). El rol se lee del claim `roles` del token.

El login siempre pide elegir cuenta (`prompt: select_account`), y "Cerrar
sesión" cierra también la sesión de Microsoft, así puedes entrar con otro usuario.

### Validar el token en el backend (opcional, al final)

El BFF valida el JWT de Azure cuando defines estas variables de entorno
(ver `../README.md`):
```
ANDESSTAY_SECURITY_ENABLED=true
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=https://login.microsoftonline.com/<TENANT_ID>/v2.0
```
