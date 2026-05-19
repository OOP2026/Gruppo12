package model;

import java.time.LocalDateTime;

public class Intervento extends Prestazione {
    private Integer salaOperatoria;

    public Intervento (Integer numPrestazione, LocalDateTime dataInizio, LocalDateTime dataFine, String esito, Integer salaOperatoria)
    {
        super(numPrestazione, dataInizio, dataFine, esito);
        this.salaOperatoria = salaOperatoria;
    }

    public Integer getSalaOperatoria() {
        return salaOperatoria;
    }

    public void setSalaOperatoria(Integer salaOperatoria) {
        this.salaOperatoria = salaOperatoria;
    }
}


