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
  const baseUrl = 'http://localhost:8080'; 

  try {
    // 1. Navegar a la página de login
    await page.goto(`${baseUrl}/login`);
    
    // Esperar a que el formulario de login sea visible
    await page.locator('input[name="username"]').waitFor({ state: 'visible' });

    // 2. Rellenar el formulario de login
    await page.locator('input[name="username"]').fill('Bruce Wayne'); 
    await page.locator('input[name="password"]').fill('2');        
    
    // 3. Enviar el formulario de login
    await Promise.all([
        page.waitForNavigation(),
        page.locator('button[type="submit"]').click() 
    ]);

    // 4. Verificar redirección a la página de selección
    check(page, {
      'url es /seleccion': (p) => p.url() === `${baseUrl}/seleccion`,
      'titulo es Seleccionar Funcionalidad': (p) => p.title() === 'Seleccionar Funcionalidad',
      'boton Gestor de Playlists visible': (p) => p.locator('a[href="/app1"]').isVisible(),
    });

    if (page.url() === `${baseUrl}/seleccion`) {
        console.log('INFO: Login exitoso y redirección a /seleccion correcta.');
    } else {
        console.warn(`WARN: Login fallido o redirección incorrecta. URL actual: ${page.url()}`);
        await page.screenshot({ path: 'E2E/failure_login.png' });
    }

  } catch (e) {
    console.error(`ERROR: Prueba de Login fallida: ${e.message}`);
    await page.screenshot({ path: `E2E/error_login_${new Date().getTime()}.png` });
    throw e;
  } finally {
    await page.close();
  }
} 