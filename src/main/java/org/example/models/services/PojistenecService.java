package org.example.models.services;

import org.example.data.entitites.PojistenecEntity;
import org.example.models.dto.PojistenecDTO;
import org.example.models.exceptions.DuplicateRodneCisloException;
import org.example.models.exceptions.FalseRCException;
import org.example.models.exceptions.SpatnyUdajException;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PojistenecService{
    List<Exception> create(PojistenecDTO pojistenecDTO) throws DuplicateRodneCisloException, FalseRCException, SpatnyUdajException;
    void savePojistenecToUser(PojistenecDTO pojistenecDTO);
    boolean dontHavePojistenec(Authentication authentication);
    void update(PojistenecDTO pojistenecDTO) throws SpatnyUdajException;
    void delete(PojistenecDTO pojistenecDTO);
    void nactiUdaje(PojistenecDTO pojistenecDTO);
    void deletePojisteniFromPojistenec(PojistenecDTO pojistenecDTO);
    void addPojisteniToPojistenec(PojistenecDTO pojistenecDTO);
    void deletePojistenec(PojistenecDTO pojistenecDTO);
    void nactiNabidkuPojisteni(PojistenecDTO pojistenecDTO);
    boolean haveThisPojisteni(PojistenecDTO pojistenecDTO);
    long returnPojistenecId(PojistenecDTO pojistenecDTO);
    PojistenecEntity vratPojistencePodleId(long id);
    void adminUpdate(PojistenecDTO pojistenecDTO) throws SpatnyUdajException;
    void nactiPojistence(PojistenecDTO pojistenecDTO);
    void validateRC(String input) throws FalseRCException;
    void overUdaje(String column, String udaj) throws SpatnyUdajException;
}
