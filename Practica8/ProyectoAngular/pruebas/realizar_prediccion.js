import { browser } from 'k6/browser';
import { check } from 'https://jslib.k6.io/k6-utils/1.5.0/index.js';
import { sleep } from 'k6';


export const options = {
  scenarios: {
    ui: {
      executor: 'shared-iterations', // para realizar iteraciones sin indicar el tiempo
      options: {
        browser: {
          type: 'chromium',
        }
      }
    }
  },
  thresholds: {
    checks: ["rate==1.0"]
  }
}

export default async function () {
  const page = await browser.newPage();
  try {
    // Ir a la página de inicio de sesión
    await page.goto('http://localhost:4200');

    // Introducir credenciales del médico
    await page.locator('input[name="nombre"]').type('Patrick Bateman');
    await page.locator('input[name="DNI"]').type('1234');

    // Hacer clic en el botón de login
    const loginButton = page.locator('button[name="login"]');
    await Promise.all([page.waitForNavigation(), loginButton.click()]);

    sleep(2); // Esperamos después del login

    // Navegar directamente a la página del paciente con ID 1
    await page.goto('http://localhost:4200/paciente/1');
    sleep(1); // Esperar a que cargue la página de información del paciente

    // Localizar el elemento <legend> que contiene el texto esperado y esperar a que sea visible
    const leyendaConTextoLocator = page.locator('div.detalle-paciente legend', { hasText: 'Detalles del paciente' });
    await leyendaConTextoLocator.waitFor({ state: 'visible' });

    // Verificar que el elemento <legend> con el texto correcto está presente
    await check(leyendaConTextoLocator, { 
        'leyenda contiene "Detalles del paciente"': async (lo) => (await lo.textContent()).includes('Detalles del paciente'),
    });

    console.log("Verificación de 'Detalles del paciente' completada.");

    // Esperar a que la tabla de imágenes esté visible
    const tablaImagenesSelector = 'table[mat-table]';
    await page.waitForSelector(tablaImagenesSelector, { state: 'visible', timeout: 15000 });
    console.log("Tabla de imágenes encontrada.");

    // Esperar a que al menos un botón 'view' esté visible en la tabla
    const viewButtonSelector = `${tablaImagenesSelector} button[name="view"]`;
    await page.waitForSelector(viewButtonSelector, { state: 'visible', timeout: 10000 });
    const viewButtonLocator = page.locator(viewButtonSelector).first();
    await viewButtonLocator.waitFor({ state: 'visible', timeout: 10000 });
    await viewButtonLocator.scrollIntoViewIfNeeded();
    console.log("Botón 'view' visible, intentando hacer clic...");
    await viewButtonLocator.click({ timeout: 5000 });
    console.log("Clic en el botón 'view' realizado.");
    sleep(2);
    // Aquí termina el script según lo solicitado.

  } finally {
    await page.close();
  }
} 