
// --- CONFIGURATION ---
const board = document.getElementById('board');
const redNumbers = [1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36];
let currentBets = [];
const urlParams = new URLSearchParams(window.location.search);
const idRoom = urlParams.get('idRoom');

// --- GÉNÉRATION DU PLATEAU (GRID) ---
// La grille fait 12 rangées de hauteur (36 numéros / 3 colonnes)

for (let row = 0; row < 12; row++) {
    // Le numéro de départ de la rangée (ex: 1, 4, 7...)
    let numStart = (row * 3) + 1;

    // 1. COLONNE "CHANCES SIMPLES" (Tout à gauche) - Prend 2 rangées de hauteur à chaque fois
    // Ordre classique tapis : 1-18, Pair, Rouge, Noir, Impair, 19-36
    if (row % 2 === 0) {
        let sideBetDiv = document.createElement('div');
        sideBetDiv.className = 'cell side-bet span-row-2 drop-zone';
        
        if(row === 0) { setText(sideBetDiv, "1 - 18"); setBetData(sideBetDiv, "LOW_HIGH", 0); }
        if(row === 2) { setText(sideBetDiv, "PAIR");   setBetData(sideBetDiv, "PARITY", 0); }
        if(row === 4) { 
            sideBetDiv.innerHTML = "<div class='diamond-red'></div>"; 
            setBetData(sideBetDiv, "COLOR", 0); // 0 = Rouge
        }
        if(row === 6) { 
            sideBetDiv.innerHTML = "<div class='diamond-black'></div>"; 
            setBetData(sideBetDiv, "COLOR", 1); // 1 = Noir
        }
        if(row === 8) { setText(sideBetDiv, "IMPAIR"); setBetData(sideBetDiv, "PARITY", 1); }
        if(row === 10){ setText(sideBetDiv, "19 - 36");setBetData(sideBetDiv, "LOW_HIGH", 1); }

        board.appendChild(sideBetDiv);
    }

    // 2. COLONNE "DOUZAINES" - Prend 4 rangées
    if (row % 4 === 0) {
        let dozenDiv = document.createElement('div');
        dozenDiv.className = 'cell side-bet span-row-4 drop-zone';
        if(row === 0) { setText(dozenDiv, "1ère 12"); setBetData(dozenDiv, "DOZEN", 0); }
        if(row === 4) { setText(dozenDiv, "2ème 12"); setBetData(dozenDiv, "DOZEN", 1); }
        if(row === 8) { setText(dozenDiv, "3ème 12"); setBetData(dozenDiv, "DOZEN", 2); }
        board.appendChild(dozenDiv);
    }

    // 3. LES 3 NUMÉROS DE LA RANGÉE
    for (let i = 0; i < 3; i++) {
        let currentNum = numStart + i;
        let numDiv = document.createElement('div');
        numDiv.className = 'cell drop-zone';
        numDiv.innerText = currentNum;
        
        if (redNumbers.includes(currentNum)) numDiv.classList.add('num-red');
        else numDiv.classList.add('num-black');

        setBetData(numDiv, "STRAIGHT_UP", currentNum); // Val = le numéro lui-même
        
        board.appendChild(numDiv);
    }
}

// 4. LIGNE DU BAS : LES COLONNES (2 TO 1)
// On ajoute 2 cellules vides pour l'alignement à gauche
board.appendChild(createEmpty()); 
board.appendChild(createEmpty());

for(let c = 0; c < 3; c++) {
    let colDiv = document.createElement('div');
    colDiv.className = 'cell side-bet drop-zone';
    colDiv.innerText = "2 pour 1";
    // Colonne 1 (1,4,7..) est l'index 0 dans l'enum Java
    setBetData(colDiv, "COLUMN", c); 
    board.appendChild(colDiv);
}

// --- UTILITAIRES ---
function setText(el, text) { el.innerText = text; }
function createEmpty() { let d = document.createElement('div'); d.style.border='none'; return d; }

// Configure les attributs data pour correspondre à l'Enum Java
function setBetData(element, type, val) {
    element.dataset.type = type; // ex: COLOR
    element.dataset.val = val;   // ex: 0
}

// --- LOGIQUE DRAG & DROP ---
let draggedAmount = 0;
let draggedClass = "";

// 1. Initialiser les jetons
document.querySelectorAll('.chip').forEach(chip => {
    chip.addEventListener('dragstart', (e) => {
        draggedAmount = parseInt(e.target.dataset.amount);
        draggedClass = Array.from(e.target.classList).find(c => c.startsWith('chip-'));
        e.target.style.opacity = "0.5";
    });
    chip.addEventListener('dragend', (e) => e.target.style.opacity = "1");
});

