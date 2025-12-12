let roomId = null;         // Sera initialisé par initGame()
let refreshInterval = null;

/**
 * Point d'entrée du script.
 * Appelée depuis le HTML pour injecter le code de la salle généré par Thymeleaf.
 * @param {string} code - Le code crypté de la salle (ex: "L0V773").
 */
function initGame(code) {
    console.log("Démarrage du jeu pour la room :", code);
    roomId = code;
    startPolling();
}

/**
 * Démarre la boucle de rafraîchissement (Polling).
 * Interroge le serveur toutes les secondes pour obtenir l'état du jeu.
 */
function startPolling() {
    refreshData();
    if (refreshInterval) clearInterval(refreshInterval);
    refreshInterval = setInterval(refreshData, 1000);
}

/**
 * Récupère les données du jeu depuis l'API et met à jour l'interface.
 */
async function refreshData() {
    if (!roomId) return;

    try {
        const response = await fetch('/api/game/blackjack/refreshData', {
            method: 'POST', 
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(roomId) 
        });
        
        if (response.ok) {
            const data = await response.json();
            renderGame(data);
        }
    } catch (e) { 
        console.error("Erreur polling :", e); 
    }
}

/**
 * Met à jour le DOM en fonction de l'état du jeu reçu (JSON).
 * @param {Object} data - L'objet gameState renvoyé par le serveur.
 */
function renderGame(data) {
    // Mise à jour des infos globales
    const balanceEl = document.getElementById('balanceDisplay');
    if(balanceEl) balanceEl.textContent = data.userBalance;
    
    document.getElementById('gameStatus').textContent = "État : " + data.status;
    
    // Timer
    const timerDiv = document.getElementById('timerDisplay');
    if (data.status === 'BETTING') {
        timerDiv.textContent = data.timer + " s";
        timerDiv.style.color = data.timer < 5 ? '#ff5252' : '#ffca28';
    } else {
        timerDiv.textContent = ""; 
    }

    // Affichage du Croupier
    const dealerDiv = document.getElementById('dealerCards');
    dealerDiv.innerHTML = '';
    data.dealer.cards.forEach(c => dealerDiv.appendChild(createCardImg(c)));
    if (data.status === 'PLAYER_TURN') {
        const backImg = document.createElement('img');
        backImg.className = 'card-img';
        backImg.src = '/img/card/back.png'; 
        dealerDiv.appendChild(backImg);
    }
    document.getElementById('dealerScore').textContent = "(" + data.dealer.score + ")";

    // Affichage des Joueurs
    const playersDiv = document.getElementById('playersContainer');
    playersDiv.innerHTML = '';
    
    let iHaveBet = false; 

    data.players.forEach(p => {
        if (p.isMe) iHaveBet = true;
        
        const seat = document.createElement('div');
        seat.className = `player-seat ${p.isMe ? 'my-seat' : ''} ${p.isTurn ? 'active-turn' : ''}`;
        
        let html = `<div><strong>${p.name}</strong></div>`;
        html += `<div style="font-size:0.9em; opacity:0.8;">Mise: ${p.bet}€</div>`;
        
        html += `<div class="cards-container">`;
        p.cards.forEach(c => {
            const src = '/img/card/' + (c.imagePath ? c.imagePath : (c.rank + '_' + c.suit + '.png'));
            html += `<img src="${src}" class="card-img" alt="${c.rank}">`;
        });
        html += `</div>`;
        
        html += `<div>Score: ${p.score}</div>`;
        html += `<div style="font-weight:bold; color:${getStatusColor(p.status)}">${p.status}</div>`;
        
        seat.innerHTML = html;
        playersDiv.appendChild(seat);
    });

    // Contrôles
    renderControls(data, iHaveBet);
}

/**
 * Gère l'affichage dynamique des boutons (Miser, Hit, Stand).
 * @param {Object} data - Données du jeu.
 * @param {boolean} iHaveBet - Indique si le joueur courant a déjà misé.
 */
