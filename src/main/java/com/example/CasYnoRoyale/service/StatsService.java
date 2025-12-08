package com.example.CasYnoRoyale.service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.CasYnoRoyale.database.AppUser;
import com.example.CasYnoRoyale.database.Transaction;
import com.example.CasYnoRoyale.model.ChartDataDTO;
import com.example.CasYnoRoyale.repository.TransactionRepository;

@Service
public class StatsService {
    private final TransactionRepository transactionRepository;

    public StatsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public ChartDataDTO getEvolutvoidionData(AppUser user, Long gameId) {
        //Variables
        List<Transaction> transactions;                                 //Liste des transactions

        //Variables à renvoyer
        List<String> labels = new ArrayList<>();                        //Liste des labels (dates)
        List<BigDecimal> dataPoints = new ArrayList<>();                //Liste des points de données (solde)
        BigDecimal totalGains = BigDecimal.ZERO;                        //Total des gains
        BigDecimal totalLosses = BigDecimal.ZERO;                       //Total des pertes
        BigDecimal totalBalance = BigDecimal.ZERO;                      //Solde Total
        Map<String, BigDecimal> balanceByDate = new LinkedHashMap<>();  //Map date -> solde

        //Si le jeu n'est pas renseigné
        if (gameId == null) {
            //Toutes les transactions sont recupérées
            transactions = transactionRepository.findByUserOrderByDateAsc(user);
        } else { //Sinon
            //Les transactions du jeu spécifié sont recupérées
            transactions = transactionRepository.findByUserAndRoom_Game_IdGameOrderByDateAsc(user, gameId);
        }
        
        //Format dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        //Parcours des transactions
        for (Transaction transaction : transactions) {
            //Si la date et le montant ne sont pas nuls
            if (transaction.getDate() != null && transaction.getMontant() != null) {
                String dayKey = transaction.getDate().format(formatter);
                totalBalance = totalBalance.add(transaction.getMontant());

                //Si la date existe déjà, on ajoute au montant existant via la méthode merge
                //Sinon on initialise avec le montant
                balanceByDate.merge(dayKey, totalBalance, BigDecimal::add);

                //Calcul des gains et pertes
                totalGains = totalGains.add(transaction.getMontant().max(BigDecimal.ZERO));
                totalLosses = totalLosses.add(transaction.getMontant().min(BigDecimal.ZERO));
            }
        }

        //Listes des labels
        labels = new ArrayList<>(balanceByDate.keySet());
        //Liste des points de données
        dataPoints = new ArrayList<>(balanceByDate.values());

        //Retour des données (Format ChartDataDTO)
        return new ChartDataDTO(labels, dataPoints, totalGains, totalLosses, totalBalance);
    }
}