// 2. Initialiser les zones de dépôt (les cellules créées dynamiquement + le 0 statique)
// On utilise un setTimeout pour s'assurer que le DOM est généré
setTimeout(() => {
    document.querySelectorAll('.drop-zone').forEach(zone => {
        zone.addEventListener('dragover', (e) => e.preventDefault()); // Autoriser le drop
        
        zone.addEventListener('dragenter', (e) => {
            e.target.style.boxShadow = "inset 0 0 10px gold";
        });
        
        zone.addEventListener('dragleave', (e) => {
            e.target.style.boxShadow = "none";
        });

        zone.addEventListener('drop', (e) => {
            e.preventDefault();
            e.target.style.boxShadow = "none";
            
            // Récupérer les infos du pari
            let betType = e.target.dataset.type;
            let betVal = e.target.dataset.val;

            if(!betType) return; // Sécurité

            placeBet(betType, betVal, draggedAmount);

            // Ajouter visuel
            let visualChip = document.createElement('div');
            visualChip.className = `chip placed-chip ${draggedClass}`;
            visualChip.innerText = draggedAmount;
            // Position aléatoire légère
            let rx = Math.floor(Math.random() * 10) - 5;
            let ry = Math.floor(Math.random() * 10) - 5;
            visualChip.style.transform = `translate(${rx}px, ${ry}px)`;
            
            e.target.appendChild(visualChip);
        });
    });
}, 100);

function placeBet(type, val, amount) {
    currentBets.push({
        betType: type,          // Pour mapper avec Enum Java
        selectionValue: parseInt(val), // 0 ou 1, ou le numéro
        amount: amount
    });
     // C'est ici que tu appelleras ton API Spring Boot
        fetch('/api/game/routelle/lockBet', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(currentBets)
    }).then(response => response.json()) // On transforme la réponse en objet JS
.then(data => {

        refreshData();
    
       
   
    }).catch(error => console.error('Erreur:', error));

    console.log("Pari ajouté : ", currentBets[currentBets.length-1]);
}



function clearBets() {
    currentBets = [];
    refreshData();
    document.querySelectorAll('.placed-chip').forEach(c => c.remove());
}

function cancelBets() {
    
    fetch('/api/game/routelle/betcanceled', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(idRoom)
    }).then(response => response.json()).then(data => {
        if(data.message){
     clearBets();
     console.log("paris annulés");

        }
    });
}

function refreshData(){
    fetch('/api/game/roulette/refreshData', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(idRoom)
    }).then(response => response.json()).then(data => {


        // 1. Mise à jour du solde (déjà existant)
        if(data.nouveauSolde !== undefined) {
             document.getElementById('mon-solde').innerText = data.nouveauSolde;
        }

        // 2. Mise à jour de l'historique
        if (data.historique) {
            updateHistoryUI(data.historique);
        }
    })
    .catch(error => console.error('Erreur:', error));
  
}

document.getElementById("exitButton").addEventListener('click',()=>{
    if(window.confirm("Voulez-vous vraiment quitter la table ?")){
        fetch(`/api/game/roulette/exit?idRoom=${idRoom}`,{
            method:'POST',
        }).then(response=>{
            if(response.ok){
                window.location.href="/";
            }
        });
    }
});

document.getElementById("spinButton").addEventListener('click',() => {
    fetch('/api/game/roulette/tirer', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'}
    }).then(response => response.json()).then(data => {
        refreshData();
    });
});
    

function annimationRoulette(){
    // Animation de la roue (Simulation)
    let wheel = document.getElementById('roulettePng');
    let currentRot = parseFloat(wheel.style.transform.replace(/[^0-9.]/g, '') || 0);
    wheel.style.transform = `rotate(${currentRot + 720 + Math.random()*360}deg)`;

}

// scriptRoulette.js

// Fonction utilitaire pour connaître la couleur d'un numéro
function getNumberColor(number) {
    const redNumbers = [1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36];
    
    if (number === 0) return 'ball-green';
    if (redNumbers.includes(number)) return 'ball-red';
    return 'ball-black';
}

function exitRoom(){
    if(window.confirm("Souhaitez-vous vraiment quitter la table ?")){
        window.open("/","Merci d'avoir joué !");
    }
}


function updateHistoryUI(historiqueNumbers) {
    const container = document.getElementById('history-container');
    container.innerHTML = ''; // On vide l'historique actuel pour le recréer proprement

    // On parcourt la liste reçue du serveur
    // On peut inverser la boucle si on veut le plus récent en premier, 
    // ou laisser tel quel selon le CSS (flex-direction: row-reverse)
    historiqueNumbers.forEach(num => {
        const ball = document.createElement('div');
        
        // Ajout des classes CSS
        ball.classList.add('history-ball');
        ball.classList.add(getNumberColor(num)); // Ajoute ball-red, ball-black ou ball-green
        
        ball.innerText = num;
        
        // Ajout au conteneur
        container.appendChild(ball);
    });
}

// Appeler refreshData au chargement de la page pour voir l'historique existant
document.addEventListener('DOMContentLoaded', () => {
    refreshData();
    // Optionnel : Lancer un rafraichissement automatique toutes les 5 secondes
    // setInterval(refreshData, 5000); 
});