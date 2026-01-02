# Souscription — Analyse et Contractualisation Automatisée (n8n)

Ce dossier contient un workflow n8n permettant d’analyser automatiquement les demandes issues des canaux digitaux (ex: web), d’identifier le type de demande via IA, de vérifier les informations saisies, de gérer le consentement et de proposer une contractualisation automatisée jusqu’à la signature.

## Contenu
- `workflows/n8n/souscription_auto_contract.json`: Workflow n8n (à importer).
- `workflows/n8n/sample-payload.json`: Exemple de payload pour le webhook d’entrée.

## Pré-requis
- n8n opérationnel (hébergé ou local).
- Comptes/API disponibles: IA (OpenAI/Azure OpenAI), CRM, service de consentement, génération de contrat, DocuSign (ou équivalent de signature électronique).

## Variables d’environnement n8n
Définir ces variables avant import ou dans les credentials (mocks en localhost):
- `AI_ENDPOINT`: ex. `http://localhost:5100`
- `AI_API_KEY`: clé API IA (mockée si besoin).
- `CRM_API_BASE_URL`, `CRM_API_TOKEN`: ex. `http://localhost:5201`
- `CONSENT_API_BASE_URL`, `CONSENT_API_TOKEN`: ex. `http://localhost:5202`
- `CONTRACT_API_BASE_URL`, `CONTRACT_API_TOKEN`: ex. `http://localhost:5203`
- `DOCUSIGN_BASE_URL`, `DOCUSIGN_ACCESS_TOKEN`, `DOCUSIGN_ACCOUNT_ID`, `DOCUSIGN_TEMPLATE_ID`: ex. `http://localhost:5204`
- `SALESFORCE_API_BASE_URL`: ex. `http://localhost:5205`
- `AGILAB_API_BASE_URL`: ex. `http://localhost:5206`
- `DQE_API_BASE_URL`: ex. `http://localhost:5207`
- `GRD_API_BASE_URL`: ex. `http://localhost:5208`
- `EM_API_BASE_URL`: ex. `http://localhost:5209`
- `BLACKLIST_API_BASE_URL`: ex. `http://localhost:5210`
- `RUM_API_BASE_URL`: ex. `http://localhost:5211`
- `OTP_API_BASE_URL`: ex. `http://localhost:5212`

## Import du workflow
Par l’UI n8n: `Workflows` → `Import from File` → sélectionner `workflows/n8n/souscription_auto_contract.json`.

Par CLI (si disponible):
```powershell
n8n import:workflow --input "c:\_dev\souscription\workflows\n8n\souscription_auto_contract.json"
```

## Déroulé fonctionnel
1. `Webhook — Requête` (`POST /souscription/request`): réception de la demande.
2. `Normaliser Entrée`: mise au format standard.
3. `IA — Classifier & Extraire`: classification d’intent et extraction d’entités via IA (réponse JSON selon schéma).
4. `Parser JSON IA`: parsing et sécurisation de la réponse IA.
5. `Données Complètes?`: si champs manquants → réponse explicite.
6. `Vérifier Infos (CRM)`: cross-check identité/adresse.
7. `Consentement Donné?`: si non, envoi et sauvegarde via API de consentement.
8. `Préparer Contrat` → `Générer Contrat (PDF)`.
9. `DocuSign — Créer Enveloppe`: génération de l’enveloppe de signature.
10. `Répondre — Signature en cours`: retour au front (statut + message).
11. `Webhook — Signature Callback`: callback de finalisation.
12. `CRM — Enregistrer Contrat` → `Email — Confirmation`.

## Test rapide (local)
- D démarrer n8n et activer le workflow.
- Envoyer l’exemple de payload:
```powershell
$body = Get-Content "c:\_dev\souscription\workflows\n8n\sample-payload.json" -Raw
Invoke-RestMethod -Method Post -Uri "http://localhost:5678/webhook/souscription/request" -Body $body -ContentType "application/json"
```

## Mocks rapides (optionnel)
Vous pouvez simuler les endpoints avec des réponses JSON statiques pendant les tests. Exemple de pattern attendu par le workflow:
- IA (`/v1/chat/completions` @ `AI_ENDPOINT`): renvoyer `{ choices: [ { message: { content: "{ \"intent\": \"contractualisation_energie\", \"entities\": { \"identite\": { \"prenom\": \"Camille\", \"nom\": \"Durand\", \"email\": "" }, \"adresse\": { \"ligne1\": "" } }, \"validation\": { \"complet\": true, \"manquants\": [] }, \"consentement\": { \"donne\": false, \"texte\": "" } }" } } ] }`.
- CRM (`/customers/verify`): renvoyer `{ ok: true }`.
- Consentement (`/consents`): renvoyer `{ id: "consent_123" }`.
- Contrat (`/contracts/generate`): renvoyer `{ documentUrl: "http://localhost:5203/docs/contract.pdf" }`.
- DocuSign (`/envelopes`): renvoyer `{ envelopeId: "env_123" }`.

## Notes & Sécurité
- Ne renvoyer au front que des messages non sensibles; stocker les pièces justificatives côté back/CRM.
- Journaliser les décisions (intent, validation, consentement) avec horodatage pour audit.
- Adapter les modèles IA et prompts aux données réelles et exigences RGPD/eIDAS.
