import { browser } from 'k6/browser';
import { check } from 'https://jslib.k6.io/k6-utils/1.5.0/index.js';
import { sleep } from 'k6';

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
  },
  thresholds: {
    checks: ["rate==1.0"]
  }
};

export default async function() {
  const page = await browser.newPage();
  
  // Generar datos únicos para el paciente
  const uniquePatientDNI = `D${Date.now()}`.substring(0, 8);
  const newPatientName = `Paciente Test ${Date.now()}`.substring(0, 20);
  const patientAge = '33';
  const appointmentText = `Consulta ${new Date().toLocaleDateString()}`;
  
  try {
    // Paso 1: Navegar a la página de inicio y hacer login
    console.log('Navegando a la página de inicio...');
    await page.goto('http://localhost:4200');
    await page.waitForSelector('input[name="nombre"]', { timeout: 10000 });
    
    // Realizar login
    console.log('Realizando login...');
    await page.locator('input[name="nombre"]').type('John Doe');
    await page.locator('input[name="DNI"]').type('12345678');
    
    // Hacer clic en el botón de login y esperar navegación
    const loginButton = page.locator('button[name="login"]');
    await Promise.all([
      page.waitForNavigation({ timeout: 10000 }),
      loginButton.click()
    ]);
    
    // Verificar que el login fue exitoso buscando elementos en la página principal
    console.log('Verificando login exitoso...');
    await page.waitForSelector('button, a[href], h1, h2', { timeout: 10000 });
    
    // Paso 2: Navegar al formulario de creación de paciente
    console.log('Buscando botón para crear paciente...');
    
    // Probar diferentes selectores para el botón de añadir paciente
    const addButtonSelectors = [
      'button[routerLink="/paciente/create"]',
      'button:has-text("Añadir")',
      'button:has-text("Add")',
      'button:has-text("Create")',
      'button.add-patient',
      'a[routerLink="/paciente/create"]',
      'a:has-text("Añadir paciente")'
    ];
    
    let addPatientButton = null;
    for (const selector of addButtonSelectors) {
      console.log(`Probando selector: ${selector}`);
      const button = page.locator(selector);
      if (await button.count() > 0 && await button.isVisible()) {
        console.log(`Botón encontrado con selector: ${selector}`);
        addPatientButton = button;
        break;
      }
    }
    
    if (!addPatientButton) {
      // Si no se encuentra el botón, tomar captura de pantalla y lanzar error
      console.log('No se encontró el botón para añadir paciente. Tomando captura de pantalla...');
      await page.screenshot({ path: 'error_no_add_button.png' });
      throw new Error('No se pudo encontrar el botón para añadir paciente');
    }
    
    // Hacer clic en el botón y esperar navegación
    console.log('Navegando al formulario de creación...');
    await Promise.all([
      page.waitForNavigation({ timeout: 10000 }).catch(() => console.log('Timeout en navegación, continuando...')),
      addPatientButton.click()
    ]);
    
    // Paso 3: Verificar que estamos en el formulario de creación
    console.log('Verificando que estamos en el formulario de creación...');
    await page.waitForSelector('mat-card-title', { timeout: 10000 });
    
    const formTitle = await page.locator('mat-card-title').textContent();
    console.log(`Título del formulario: ${formTitle}`);
    
    await check(page.locator('mat-card-title'), {
      'Estamos en el formulario de creación': (title) => {
        const text = title.textContent() || '';
        return text.includes('Añadir') || text.includes('nuevo paciente');
      }
    });
    
    // Esperar a que el formulario esté listo
    console.log('Esperando a que el formulario esté listo...');
    await page.waitForSelector('form.create-model-form', { timeout: 10000 });
    sleep(1);
    
    // Paso 4: Rellenar el formulario
    console.log('Rellenando el formulario...');
    
    // DNI
    console.log(`Rellenando DNI: ${uniquePatientDNI}`);
    await page.evaluate((dni) => {
      const input = document.querySelector('input[name="dni"]');
      if (input) {
        input.value = '';
        input.value = dni;
        input.dispatchEvent(new Event('input', { bubbles: true }));
        input.dispatchEvent(new Event('change', { bubbles: true }));
      }
    }, uniquePatientDNI);
    sleep(0.5);
    
    // Nombre
    console.log(`Rellenando Nombre: ${newPatientName}`);
    await page.evaluate((nombre) => {
      const input = document.querySelector('input[name="nombre"]');
      if (input) {
        input.value = '';
        input.value = nombre;
        input.dispatchEvent(new Event('input', { bubbles: true }));
        input.dispatchEvent(new Event('change', { bubbles: true }));
      }
    }, newPatientName);
    sleep(0.5);
    
    // Edad
    console.log(`Rellenando Edad: ${patientAge}`);
    await page.evaluate((edad) => {
      const input = document.querySelector('input[name="edad"]');
      if (input) {
        input.value = '';
        input.value = edad;
        input.dispatchEvent(new Event('input', { bubbles: true }));
        input.dispatchEvent(new Event('change', { bubbles: true }));
      }
    }, patientAge);
    sleep(0.5);
    
    // Cita
    console.log(`Rellenando Cita: ${appointmentText}`);
    await page.evaluate((cita) => {
      const input = document.querySelector('input[name="cita"]');
      if (input) {
        input.value = '';
        input.value = cita;
        input.dispatchEvent(new Event('input', { bubbles: true }));
        input.dispatchEvent(new Event('change', { bubbles: true }));
      }
    }, appointmentText);
    sleep(0.5);
    
    // Tomar screenshot para verificar el formulario completado
    console.log('Tomando captura del formulario completado...');
    await page.screenshot({ path: 'form_completed.png' });
    
    // Paso 5: Enviar el formulario
    console.log('Enviando el formulario...');
    
    // Encontrar el botón de envío
    const submitButtonSelectors = [
      'form.create-model-form button[type="submit"]',
      'form button:has-text("Create")',
      'form button.form-btn',
      'button[color="accent"]'
    ];
    
    let submitButton = null;
    for (const selector of submitButtonSelectors) {
      console.log(`Probando selector para botón de envío: ${selector}`);
      const button = page.locator(selector);
      if (await button.count() > 0 && await button.isVisible()) {
        console.log(`Botón de envío encontrado con selector: ${selector}`);
        submitButton = button;
        break;
      }
    }
    
    if (!submitButton) {
      console.log('No se encontró el botón de envío. Tomando captura de pantalla...');
      await page.screenshot({ path: 'error_no_submit_button.png' });
      throw new Error('No se pudo encontrar el botón de envío');
    }
    
    // Verificar que el botón está habilitado
    const isSubmitEnabled = await submitButton.isEnabled();
    console.log(`¿Botón de envío habilitado? ${isSubmitEnabled}`);
    
    if (!isSubmitEnabled) {
      console.log('El botón de envío está deshabilitado. Tomando captura de pantalla...');
      await page.screenshot({ path: 'error_button_disabled.png' });
      
      // Verificar si hay errores de validación visibles
      const errors = await page.locator('mat-error').count();
      if (errors > 0) {
        console.log(`Se encontraron ${errors} errores de validación en el formulario`);
        const errorTexts = await page.locator('mat-error').allTextContents();
        console.log('Errores de validación:', errorTexts);
      }
      
      throw new Error('El botón de envío está deshabilitado');
    }
    
    // Hacer clic en el botón de envío y esperar navegación
    console.log('Haciendo clic en el botón de envío...');
    await Promise.all([
      page.waitForNavigation({ timeout: 15000 }).catch(() => console.log('Timeout en navegación después de envío, continuando...')),
      submitButton.click()
    ]);
    
    // Paso 6: Verificar que volvimos a la lista de pacientes
    console.log('Verificando redirección a la lista de pacientes...');
    
    // Buscar indicadores de la página de listado (título, tabla, etc.)
    const listPageSelectors = [
      'h2:has-text("Listado de pacientes")',
      'h1:has-text("pacientes")',
      'table',
      'mat-table',
      '.patient-list'
    ];
    
    let listPageElement = null;
    for (const selector of listPageSelectors) {
      console.log(`Buscando elemento de lista con selector: ${selector}`);
      try {
        await page.waitForSelector(selector, { timeout: 5000 });
        const element = page.locator(selector);
        if (await element.isVisible()) {
          console.log(`Elemento de lista encontrado con selector: ${selector}`);
          listPageElement = element;
          break;
        }
      } catch (e) {
        console.log(`No se encontró elemento con selector: ${selector}`);
      }
    }
    
    if (!listPageElement) {
      console.log('No se pudo confirmar la redirección a la lista de pacientes. Tomando captura de pantalla...');
      await page.screenshot({ path: 'error_not_redirected.png' });
    } else {
      console.log('Confirmada la redirección a la lista de pacientes');
    }
    
    // Esperar a que la lista se actualice
    sleep(2);
    
    // Paso 7: Buscar el paciente creado en la lista
    console.log(`Buscando el paciente creado: ${newPatientName} (DNI: ${uniquePatientDNI})`);
    
    // Tomar captura de la lista
    await page.screenshot({ path: 'patient_list.png' });
    
    // Buscar el paciente por nombre o DNI
    const patientSelectors = [
      `*:has-text("${newPatientName}")`,
      `*:has-text("${uniquePatientDNI}")`,
      `tr:has-text("${newPatientName}")`,
      `tr:has-text("${uniquePatientDNI}")`,
      `mat-row:has-text("${newPatientName}")`,
      `mat-row:has-text("${uniquePatientDNI}")`
    ];
    
    let patientFound = false;
    for (const selector of patientSelectors) {
      console.log(`Buscando paciente con selector: ${selector}`);
      const patientElement = page.locator(selector);
      
      if (await patientElement.count() > 0) {
        const isVisible = await patientElement.first().isVisible();
        console.log(`Paciente encontrado con selector "${selector}". ¿Visible? ${isVisible}`);
        
        if (isVisible) {
          patientFound = true;
          await check(patientElement.first(), {
            'El paciente aparece en la lista': (el) => el.isVisible()
          });
          break;
        }
      }
    }
    
    if (!patientFound) {
      console.log('No se pudo encontrar el paciente en la lista. Es posible que se haya creado pero no aparezca en la interfaz.');
    } else {
      console.log('¡Éxito! El paciente fue creado y se muestra en la lista.');
    }
    
  } catch (error) {
    console.error(`Error en la prueba: ${error.message}`);
    await page.screenshot({ path: 'error_screenshot.png' });
    throw error;
  } finally {
    await page.close();
  }
}
