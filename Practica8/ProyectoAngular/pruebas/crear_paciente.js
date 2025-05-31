import { browser } from 'k6/browser';

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
  const timestamp = new Date().getTime();
  const newPatientName = `TestPatient ${timestamp}`;
  const patientDNI = `P${timestamp % 100000000}`;

  try {
    // 1. Login
    await page.goto('http://localhost:4200');
    await page.locator('input[name="nombre"]').fill('Patrick Bateman');
    await page.locator('input[name="DNI"]').fill('1234');
    await Promise.all([
        page.waitForNavigation(),
        page.locator('button[name="login"]').click()
    ]);

    // 2. Navigate to Create Patient Form
    const addPatientButton = page.locator('button[routerLink="/paciente/create"]');
    await Promise.all([
        page.waitForNavigation(),
        addPatientButton.click()
    ]);

    // 3. Fill out the patient creation form
    // Esperar a que el primer campo del formulario esté editable como señal de que el formulario está listo
    await page.locator('input[name="dni"]').type(patientDNI)
    await page.locator('input[name="nombre"]').fill(newPatientName);
    await page.locator('input[name="edad"]').fill('33');
    const citaText = `Consulta General - ${new Date().toLocaleDateString()}`;
    await page.locator('input[name="cita"]').fill(citaText);

    // 4. Submit the form
    const submitButton = page.locator('button[type="submit"].form-btn');
    await Promise.all([
      page.waitForNavigation(),
      submitButton.click(),
    ]);

    console.log(`INFO: Paciente ${newPatientName} con DNI ${patientDNI} intento de creación enviado.`);

    // 5. Verify the patient was created
    await page.locator('h2', { hasText: 'Listado de pacientes' }).waitFor({ state: 'visible', timeout: 10000 });

    // Buscar el nombre del paciente en la página.
    // Esto buscará cualquier elemento que contenga el texto del nombre del nuevo paciente.
    // Puedes hacerlo más específico si sabes que estará en una celda de tabla, ej: 'td:has-text("${newPatientName}")'
    const patientInListLocator = page.locator(`*:text("${newPatientName}")`);
    
    // Esperar a que el elemento del paciente sea visible en la lista
    await patientInListLocator.first().waitFor({ state: 'visible', timeout: 10000 });

    // Usar check para verificar
    check(patientInListLocator, {
      'nuevo paciente aparece en la lista': (el) => el.isVisible(),
    });

    if (await patientInListLocator.isVisible()) {
        console.log(`INFO: Paciente ${newPatientName} encontrado en la lista.`);
    } else {
        console.warn(`WARN: Paciente ${newPatientName} NO encontrado en la lista.`);
        // Podrías tomar un screenshot aquí si no se encuentra para depurar
        // await page.screenshot({ path: `not_found_${timestamp}.png` });
    }
  } catch (e) {
    console.error(`ERROR: Test execution failed: ${e.message}`);
    // Considerar tomar un screenshot en caso de error para depuración
    // await page.screenshot({ path: `failure_${timestamp}.png` });
    throw e; // Re-lanza el error para que k6 marque la iteración como fallida
  } finally {
    await page.close();
  }
}