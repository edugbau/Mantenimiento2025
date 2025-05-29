import { browser } from 'k6/browser';
import { check } from 'https://jslib.k6.io/k6-utils/1.5.0/index.js';


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
    // Replace with the actual login page URL
    await page.goto('http://localhost:4200'); 

    // Replace with the actual locators and credentials for the doctor
    await page.locator('input[name="nombre"]').type('John Doe'); 
    await page.locator('input[name="DNI"]').type('12345678'); 

    const submitButton = page.locator('button[name="login"]'); 
    await Promise.all([page.waitForNavigation(), submitButton.click()]);

    sleep(1000); // Wait for the page to load

    // Replace with the actual locator and expected text for the welcome message
    await check(page.locator('h2'), {
      header: async (lo) => (await lo.textContent()) == 'Listado de pacientes',
    });
  } finally {
    await page.close();
  }
}
