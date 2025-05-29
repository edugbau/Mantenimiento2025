import { browser } from 'k6/browser';
import { check } from 'https://jslib.k6.io/k6-utils/1.5.0/index.js';
import { sleep } from 'k6'; // Added import for sleep


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
  const uniquePatientDNI = `DNI${Date.now()}`.slice(0, 8); 
  const newPatientName = `TestPatient ${new Date().getTime()}`;

  try {
    // 1. Login as Doctor
    await page.goto('http://localhost:4200');
    await page.locator('input[name="nombre"]').type('John Doe'); 
    await page.locator('input[name="DNI"]').type('12345678'); 

    const loginButton = page.locator('button[name="login"]');
    await Promise.all([page.waitForNavigation(), loginButton.click()]);


    // 2. Navigate to Create Patient Form
    // Updated selector to target the button by its routerLink attribute
    const addPatientButton = page.locator('button[routerLink="/paciente/create"]'); 
    
    if (!await addPatientButton.isVisible()) {
        // Fallback: Try to find a button by name or title
        const fallbackAddButton = page.locator('button[name="add"], button[title="Añadir paciente"]');
        if (await fallbackAddButton.first().isVisible()) { 
            await Promise.all([page.waitForNavigation(), fallbackAddButton.first().click()]);
        } else {
            console.error('Could not find the "Add Patient" button. Please check selectors on the patient list page. Common selectors include those targeting routerLink or specific button text.');
            throw new Error('Add patient button not found');
        }
    } else {
        await Promise.all([page.waitForNavigation(), addPatientButton.click()]);
    }    // Check navigation to the create form by looking for its title
    await check(page.locator('mat-card-title'), {
      'Navigated to create patient form': (title) => (title.textContent() || '').includes('Añadir un nuevo paciente'),
    });
    sleep(1);
    
    // 3. Fill out the patient creation form (selectors based on paciente-create.component.html)
    // Esperar a que el formulario sea visible e interactivo
    await page.waitForSelector('form.create-model-form', { timeout: 10000 });
    sleep(1); // Pausa para asegurar que el formulario esté completamente cargado
    
    // Selector más específico y robusto para el campo DNI
    console.log('DEBUG: Attempting to fill DNI field...');
    const dniInput = page.locator('mat-form-field input[name="dni"]');
    await dniInput.waitFor({ state: 'visible', timeout: 7000 });
    // Limpiar el campo primero por si acaso
    await dniInput.clear();
    await dniInput.type(uniquePatientDNI);
    console.log(`DEBUG: Typed DNI: ${uniquePatientDNI}`);
    sleep(0.5); // Pausa más larga para asegurar que el valor se registre
    
    // Selector más específico para el campo Nombre
    console.log('DEBUG: Attempting to fill Nombre field...');
    const nombreInput = page.locator('mat-form-field input[name="nombre"]');
    await nombreInput.waitFor({ state: 'visible', timeout: 5000 });
    await nombreInput.clear();
    await nombreInput.type(newPatientName);
    console.log(`DEBUG: Typed Nombre: ${newPatientName}`);
    sleep(0.5);
    
    // Selector más específico para el campo Edad
    console.log('DEBUG: Attempting to fill Edad field...');
    const edadInput = page.locator('mat-form-field input[name="edad"]');
    await edadInput.waitFor({ state: 'visible', timeout: 5000 });
    await edadInput.clear();
    await edadInput.type('33');
    console.log(`DEBUG: Typed Edad: 33`);
    sleep(0.5);
    
    // Selector más específico para el campo Cita
    console.log('DEBUG: Attempting to fill Cita field...');
    const citaInput = page.locator('mat-form-field input[name="cita"]');
    await citaInput.waitFor({ state: 'visible', timeout: 5000 });
    await citaInput.clear();
    const citaText = 'Consulta General - ' + new Date().toLocaleDateString();
    await citaInput.type(citaText);    console.log(`DEBUG: Typed Cita: ${citaText}`);
    sleep(0.5);
    
    // 4. Submit the form (selector based on paciente-create.component.html)
    sleep(1); // Asegurarse que todos los campos se procesaron correctamente
    
    // Selector más específico para el botón de crear
    const createButton = page.locator('form.create-model-form button[type="submit"]:has-text("Create")');
    // Si no encuentra el botón específico con texto, intentar con un selector más general
    if (!await createButton.isVisible()) {
        console.log('DEBUG: Trying alternative selector for Create button...');
        const altCreateButton = page.locator('form.create-model-form button[type="submit"]');
        if (await altCreateButton.isVisible()) {
            console.log('DEBUG: Found create button with alternative selector');
            await altCreateButton.waitFor({ state: 'enabled', timeout: 5000 });
            
            console.log('DEBUG: Checking if Create button is enabled...');
            const isButtonEnabled = await altCreateButton.isEnabled();
            console.log(`DEBUG: Create button enabled: ${isButtonEnabled}`);
            
            if (!isButtonEnabled) {
                console.error('ERROR: Create button is NOT enabled. Form might be invalid or fields not filled correctly.');
                throw new Error('Create button is not enabled before click');
            }
            
            console.log('DEBUG: Taking screenshot before clicking button...');
            await page.screenshot({ path: 'before_click.png' });
            
            console.log('DEBUG: Attempting to click Create button...');
            await Promise.all([
                page.waitForNavigation({ timeout: 15000 }).catch(e => console.log('Navigation timeout, continuing...')),
                altCreateButton.click()
            ]);
            
            console.log('DEBUG: Clicked Create button with alternative selector.');
        } else {
            console.error('ERROR: No Create button found with any selector');
            throw new Error('Create button not found');
        }
    } else {
        await createButton.waitFor({ state: 'enabled', timeout: 5000 });
        
        console.log('DEBUG: Checking if Create button is enabled...');
        const isButtonEnabled = await createButton.isEnabled();
        console.log(`DEBUG: Create button enabled: ${isButtonEnabled}`);
        
        if (!isButtonEnabled) {
            console.error('ERROR: Create button is NOT enabled. Form might be invalid or fields not filled correctly.');
            throw new Error('Create button is not enabled before click');
        }
        
        console.log('DEBUG: Taking screenshot before clicking button...');
        await page.screenshot({ path: 'before_click.png' });
        
        console.log('DEBUG: Attempting to click Create button...');
        await Promise.all([
            page.waitForNavigation({ timeout: 15000 }).catch(e => console.log('Navigation timeout, continuing...')),
            createButton.click()
        ]);
        
        console.log('DEBUG: Clicked Create button.');
    }
    console.log('DEBUG: Clicked Create button.');

    // Esperar a que la navegación (o actualización de la vista) ocurra y se muestre el listado
    console.log('DEBUG: Waiting for navigation back to patient list (H2 check)...');
    await page.waitForSelector('h2:has-text("Listado de pacientes")', { timeout: 10000 });
    console.log('DEBUG: Patient list H2 found after create.');

    // 5. Verify patient is in the list after returning to the patient list page
    await check(page.locator('h2'), {
      'Redirected to patient list after creation': (h2) => (h2.textContent() || '').includes('Listado de pacientes'),
    });
    sleep(1); // Allow time for the list to potentially update if it's dynamic

    // USER: Adjust the selector to find the newly created patient in the list.
    // This might involve looking for a table row, a card, or an element containing the patient's name or DNI.
    const patientInList = page.locator(`*:contains('${newPatientName}')`); // General check, make more specific if needed
    
    const isPatientVisible = await patientInList.first().isVisible(); // Check the first matching element
    console.log(`DEBUG: Checking for patient: ${newPatientName}, DNI: ${uniquePatientDNI}. Visible: ${isPatientVisible}`);

    await check(patientInList.first(), { // Check the first element that matches
      'New patient appears in the list': (el) => el.isVisible(),
    });

  } finally {
    await page.close();
  }
}