function renderControls(data, iHaveBet) {
    const controls = document.getElementById('controlsBar');

    // Phase de Paris
    if (data.status === 'BETTING') {
        if (!iHaveBet && data.timer > 0) {
            if (!document.getElementById('betInput')) {
                controls.innerHTML = `
                    <input type="number" id="betInput" value="10" min="10" placeholder="Mise">
                    <button class="btn-bet" onclick="placeBet()">MISER</button>
                `;
            }
        } else {
            controls.innerHTML = `<span style="color:white; font-style:italic;">En attente des autres joueurs...</span>`;
        }
    } 
    // Tour de Jeu
    else if (data.status === 'PLAYER_TURN') {
        if (data.isMyTurn) {
            if (!document.querySelector('.btn-hit')) {
                controls.innerHTML = `
                    <button class="btn-hit" onclick="actionHit()">TIRER (HIT)</button>
                    <button class="btn-stand" onclick="actionStand()">RESTER (STAND)</button>
                `;
            }
        } else {
            controls.innerHTML = `<span style="color:#aaa">Tour d'un autre joueur...</span>`;
        }
    }
    // Fin de manche
    else if (data.status === 'FINISHED') {
            controls.innerHTML = `<span style="color:#ffca28; font-weight:bold">Manche terminée</span>`;
    }
}

/**
 * Crée un élément DOM <img> pour une carte donnée.
 * @param {Object} card - L'objet carte contenant rank, suit ou imagePath.
 * @returns {HTMLImageElement} L'élément image prêt à être inséré.
 */
function createCardImg(card) {
    const img = document.createElement('img');
    img.className = 'card-img';
    img.src = '/img/card/' + (card.imagePath ? card.imagePath : (card.rank + '_' + card.suit + '.png'));
    return img;
}

/**
 * Détermine la couleur du texte de statut en fonction du résultat.
 * @param {string} status - Le statut du joueur (WON, LOST, PUSH...).
 * @returns {string} Le code couleur hexadécimal ou le nom CSS.
 */
function getStatusColor(status) {
    if(status.includes('WON') || status.includes('BLACKJACK')) return '#69f0ae';
    if(status === 'LOST' || status === 'BUSTED') return '#ff5252';
    if(status === 'PUSH') return '#ffca28';
    return 'white';
}

/**
 * Envoie une requête pour placer une mise.
 * Effectue une validation côté client avant l'envoi pour économiser une requête.
 */
async function placeBet() {
    const input = document.getElementById('betInput');
    if (!input) return; 
    
    const amount = parseInt(input.value);
    const statusDiv = document.getElementById('gameStatus');

    if (isNaN(amount) || amount <= 0) {
        alert("La mise doit être supérieure à 0 !");
        return;
    }

    statusDiv.style.color = 'white';

    try {
        const response = await fetch('/api/game/blackjack/bet', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({idRoom: roomId, amount: amount})
        });

        if (response.ok) {
            refreshData();
        } else {
            const errorData = await response.json().catch(() => ({}));
            if (response.status === 400 || (errorData.message && errorData.message.includes("Solde"))) {
                alert("Solde insuffisant !");
            } else {
                alert("Erreur : " + (errorData.message || "Impossible de miser"));
            }
        }
    } catch (e) {
        console.error(e);
        alert("Erreur réseau.");
    }
}

/**
 * Envoie l'action "Tirer une carte" (Hit) au serveur.
 */
async function actionHit() {
    try {
        await fetch('/api/game/blackjack/hit', {
            method: 'POST', headers: {'Content-Type': 'application/json'}, body: roomId
        });
        refreshData();
    } catch (e) { console.error(e); }
}

/**
 * Envoie l'action "Rester" (Stand) au serveur.
 */
async function actionStand() {
    try {
        await fetch('/api/game/blackjack/stand', {
            method: 'POST', headers: {'Content-Type': 'application/json'}, body: roomId
        });
        refreshData();
    } catch (e) { console.error(e); }
}

/**
 * Quitte la salle, notifie le serveur et redirige l'utilisateur vers l'accueil.
 */
async function exitRoom() {
    try {
        await fetch('/api/game/blackjack/exit', {
            method: 'POST', headers: {'Content-Type': 'text/plain'}, body: roomId 
        });
        window.location.href = "/";
    } catch (e) {
        console.error("Erreur sortie:", e);
        window.location.href = "/";
    }
}