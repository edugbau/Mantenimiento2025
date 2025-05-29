import { browser } from 'k6/browser';
import { check } from 'https://jslib.k6.io/k6-utils/1.5.0/index.js';
import { sleep } from 'k6'; // Import sleep from k6

export const options = {
  scenarios: {
    ui: {
      executor: 'shared-iterations',
      options: {
        browser: {
          type: 'chromium',
        },
      },
    },
  },
  thresholds: {
    checks: ['rate==1.0'],
  },
};

export default async function () {
  const page = await browser.newPage();
  try {
    // 1. Login as Doctor
    await page.goto('http://localhost:4200'); 
    await page.locator('input[name="nombre"]').type('John Doe'); 
    await page.locator('input[name="DNI"]').type('12345678');    
    
    const loginButton = page.locator('button[name="login"]'); 
    await Promise.all([page.waitForNavigation(), loginButton.click()]);

    await check(page, {
      'Login successful - redirected to patient list': (p) => p.locator('h2').textContent().includes('Listado de pacientes'),
    });
    sleep(1); // Short pause

    // 2. Navigate to a specific patient's details page
    // USER: Replace "PacienteParaTest" with the actual name or unique identifier of the patient.
    // Adjust the selector below based on your application's HTML structure for the patient list.
    // Example: const patientLink = page.locator('//mat-card-title[contains(text(),"PacienteParaTest")]/ancestor::mat-card//button[contains(text(),"Ver Detalles")]');
    const patientLink = page.locator('//a[contains(text(),"PacienteParaTest")]'); // Placeholder
    await Promise.all([page.waitForNavigation(), patientLink.click()]);
    
    await check(page, {
      'Navigated to patient details page': (p) => p.url().includes('/pacientes/') || (p.locator('h3').textContent() || '').includes('Detalles del Paciente'),
    });
    sleep(1);

    // 3. Find and navigate to the details of the pre-uploaded image (e.g., healthty.png)
    // USER: Ensure 'healthty.png' is the correct alt text or identifier for the image.
    // Adjust selector for how images are listed and selected on the patient details page.
    // Example: const imageElement = page.locator('//div[contains(@class,"image-container") and .//img[@alt="healthty.png"]]//button[contains(text(),"Ver Imagen")]');
    const imageElement = page.locator('//img[@alt="healthty.png"]'); // Placeholder, assumes clicking image navigates
    await Promise.all([page.waitForNavigation(), imageElement.click()]);

    await check(page, {
      'Navigated to image details page': (p) => p.url().includes('/image-detail/') || (p.locator('h4').textContent() || '').includes('Detalle de Imagen'),
    });
    sleep(1);

    // 4. Trigger AI prediction on the image
    // USER: Verify the selector for the "Realizar Predicción" button on the image detail page.
    const predictButton = page.locator('button:has-text("Realizar Predicción")');
    await predictButton.click();
    
    // Wait for the prediction result to appear.
    // USER: Adjust the selector for the prediction result container ('#prediction-result') and timeout if needed.
    const predictionResultSelector = '#prediction-result'; 
    try {
      await page.waitForSelector(predictionResultSelector, { timeout: 20000 }); // Wait up to 20s
    } catch (e) {
      console.error(`Prediction result element (${predictionResultSelector}) not found after 20s.`);
      // page.screenshot({ path: `error_screenshot_prediction_${new Date().toISOString().replace(/:/g, '-')}.png` });
      throw e; 
    }

    // 5. Verify that the prediction was successful
    // USER: Adjust how a successful prediction is verified.
    const predictionResultElement = page.locator(predictionResultSelector);
    const predictionText = await predictionResultElement.textContent();
    
    await check(predictionResultElement, {
      'Prediction successful and result displayed': (el) => {
        const text = (el.textContent() || '').trim();
        console.log(`DEBUG: Prediction text found: "${text}"`);
        return text !== '' && !text.toLowerCase().includes('error');
      },
    });
    sleep(1);

  } finally {
    await page.close();
  }
}
