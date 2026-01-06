const http = require('http');
const url = require('url');

const ports = {
  salesforce: parseInt(process.env.SALESFORCE_PORT || '5205', 10),
  agilab: parseInt(process.env.AGILAB_PORT || '5206', 10),
  dqe: parseInt(process.env.DQE_PORT || '5207', 10),
  grd: parseInt(process.env.GRD_PORT || '5208', 10),
  em: parseInt(process.env.EM_PORT || '5209', 10),
};

function readBody(req) {
  return new Promise((resolve) => {
    let data = '';
    req.on('data', (chunk) => (data += chunk));
    req.on('end', () => {
      try {
        resolve(data ? JSON.parse(data) : {});
      } catch (e) {
        resolve({});
      }
    });
  });
}

function sendJson(res, status, obj) {
  const body = JSON.stringify(obj);
  res.writeHead(status, {
    'Content-Type': 'application/json',
    'Content-Length': Buffer.byteLength(body),
  });
  res.end(body);
}

function createServer(port, handlerName, router) {
  const server = http.createServer(async (req, res) => {
    const { pathname } = url.parse(req.url, true);
    const method = req.method || 'GET';
    const body = await readBody(req);
    try {
      const handled = await router({ pathname, method, body, req, res });
      if (!handled) sendJson(res, 404, { error: 'Not found', path: pathname });
    } catch (e) {
      sendJson(res, 500, { error: 'server_error', message: e.message });
    }
  });
  server.listen(port, () => {
    console.log(`[mock:${handlerName}] listening on http://localhost:${port}`);
  });
}

// Agilab
createServer(ports.agilab, 'agilab', async ({ pathname, method, body, res }) => {
  if (method === 'POST' && pathname === '/getgrille') {
    return sendJson(res, 200, {
      retrievedAt: new Date().toISOString(),
      grids: [
        { energieType: 'electricite', offre: 'standard', prixKwh: 0.215, abonnement: 12.5 },
        { energieType: 'gaz', offre: 'standard', prixKwh: 0.115, abonnement: 9.8 },
      ],
    });
  }
  if (method === 'POST' && pathname === '/calculette') {
    const energyType = body.energyType || 'electricite';
    const pdlpce = body.pdlpce || 'PDL-MOCK';
    const kwh = body?.estimation?.annualKWh || 4200;
    const prixKwh = energyType === 'gaz' ? 0.115 : 0.215;
    const abonnement = energyType === 'gaz' ? 9.8 : 12.5;
    const total = +(kwh * prixKwh + abonnement * 12).toFixed(2);
    return sendJson(res, 200, {
      quoteId: `Q-${Date.now()}`,
      pdlpce,
      energyType,
      annualKWh: kwh,
      unitPrice: prixKwh,
      subscriptionMonthly: abonnement,
      totalYear: total,
    });
  }
  return false;
});

// DQE
createServer(ports.dqe, 'dqe', async ({ pathname, method, body, res }) => {
  if (method === 'POST' && pathname === '/address/validate') {
    const adr = body || {};
    return sendJson(res, 200, {
      validated: {
        ligne1: adr.ligne1 || adr.line1 || 'UNKNOWN',
        codePostal: adr.codePostal || '00000',
        ville: adr.ville || 'UNKNOWN',
        isValid: true,
      },
    });
  }
  if (method === 'POST' && pathname === '/email/validate') {
    return sendJson(res, 200, { isValid: true });
  }
  if (method === 'POST' && pathname === '/phone/validate') {
    return sendJson(res, 200, { isValid: true });
  }
  return false;
});

// GRD
function hash(s) {
  let h = 0;
  const str = String(s || '');
  for (let i = 0; i < str.length; i++) h = (h * 31 + str.charCodeAt(i)) | 0;
  return Math.abs(h);
}

createServer(ports.grd, 'grd', async ({ pathname, method, body, res }) => {
  if (method === 'POST' && pathname === '/meter/search') {
    const seed = JSON.stringify(body.adresse || body.address || body);
    return sendJson(res, 200, { pdlpce: `PDL-${hash(seed)}` });
  }
  if (method === 'POST' && pathname === '/consent') {
    return sendJson(res, 200, { ok: true, received: body });
  }
  if (method === 'POST' && pathname === '/meter/read') {
    return sendJson(res, 200, {
      pdlpce: body.pdlpce || 'PDL-MOCK',
      puissance: 9,
      heuresCreuses: true,
      lastIndex: 12345,
    });
  }
  if (method === 'POST' && pathname === '/meter/upsert') {
    return sendJson(res, 200, { pdlpce: body.pdlpce || `PDL-${Date.now()}` });
  }
  if (method === 'POST' && pathname === '/installation/upsert') {
    return sendJson(res, 200, { installationId: `INST-${Date.now()}` });
  }
  return false;
});

// EM
createServer(ports.em, 'em', async ({ pathname, method, body, res }) => {
  if (method === 'POST' && pathname === '/estimate') {
    return sendJson(res, 200, {
      pdlpce: body.pdlpce || 'PDL-MOCK',
      annualKWh: 4200,
      method: 'mock-average',
    });
  }
  return false;
});

// Salesforce
createServer(ports.salesforce, 'salesforce', async ({ pathname, method, body, res }) => {
  if (method === 'POST' && pathname === '/saves/1') {
    if (body && body.forceError) {
      return sendJson(res, 500, { error: 'forced_error' });
    }
    return sendJson(res, 200, { status: 'ok', save: 1 });
  }
  if (method === 'POST' && pathname === '/cases') {
    return sendJson(res, 200, { caseId: `CASE-${Date.now()}`, context: body?.context || 'save1' });
  }
  return false;
});
