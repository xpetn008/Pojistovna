package org.example.models.services;

import org.example.data.entitites.PojisteniEntity;
import org.example.models.dto.PojistenecDTO;
import org.example.models.dto.PojisteniDTO;

public interface PojisteniService {
    void create(PojisteniDTO pojisteniDTO);
    String vratSoucetMesicnichSplatek(PojistenecDTO pojistenecDTO);
    PojisteniEntity vratPojisteniPodleId(Long id);
    void update(PojisteniDTO pojisteniDTO);
    void nactiVsechnaPojisteni(PojisteniDTO pojisteniDTO);
    boolean nabidkaJePrazdna();
}
