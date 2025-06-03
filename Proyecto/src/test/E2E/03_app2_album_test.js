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
  const newAlbumRecopilatorioName = `Recopilatorio Test ${timestamp}`;

  try {
    // 1. Login
    await page.goto(`${baseUrl}/login`);
    await page.locator('input[name="username"]').waitFor({ state: 'visible' });
    await page.locator('input[name="username"]').fill('Patrick Bateman'); // Ajusta credenciales
    await page.locator('input[name="password"]').fill('1234');       // Ajusta credenciales
    await Promise.all([
        page.waitForNavigation(),
        page.locator('button[type="submit"]').click()
    ]);
    check(page, { 'post-login url es /seleccion': (p) => p.url() === `${baseUrl}/seleccion` });
    if (page.url() !== `${baseUrl}/seleccion`) {
        throw new Error(`Login fallido o redirección incorrecta. URL: ${page.url()}`);
    }

    // 2. Navegar a App2 (Creador de Álbumes Recopilatorios)
    await Promise.all([
        page.waitForNavigation(),
        page.locator('a[href="/app2"]').click()
    ]);
    check(page, {
        'url es /app2': (p) => p.url() === `${baseUrl}/app2`,
        'titulo es Artistas': (p) => p.title() === 'Artistas'
    });
    if (page.url() !== `${baseUrl}/app2`) {
        throw new Error(`Navegación a App2 fallida. URL: ${page.url()}`);
    }

    // 3. Navegar a la página de añadir álbum
    await Promise.all([
        page.waitForNavigation(),
        page.locator('a[href="/app2/addAlbum"]').click()
    ]);
    check(page, {
        'url es /app2/addAlbum': (p) => p.url() === `${baseUrl}/app2/addAlbum`,
        'titulo es Añadir Álbum Recopilatorio': (p) => p.title() === 'Añadir Álbum Recopilatorio'
    });
    if (page.url() !== `${baseUrl}/app2/addAlbum`) {
        throw new Error(`Navegación a addAlbum fallida. URL: ${page.url()}`);
    }

    // 4. Rellenar el formulario de creación de álbum
    await page.locator('input[id="albumRecopilatorioName"]').fill(newAlbumRecopilatorioName);
    
    // Seleccionar la primera canción disponible (si hay alguna)
    const songOptions = await page.locator('select[id="canciones"] option').elements();
    if (songOptions.length > 0) {
        const firstSongValue = await songOptions[0].getProperty('value');
        await page.selectOption('select[id="canciones"] ', [firstSongValue]);
        console.log(`INFO: Seleccionada canción con valor: ${firstSongValue}`);
    } else {
        console.log('INFO: No hay canciones disponibles para seleccionar.');
    }

    // 5. Enviar el formulario
    await Promise.all([
        page.waitForNavigation(),
        page.locator('button[name="save"][type="submit"]').click()
    ]);

    // 6. Verificar redirección a /app2 y que el nuevo artista/álbum aparece
    check(page, { 
        'url es /app2 después de guardar': (p) => p.url() === `${baseUrl}/app2`,
        'titulo es Artistas después de guardar': (p) => p.title() === 'Artistas'
    });
     if (page.url() !== `${baseUrl}/app2`) {
        console.warn(`WARN: No se redirigió a /app2 después de guardar álbum. URL: ${page.url()}`);
        await page.screenshot({ path: `E2E/failure_save_album_${timestamp}.png` });
    }
    
    // Verificar si el nuevo artista (con el nombre del recopilatorio) está en la lista
    const newArtistLocator = page.locator(`td:text("${newAlbumRecopilatorioName}")`);
    await newArtistLocator.waitFor({ state: 'visible', timeout: 5000 }); // Esperar a que aparezca

    check(page, {
        'nuevo artista/album aparece en la lista': (p) => newArtistLocator.isVisible(),
    });
    if (await newArtistLocator.isVisible()) {
        console.log(`INFO: Álbum/Artista '${newAlbumRecopilatorioName}' encontrado en la lista de app2.`);
    } else {
        console.warn(`WARN: Álbum/Artista '${newAlbumRecopilatorioName}' NO encontrado en la lista de app2.`);
        await page.screenshot({ path: `E2E/not_found_album_${timestamp}.png` });
    }

    // 7. Volver a la página de selección desde app2 (home)
    await Promise.all([
        page.waitForNavigation(),
        page.locator('a[href="/seleccion"]').click()
    ]);
    check(page, { 'url es /seleccion desde app2': (p) => p.url() === `${baseUrl}/seleccion` });
     if (page.url() !== `${baseUrl}/seleccion`) {
        throw new Error(`Vuelta a selección desde App2 fallida. URL: ${page.url()}`);
    }
    console.log('INFO: Navegación de vuelta a /seleccion desde App2 exitosa.');

  } catch (e) {
    console.error(`ERROR: Prueba de App2 fallida: ${e.message}`);
    await page.screenshot({ path: `E2E/error_app2_${timestamp}.png` });
    throw e;
  } finally {
    await page.close();
  }
} 