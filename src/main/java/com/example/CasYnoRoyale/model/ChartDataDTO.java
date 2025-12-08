package com.example.CasYnoRoyale.model;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChartDataDTO {
    private List<String> labels;        //Axe X (Dates)
    private List<BigDecimal> data;      //Axe Y (Solde)

    private BigDecimal totalGains;      //Total des gains
    private BigDecimal totalLosses;     //Total des pertes
    private BigDecimal totalBalance;    //Solde total
}