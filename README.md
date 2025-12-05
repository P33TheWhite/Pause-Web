# 📘 Documentation Technique - Pause-Web

Bienvenue sur le Wiki du projet **Pause-Web**. Ce document référence l'architecture des données, les règles de gestion et les spécificités techniques de l'application de gestion du BDE.

---

## 1. Modèle Conceptuel de Données (MCD)

Ce diagramme représente la structure de la base de données et les relations entre les entités.

```mermaid
classDiagram
    %% --- ENTITES ---
    class Utilisateur {
        +Long id
        +String email
        +String motDePasse
        +String nom
        +String prenom
        +String classe
        +boolean estCotisant
        +boolean demandeCotisationEnCours
        +boolean vip
        +boolean estStaffeur
        +Integer points
        +Integer pointsAllTime
        +BigDecimal soldeReduction
        +String icon
        +List~String~ unlockedIcons
    }

    class Evenement {
        +Long id
        +String titre
        +String description
        +LocalDate date
        +LocalTime heureDebut
        +LocalTime heureFin
        +BigDecimal prixCotisant
        +BigDecimal prixNonCotisant
        +String lienPaiement
        +Integer nbPlacesMax
        +BigDecimal coutCourses
        +boolean estArchive
    }

    class Inscription {
        +Long id
        +boolean aPaye
        +boolean aRecupereRepas
        +boolean enAttente
        +boolean estStaff
        +boolean staffValide
        +LocalDateTime dateInscription
        +BigDecimal montantAPayer
        +Integer pointsUtilises
        +BigDecimal montantReductionVoucher
        +Integer pointsGagnes
    }

    class TypeEvenement {
        +Long id
        +String libelle
    }

    class Photo {
        +Long id
        +String url
        +String titre
    }

    class InfoBureau {
        +Long id
        +String poste
        +String bio
        +String photoUrl
    }

    %% --- RELATIONS ---
    Utilisateur "1" -- "0..1" InfoBureau : possède
    Utilisateur "1" -- "*" Inscription : effectue
    Evenement "1" -- "*" Inscription : concerne
    Evenement "1" -- "*" Photo : contient
    Evenement "*" -- "*" TypeEvenement : catégorisé par
```

---

## 2. Dictionnaire des Données

### 👤 Utilisateur
L'acteur central de l'application. Il peut être un étudiant standard ou un membre du bureau.

| Attribut | Type (Java) | Description |
| :--- | :--- | :--- |
| **id** | `Long` | Identifiant technique (Clé primaire). |
| `email` | `String` | Identifiant de connexion (Unique). |
| `motDePasse` | `String` | Hash du mot de passe (Bcrypt/Argon2). |
| `nom` | `String` | Nom de famille. |
| `prenom` | `String` | Prénom. |
| `classe` | `String` | Promotion de l'étudiant (ex: "Ing1", "Prepa2"). |
| `estCotisant` | `Boolean` | **Vrai** si la cotisation BDE est active pour l'année. |
| `demandeCotisationEnCours` | `Boolean` | **Vrai** si une preuve de paiement est en attente de validation admin. |
| `vip` | `Boolean` | Statut donnant accès aux avantages VIP (coupe-file, etc.). |
| `estStaffeur` | `Boolean` | Statut global indiquant que l'élève fait partie de l'équipe staff (bénévoles). |
| `points` | `Integer` | Solde actuel de points utilisables (Gamification). |
| `pointsAllTime` | `Integer` | Somme totale des points gagnés (pour le classement général). |
| `soldeReduction` | `BigDecimal` | Porte-monnaie virtuel (en €) acquis via les points. |
| `icon` | `String` | Identifiant de l'icône de profil actuelle. |
| `unlockedIcons` | `List<String>` | Liste des IDs d'icônes que l'utilisateur a débloquées. |

### 🎉 Evenement
Représente une soirée, un afterwork ou une activité.

| Attribut | Type (Java) | Description |
| :--- | :--- | :--- |
| **id** | `Long` | Identifiant technique. |
| `titre` | `String` | Nom de l'événement. |
| `description` | `String` | Détails (Lieu, ambiance, menu...). Max 1000 cars. |
| `date` | `LocalDate` | Date de l'événement. |
| `heureDebut` | `LocalTime` | Heure de commencement. |
| `heureFin` | `LocalTime` | Heure de fin estimée. |
| `prixCotisant` | `BigDecimal` | Tarif préférentiel. |
| `prixNonCotisant` | `BigDecimal` | Tarif plein. |
| `lienPaiement` | `String` | URL vers la billetterie externe (Lydia/Wero). |
| `nbPlacesMax` | `Integer` | Capacité maximale (`null` si illimité). |
| `coutCourses` | `BigDecimal` | Coût organisationnel (pour stats BDE). |
| `estArchive` | `Boolean` | **Vrai** une fois l'événement passé (ne s'affiche plus en accueil). |

### 📝 Inscription
Table de jointure complexe gérant l'état d'un utilisateur pour un événement donné.

