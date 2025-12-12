const board = document.getElementById('board');
const redNumbers = [1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36];
let currentBets = [];
const urlParams = new URLSearchParams(window.location.search);
const idRoom = urlParams.get('idRoom');
const idRoomSpan = document.getElementById("idRoom");
idRoomSpan.innerHTML=idRoom;
let moneyRemaining = 0;

let timerInterval = null; 

refreshData();
function startTimer(targetIsoDate) {

    if (timerInterval) clearInterval(timerInterval);

    const timerElement = document.getElementById('timer');
    const targetTime = new Date(targetIsoDate).getTime();

    const update = () => {
        const now = new Date().getTime();
        const distance = targetTime - now;

        if (distance <= 0) {
            clearInterval(timerInterval);
            timerElement.innerText = "00:00";
            timerElement.classList.add('timer-urgent'); 

            refreshData();

            return;
        }

        const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((distance % (1000 * 60)) / 1000);

        timerElement.innerText = 
            (minutes < 10 ? "0" + minutes : minutes) + ":" + 
            (seconds < 10 ? "0" + seconds : seconds);

        if (distance < 10000) {
            timerElement.classList.add('timer-urgent');
        } else {
            timerElement.classList.remove('timer-urgent');
        }
    };

    update(); 
    timerInterval = setInterval(update, 1000);
}

for (let row = 0; row < 12; row++) {

    let numStart = (row * 3) + 1;

    if (row % 2 === 0) {
        let sideBetDiv = document.createElement('div');
        sideBetDiv.className = 'cell side-bet span-row-2 drop-zone';

        if(row === 0) { setText(sideBetDiv, "1 - 18"); setBetData(sideBetDiv, "LOW_HIGH", 0); }
        if(row === 2) { setText(sideBetDiv, "PAIR");   setBetData(sideBetDiv, "PARITY", 0); }
        if(row === 4) { 
            sideBetDiv.innerHTML = "<div class='diamond-red'></div>"; 
            setBetData(sideBetDiv, "COLOR", 0); 

        }
        if(row === 6) { 
            sideBetDiv.innerHTML = "<div class='diamond-black'></div>"; 
            setBetData(sideBetDiv, "COLOR", 1); 

        }
        if(row === 8) { setText(sideBetDiv, "IMPAIR"); setBetData(sideBetDiv, "PARITY", 1); }
        if(row === 10){ setText(sideBetDiv, "19 - 36");setBetData(sideBetDiv, "LOW_HIGH", 1); }

        board.appendChild(sideBetDiv);
    }

    if (row % 4 === 0) {
        let dozenDiv = document.createElement('div');
        dozenDiv.className = 'cell side-bet span-row-4 drop-zone';
        if(row === 0) { setText(dozenDiv, "1ère 12"); setBetData(dozenDiv, "DOZEN", 0); }
        if(row === 4) { setText(dozenDiv, "2ème 12"); setBetData(dozenDiv, "DOZEN", 1); }
        if(row === 8) { setText(dozenDiv, "3ème 12"); setBetData(dozenDiv, "DOZEN", 2); }
        board.appendChild(dozenDiv);
    }

    for (let i = 0; i < 3; i++) {
        let currentNum = numStart + i;
        let numDiv = document.createElement('div');
        numDiv.className = 'cell drop-zone';
        numDiv.innerText = currentNum;

        if (redNumbers.includes(currentNum)) numDiv.classList.add('num-red');
        else numDiv.classList.add('num-black');

        setBetData(numDiv, "STRAIGHT_UP", currentNum); 

        board.appendChild(numDiv);
    }
}

board.appendChild(createEmpty()); 
board.appendChild(createEmpty());

for(let c = 0; c < 3; c++) {
    let colDiv = document.createElement('div');
    colDiv.className = 'cell side-bet drop-zone';
    colDiv.innerText = "2 pour 1";

    setBetData(colDiv, "COLUMN", c); 
    board.appendChild(colDiv);
}

function setText(el, text) { el.innerText = text; }
function createEmpty() { let d = document.createElement('div'); d.style.border='none'; return d; }

