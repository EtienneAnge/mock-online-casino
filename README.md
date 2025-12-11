# CasYnoRoyale

Une application web de casino en ligne construite avec **Spring Boot**, permettant aux utilisateurs de jouer à des jeux de hasard comme le Blackjack et la Roulette.

## Table des matières

- [Vue d'ensemble](#vue-densemble)
- [Fonctionnalités](#fonctionnalités)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration](#configuration)
- [Structure du projet](#structure-du-projet)
- [Architecture](#architecture)
- [Technologies utilisées](#technologies-utilisées)
- [Utilisation](#utilisation)
- [API Endpoints](#api-endpoints)

## Vue d'ensemble

CasYnoRoyale est une plateforme de casino en ligne multiplayer où les utilisateurs peuvent :
- Créer un compte et se connecter
- Rejoindre des salles de jeu
- Jouer au Blackjack et à la Roulette
- Suivre leurs statistiques et transactions
- Gérer leur compte utilisateur

## Fonctionnalités

- **Authentification sécurisée** : Système de login/signup avec gestion des sessions
- **Jeux de casino** :
  - **Blackjack** : Jeu classique avec plusieurs sièges
  - **Roulette** : Jeu de roulette avec système de paris
- **Salles de jeu** : Créez et rejoignez des salles avec codes d'accès
- **Gestion des paris** : Système de verrouillage des paris
- **Compte utilisateur** : Suivi des statistiques et historique des transactions
- **Base de données H2** : Base de données en mémoire pour le développement
- **Sécurité** : Filtrage admin, intercepteurs de session

## Prérequis

- **Java 17** ou supérieur
- **Maven 3.6+**
- Un navigateur web moderne (Chrome, Firefox, Safari, Edge)

## Installation

1. **Clonez le repository** :
   ```bash
   git clone https://github.com/CasYnoRoyal/CasYnoRoyale.git
   cd CasYnoRoyale
   ```
2. **Construisez le projet** avec Maven :
   ```bash
   mvn clean install
   ```
3. **Lancez l'application** :
   ```bash
   mvn spring-boot:run
   ```
4. Ouvrez votre navigateur et allez à l'adresse `http://localhost:8080`

## Configuration

- **Fichier de configuration** : `src/main/resources/application.properties`
- **Port par défaut** : 8080 (modifiable dans le fichier de configuration)
- **Base de données** : Configurée pour utiliser H2 en mémoire par défaut

## Structure du projet

- `src/main/java` : Contient le code source Java
- `src/main/resources` : Contient les fichiers de configuration et les ressources statiques
- `src/test/java` : Contient les tests unitaires et d'intégration

## Architecture

L'architecture de CasYnoRoyale est basée sur le modèle MVC (Modèle-Vue-Contrôleur) :
- **Modèle** : Représente les données et la logique métier (ex. : entités JPA, services)
- **Vue** : Représente l'interface utilisateur (ex. : fichiers HTML, CSS, JavaScript)
- **Contrôleur** : Gère les requêtes HTTP et dirige vers les services appropriés

## Technologies utilisées

- **Spring Boot** : Framework principal pour construire l'application
- **Spring Security** : Pour la gestion de la sécurité et des utilisateurs
- **Spring Data JPA** : Pour l'accès et la gestion des données
- **Thymeleaf** : Moteur de templates pour rendre les vues HTML
- **Bootstrap** : Pour le design et la mise en page réactive
- **H2 Database** : Base de données en mémoire pour le développement et les tests

## Utilisation

- **Créer un compte** : Remplissez le formulaire d'inscription avec un nom d'utilisateur, un email et un mot de passe
- **Se connecter** : Utilisez vos identifiants pour vous connecter
- **Rejoindre une salle** : Entrez le code d'accès de la salle pour y participer
- **Jouer** : Sélectionnez un jeu (Blackjack ou Roulette) et commencez à jouer
- **Consulter ses statistiques** : Allez dans la section "Mon compte" pour voir vos statistiques et transactions

## API Endpoints

- **POST /api/auth/signup** : Inscription d'un nouvel utilisateur
- **POST /api/auth/login** : Authentification d'un utilisateur
- **GET /api/games/blackjack** : Récupérer les données du jeu Blackjack
- **GET /api/games/roulette** : Récupérer les données du jeu Roulette
- **POST /api/games/bet** : Placer un pari sur un jeu
- **GET /api/user/stats** : Récupérer les statistiques de l'utilisateur connecté
- **GET /api/user/transactions** : Récupérer l'historique des transactions de l'utilisateur connecté

