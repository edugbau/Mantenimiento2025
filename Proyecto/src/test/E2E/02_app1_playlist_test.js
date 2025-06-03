import { browser, check } from 'k6/browser';

export const options = {
  scenarios: {
    ui: {
      executor: 'shared-iterations',
      options: {
        browser: {
          type: 'chromium',
        }
      }
    }
  }
};

export default async function () {
  const page = await browser.newPage();
  const baseUrl = 'http://localhost:8080'; // Ajusta si tu URL base es diferente
  const timestamp = new Date().getTime();
  const newPlaylistName = `Mi Playlist Test ${timestamp}`;
  const screenshotDir = 'E2E_Screenshots/'; // Crea esta carpeta en la raíz del proyecto

  // --- INICIO: Listeners de eventos de página ---
  console.log('INFO: Configurando listeners de eventos de página...');
  page.on('console', msg => {
    console.log(`BROWSER CONSOLE (${msg.type()}): ${msg.text()}`);
  });
  page.on('pageerror', error => {
    console.error(`BROWSER PAGE ERROR: ${error.message}\n${error.stack}`);
  });
  page.on('request', req => {
    console.log(`BROWSER REQUEST: ${req.method()} ${req.url()}`);
  });
  page.on('response', res => {
    console.log(`BROWSER RESPONSE: ${res.status()} ${res.url()}`);
  });
  page.on('requestfailed', req => {
    const failure = req.failure();
    console.error(`BROWSER REQUEST FAILED: ${req.method()} ${req.url()} - Error: ${failure ? failure.errorText : 'N/A'}`);
  });
   page.on('requestfinished', req => {
    if (req.failure()) {
      console.error(`BROWSER REQUEST FINISHED WITH FAILURE: ${req.method()} ${req.url()} - Error: ${req.failure().errorText}`);
    }
  });
  console.log('INFO: Listeners de eventos de página configurados.');
  // --- FIN: Listeners de eventos de página ---

  try {
    console.log('INFO: Iniciando prueba de App1...');
    // 1. Login
    await page.goto(`${baseUrl}/login`, { waitUntil: 'networkidle', timeout: 15000 });
    await page.locator('input[name="username"]').waitFor({ state: 'visible', timeout: 10000 });
    console.log('INFO: Página de login cargada (networkidle).');

    await page.locator('input[name="username"]').fill('Bruce Wayne');
    await page.locator('input[name="password"]').fill('2');
    console.log('INFO: Formulario de login rellenado.');

    await Promise.all([
        page.waitForNavigation({ waitUntil: 'networkidle', timeout: 15000 }),
        page.locator('button[type="submit"]').click()
    ]);
    console.log(`INFO: Navegación después del login. URL actual: ${page.url()}`);

    check(page, { 'post-login url es /seleccion': (p) => p.url() === `${baseUrl}/seleccion` });
    if (page.url() !== `${baseUrl}/seleccion`) {
        await page.screenshot({ path: `${screenshotDir}error_app1_not_on_seleccion_${timestamp}.png` });
        throw new Error(`Login fallido o redirección incorrecta. URL: ${page.url()}, Esperada: ${baseUrl}/seleccion`);
    }
    console.log('INFO: Redirección a /seleccion confirmada.');

    console.log('INFO: Esperando que la página /seleccion esté completamente cargada (networkidle)...');
    await page.waitForLoadState('networkidle', { timeout: 15000 });
    console.log('INFO: /seleccion networkidle alcanzado.');

    const h1Bienvenido = page.locator('h1:has-text("¡Bienvenido!")');
    await h1Bienvenido.waitFor({ state: 'visible', timeout: 10000 });
    console.log(`INFO: H1 de bienvenida en /seleccion es visible: ${await h1Bienvenido.isVisible()}`);

    const app1LinkLocator = page.locator('a[href="/app1"]');
    await app1LinkLocator.waitFor({ state: 'visible', timeout: 10000 });
    console.log(`INFO: El enlace a App1 es visible en /seleccion: ${await app1LinkLocator.isVisible()}`);

    const seleccionTitle = await page.title();
    console.log(`INFO: Título de /seleccion: ${seleccionTitle}`);
    if (seleccionTitle !== 'Seleccionar Funcionalidad') {
        console.warn(`WARN: El título de /seleccion es '${seleccionTitle}', se esperaba 'Seleccionar Funcionalidad'.`);
    }

    // 2. Navegar a App1 (Gestor de Playlists)
    console.log('INFO: Intentando navegar a App1...');
    let navigationToApp1Promise, clickApp1LinkPromise;
    try {
        console.log('INFO: Preparando page.waitForNavigation para App1...');
        navigationToApp1Promise = page.waitForNavigation({ waitUntil: 'networkidle', timeout: 15000 });
        console.log('INFO: page.waitForNavigation para App1 preparado. Haciendo clic en el enlace...');
        clickApp1LinkPromise = app1LinkLocator.click();
        console.log('INFO: Clic en enlace a App1 realizado.');
    } catch (navError) {
        console.error(`ERROR al iniciar la acción de navegación a App1: ${navError.message}\nStack: ${navError.stack}`);
        await page.screenshot({ path: `${screenshotDir}error_app1_init_nav_action_${timestamp}.png` });
        throw navError;
    }

    if (!navigationToApp1Promise || typeof navigationToApp1Promise.then !== 'function') {
      console.error('FATAL: page.waitForNavigation() no devolvió una promesa para App1.');
      throw new Error('page.waitForNavigation() no devolvió una promesa para App1.');
    }
    if (!clickApp1LinkPromise || typeof clickApp1LinkPromise.then !== 'function') {
      console.error('FATAL: app1LinkLocator.click() no devolvió una promesa.');
      await page.screenshot({ path: `${screenshotDir}error_app1_click_not_promise_${timestamp}.png` });
      throw new Error('app1LinkLocator.click() no devolvió una promesa.');
    }
    
    console.log('INFO: Ejecutando Promise.all para navegación a App1...');
    await Promise.all([
        navigationToApp1Promise,
        clickApp1LinkPromise
    ]);
    console.log(`INFO: Navegación a App1 completada. URL actual: ${page.url()}`);
    
    check(page, {
        'url es /app1/': (p) => p.url() === `${baseUrl}/app1/`,
        'titulo es Crear Playlist': (p) => p.title() === 'Crear Playlist'
    });
    if (page.url() !== `${baseUrl}/app1/`) {
        await page.screenshot({ path: `${screenshotDir}error_app1_navigation_failed_${timestamp}.png` });
        throw new Error(`Navegación a App1 fallida. URL: ${page.url()}, Esperada: ${baseUrl}/app1/`);
    }
    console.log('INFO: En la página de creación de playlist de App1.');

    // 3. Crear una nueva playlist
    await page.locator('input[name="nombre"]').fill(newPlaylistName);
    await page.locator('input[type="radio"][name="usuarioId"]').first().check();
    console.log(`INFO: Formulario de creación de playlist rellenado con nombre: ${newPlaylistName}`);
    
    await Promise.all([
        page.waitForNavigation({ waitUntil: 'networkidle', timeout: 15000 }),
        page.locator('form[action="/app1/createPlaylist"] button[type="submit"]').click()
    ]);
    console.log(`INFO: Playlist creada. URL actual: ${page.url()}`);

    check(page, {
        'url contiene /app1/viewPlaylist': (p) => p.url().startsWith(`${baseUrl}/app1/viewPlaylist`),
        'titulo contiene nombre de playlist': (p) => p.title().includes(newPlaylistName)
    });
    if (!page.url().startsWith(`${baseUrl}/app1/viewPlaylist`)) {
        console.warn(`WARN: No se redirigió a viewPlaylist. URL: ${page.url()}`);
        await page.screenshot({ path: `${screenshotDir}failure_create_playlist_redirect_${timestamp}.png` });
    } else {
        console.log('INFO: Redirección a viewPlaylist verificada.');
    }
    
    const backToSeleccionLink = page.locator('a[href="/seleccion"]');
    await backToSeleccionLink.waitFor({ state: 'visible', timeout: 10000 });
    console.log('INFO: Enlace para volver a selección es visible en viewPlaylist.');

    await Promise.all([
        page.waitForNavigation({ waitUntil: 'networkidle', timeout: 15000 }),
        backToSeleccionLink.click()
    ]);
    check(page, { 'url es /seleccion desde app1': (p) => p.url() === `${baseUrl}/seleccion` });
    if (page.url() !== `${baseUrl}/seleccion`) {
        await page.screenshot({ path: `${screenshotDir}error_app1_back_to_seleccion_failed_${timestamp}.png` });
        throw new Error(`Vuelta a selección desde App1 fallida. URL: ${page.url()}`);
    }
    console.log('INFO: Navegación de vuelta a /seleccion desde App1 exitosa.');

  } catch (e) {
    console.error(`ERROR: Prueba de App1 fallida: ${e.message}\nStack: ${e.stack}`);
    if (page && typeof page.url === 'function' && page.url()) { 
        try {
            const currentUrl = page.url();
            const pageName = currentUrl.substring(currentUrl.lastIndexOf('/') + 1) || 'page';
            await page.screenshot({ path: `${screenshotDir}error_app1_general_failure_${timestamp}_${pageName.replace(/[^a-zA-Z0-9_.-]/g, '_')}.png` });
        } catch (ssError) {
            console.error(`ERROR al tomar screenshot: ${ssError.message}`);
        }
    }
    throw e;
  } finally {
    if (page) {
        await page.close();
    }
    console.log('INFO: Prueba de App1 finalizada.');
  }
} 