function setBetData(element, type, val) {
    element.dataset.type = type; 

    element.dataset.val = val;   

}

let draggedAmount = 0;
let draggedClass = "";

document.querySelectorAll('.chip').forEach(chip => {
    chip.addEventListener('dragstart', (e) => {
        draggedAmount = parseInt(e.target.dataset.amount);
        draggedClass = Array.from(e.target.classList).find(c => c.startsWith('chip-'));
        e.target.style.opacity = "0.5";
    });
    chip.addEventListener('dragend', (e) => e.target.style.opacity = "1");
});

setTimeout(() => {
    document.querySelectorAll('.drop-zone').forEach(zone => {
        zone.addEventListener('dragover', (e) => e.preventDefault()); 

        zone.addEventListener('dragenter', (e) => {
            e.target.style.boxShadow = "inset 0 0 10px gold";
        });

        zone.addEventListener('dragleave', (e) => {
            e.target.style.boxShadow = "none";
        });

        zone.addEventListener('drop', (e) => {
            e.preventDefault();
            e.target.style.boxShadow = "none";

            let betType = e.target.dataset.type;
            let betVal = e.target.dataset.val;

            if(!betType) return; 

            if(draggedAmount > moneyRemaining) return;
            placeBet(betType, betVal, draggedAmount);

            let visualChip = document.createElement('div');
            visualChip.className = `chip placed-chip ${draggedClass}`;
            visualChip.innerText = draggedAmount;

            let rx = Math.floor(Math.random() * 10) - 5;
            let ry = Math.floor(Math.random() * 10) - 5;
            visualChip.style.transform = `translate(${rx}px, ${ry}px)`;

            e.target.appendChild(visualChip);
        });
    });
}, 100);

function placeBet(type, val, amount) {
    currentBets = [];
    currentBets.push({
        betType: type,          

        selectionValue: parseInt(val), 

        amount: amount
    });
    const requestBody = {
        idRoom:idRoom,
        bets:currentBets
    }
    fetch("/api/game/roulette/lockBet", {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(requestBody)
    }).then(response => response.json())
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

    fetch('/api/game/roulette/betcanceled', {
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

        if(data.nouveauSolde !== undefined) {
             document.getElementById('mon-solde').innerText = data.nouveauSolde;
             moneyRemaining = data.nouveauSolde;
        }

        if (data.historique) {
            updateHistoryUI(data.historique);
        }

        if (data.prochainTirage) {

            startTimer(data.prochainTirage);
        }

        if(data.tirage){
            tirage(data.tirage);
        }
    })
    .catch(error => console.error('Erreur:', error));

}

function tirage(t){
    annimationRoulette();
    clearBets();
    refreshData();
}

document.getElementById("exitButton").addEventListener('click',()=>{
    if(window.confirm("Voulez-vous vraiment quitter la table ?")){
        fetch(`/api/game/roulette/exit`,{
            method:'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(idRoom)
        }).then(response=>{
            if(response.ok){
                window.location.href="/";
            }
        });
    }
});

document.getElementById("spinButton").addEventListener('click',() => {
    fetch('/api/game/roulette/refreshData', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(idRoom)
    }).then(response => response.json()).then(data => {
        refreshData();
    });
});

function annimationRoulette(){

    let wheel = document.getElementById('roulettePng');
    let currentRot = parseFloat(wheel.style.transform.replace(/[^0-9.]/g, '') || 0);
    wheel.style.transform = `rotate(${currentRot + 720 + Math.random()*360}deg)`;

}

function getNumberColor(number) {
    const redNumbers = [1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36];

    if (number === 0) return 'ball-green';
    if (redNumbers.includes(number)) return 'ball-red';
    return 'ball-black';
}

function updateHistoryUI(historiqueNumbers) {
    const container = document.getElementById('history-container');
    container.innerHTML = ''; 

    historiqueNumbers.forEach(num => {
        const ball = document.createElement('div');

        ball.classList.add('history-ball');
        ball.classList.add(getNumberColor(num)); 

        ball.innerText = num;

        container.appendChild(ball);
    });
}

document.addEventListener('DOMContentLoaded', () => {
    refreshData();
    addCodeRoom();

});