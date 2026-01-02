# Copilot Instructions — Souscription

These instructions help AI coding agents be immediately productive in this workspace.

## Repository Snapshot
- Root: `c:\_dev\souscription`
- Architecture summary: `architecture.txt` (Save 1/2/3 flows)
- n8n workflow: `workflows/n8n/souscription_auto_contract.json`
- Example payload: `workflows/n8n/sample-payload.json`
- Env template: `.env.example`
- Slide deck: `Parcours_DIG_Archi_BV.pptx`
- OS: Windows; default shell: Windows PowerShell 5.1 (`powershell.exe`). Prefer PowerShell commands.

## First Steps (Discovery)
- Read `architecture.txt` to align with Save 1/2/3 phases.
- Inspect `workflows/n8n/souscription_auto_contract.json` to understand current orchestration.
- Use `.env.example` to configure external integrations locally before testing.

## Guardrails (Important)
- Do not scaffold or restructure the project without explicit user request.
- Keep edits minimal and focused; preserve existing naming and layout.
- Use PowerShell syntax; join commands with `;` when needed.
- Wrap file paths, commands, and identifiers in backticks.

## Developer Workflows
- n8n import:
  - UI: Workflows → Import → `workflows/n8n/souscription_auto_contract.json`
  - CLI:
    ```powershell
    n8n import:workflow --input "c:\_dev\souscription\workflows\n8n\souscription_auto_contract.json"
    ```
- Local test (after activation):
  ```powershell
  $body = Get-Content "c:\_dev\souscription\workflows\n8n\sample-payload.json" -Raw
  Invoke-RestMethod -Method Post -Uri "http://localhost:5678/webhook/souscription/request" -Body $body -ContentType "application/json"
  ```

## Architecture & Boundaries
- Phases de souscription (voir `architecture.txt`):
  - Save 1: tarification (Agilab `GetGrille`), validation adresse DQE, vérif ELD, recherche PDL/PCE (adresse + éventuellement matricule via OCR), consentement GRD en cas de CHF, lecture PDL/PCE, estimation conso (EM), devis via Agilab `calculette`, création PDL/PCE & installation, envoi à Salesforce (case en cas d’erreur).
  - Save 2: vérif email/téléphone DQE, contrôles blacklist (PDL/PCE, IBAN), génération RUM, envoi à Salesforce, MAJ personne/portefeuille, documents CPVs & Mandat (si prélèvement), génération/envoi code de signature, récup questions consentements. Note: Salesforce renvoie portefeuille & proposition commerciale.
  - Save 3: validation code de signature, signature CPVs & Mandat, MAJ personne/portefeuille, envoi à Salesforce, emails de finalisation/activation EC/bienvenue.
- Orchestration actuelle: `workflows/n8n/souscription_auto_contract.json` couvre classification IA, vérifs CRM, consentement, génération contrat, et signature (DocuSign). Adapter/étendre pour aligner finement sur Save 1/2/3.

## Project-Specific Conventions
- IA extraction: la requête IA doit renvoyer un JSON strict (intent, entities, validation, consentement). Voir noeud "IA — Classifier & Extraire" dans `workflows/n8n/souscription_auto_contract.json`.
- Webhooks:
  - Entrée: `POST /webhook/souscription/request`.
  - Callback signature: `POST /webhook/souscription/signature-callback`.
- Branches n8n: réponses explicites pour `needs_more_info` (400) en cas de champs manquants.

## Integration Points
- Agilab: `GetGrille`, `calculette` (tarifs/devis) — configurer via variables (à définir selon vos endpoints).
- DQE: validation adresse, email, téléphone — API HTTP, résultats utilisés dans Save 1/2.
- GRD: consentement en cas de CHF — trace et preuve de consentement.
- CRM/Salesforce: Save 1/2/3, création/MAJ personne, portefeuille, proposition; callback renvoyé au Digital.
- Consentement: `CONSENT_API_*` — stockage des consentements et textes.
- Contrats: `CONTRACT_API_*` — génération PDF/Doc.
- Signature: DocuSign (`DOCUSIGN_*`) ou mécanisme code OTP (Save 2/3) — adapter provider.
- Blacklist/RUM: services de contrôle IBAN/PDL/PCE et génération RUM.
 - Local mocks: utiliser `localhost` avec ports dédiés (voir `.env.example`). Exemple: `AI_ENDPOINT=http://localhost:5100`, `CRM_API_BASE_URL=http://localhost:5201`, `CONSENT_API_BASE_URL=http://localhost:5202`, etc.

## Examples
- `workflows/n8n/souscription_auto_contract.json`: noeuds "Webhook — Requête", "IA — Classifier & Extraire", "Vérifier Infos (CRM)", "Sauver Consentement", "Générer Contrat (PDF)", "DocuSign — Créer Enveloppe", "Webhook — Signature Callback".
- `.env.example`: variables requises pour IA, CRM, consentement, contrats, signature.

## Next Actions for Agents
- Affiner le workflow n8n pour implémenter explicitement Save 1/2/3 (ajout des intégrations Agilab, DQE, RUM, blacklist, CPVs, code OTP).
- Paramétrer les credentials dans n8n via l’UI ou variables d’environnement.
- Documenter les endpoints réels (Agilab, DQE, Salesforce) ici dès qu’ils sont disponibles.