| Attribut | Type (Java) | Description |
| :--- | :--- | :--- |
| **id** | `Long` | Identifiant technique. |
| `aPaye` | `Boolean` | Confirme que le paiement a été reçu. |
| `aRecupereRepas` | `Boolean` | Validé lors du scan QR Code à l'entrée/au repas. |
| `enAttente` | `Boolean` | **Vrai** si l'événement était plein au moment de l'inscription. |
| `estStaff` | `Boolean` | **Vrai** si l'utilisateur travaille spécifiquement sur cet événement. |
| `staffValide` | `Boolean` | Validation finale par le bureau (débloque les points/récompenses). |
| `dateInscription` | `LocalDateTime`| Horodatage de la demande d'inscription. |
| `montantAPayer` | `BigDecimal` | Prix final gelé au moment de l'inscription. |
| `pointsUtilises` | `Integer` | Nombre de points brûlés pour réduire le prix. |
| `montantReductionVoucher` | `BigDecimal` | Valeur en euros de la réduction points. |
| `pointsGagnes` | `Integer` | Points crédités une fois l'événement terminé/validé. |

### 🏷️ TypeEvenement
Catégorisation pour le filtrage (Tag).

| Attribut | Type (Java) | Description |
| :--- | :--- | :--- |
| **id** | `Long` | Identifiant unique. |
| `libelle` | `String` | Nom (ex: "Soirée", "Afterwork", "Sport"). |

### 📸 Photo
Galerie d'images liée à un événement.

| Attribut | Type (Java) | Description |
| :--- | :--- | :--- |
| **id** | `Long` | Identifiant unique. |
| `url` | `String` | Chemin de stockage de l'image. |
| `titre` | `String` | Légende de la photo. |

### 👔 InfoBureau
Extension du profil pour les membres du BDE.

| Attribut | Type (Java) | Description |
| :--- | :--- | :--- |
| **id** | `Long` | Identifiant unique. |
| `poste` | `String` | Rôle (ex: "Trésorier"). |
| `bio` | `String` | Description publique du membre. |
| `photoUrl` | `String` | Photo officielle du membre. |

---

## 3. Règles de Gestion (Business Logic)

### 💰 Gestion des Tarifs & Paiements
1.  **Calcul du prix :** Lors de l'inscription, le système vérifie le booléen `Utilisateur.estCotisant`.
    * Si `true` → `Inscription.montantAPayer` prend la valeur de `Evenement.prixCotisant`.
    * Si `false` → `Inscription.montantAPayer` prend la valeur de `Evenement.prixNonCotisant`.
2.  **Utilisation de réduction :** Si l'utilisateur utilise son `soldeReduction`, le `montantAPayer` est diminué d'autant, et le solde est débité.

### ⏳ Gestion des Places (Liste d'attente)
1.  Si le nombre d'inscriptions (où `enAttente == false`) atteint `Evenement.nbPlacesMax` :
    * Toute nouvelle inscription est créée avec `enAttente = true`.
2.  Si une place se libère (désistement), l'admin peut passer un utilisateur de la liste d'attente vers la liste principale manuellement.

### 🛠️ Gestion du Staff
Le système distingue deux niveaux de "Staff" :
1.  **L'attribut Utilisateur `estStaffeur`** : C'est un rôle global. L'étudiant fait partie du pool de bénévoles. Il a accès à l'interface de candidature staff.
2.  **L'attribut Inscription `estStaff`** : L'étudiant a été sélectionné pour travailler sur *cet* événement précis. Il ne paie pas sa place (`montantAPayer = 0`).
3.  **Validation (`staffValide`)** : Après l'événement, un admin valide que le staffeur a bien fait son travail. Cela déclenche l'attribution des points bonus.

### 🎮 Gamification (Points & Icônes)
* **Gagner des points :** Les points sont attribués lors de la participation aux événements ou lors de la validation d'une mission staff.
* **Dépenser des points :** Les points peuvent être convertis en `soldeReduction` (€) ou utilisés pour débloquer des `icons` cosmétiques.
* **Icônes :** Un utilisateur ne peut équiper l'icône "X" que si "X" est présent dans sa liste `unlockedIcons`.

---

## 4. Notes Techniques pour les Développeurs

* **Gestion de l'Argent :** Ne jamais utiliser `Double` ou `Float` pour les montants financiers (`prix`, `solde`). Toujours utiliser `java.math.BigDecimal` pour éviter les erreurs d'arrondi flottant.
* **Dates :** Utiliser l'API `java.time` (`LocalDate`, `LocalDateTime`, `LocalTime`) et jamais `java.util.Date`.
* **Stockage des icônes :** La liste `unlockedIcons` doit être gérée via une `@ElementCollection` en JPA ou stockée sous forme de JSON stringifié si la base de données ne supporte pas les tableaux natifs.
* **Sécurité :** L'accès à l'entité `InfoBureau` et aux fonctionnalités d'administration (validation staff, création événement) doit être protégé par un rôle Spring Security (`ROLE_ADMIN`).
