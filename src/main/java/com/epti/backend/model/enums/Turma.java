package com.epti.backend.model.enums;

import lombok.Getter;

@Getter
public enum Turma {
    PRIMEIRO_A("1°A"),
    PRIMEIRO_B("1°B"),
    PRIMEIRO_C("1°C"),
    PRIMEIRO_D("1°D"),
    SEGUNDO_A("2°A"),
    SEGUNDO_B("2°B"),
    SEGUNDO_C("2°C"),
    SEGUNDO_D("2°D"),
    SEGUNDO_D_MARTA("2°D Marta Giffoni"),
    TERCEIRO_A("3°A"),
    TERCEIRO_B("3°B"),
    TERCEIRO_C("3°C"),
    TERCEIRO_D("3°D");

    private final String displayName;

    Turma(String displayName) {
        this.displayName = displayName;
    }

    public static Turma fromDisplayName(String displayName) {
        for (Turma turma : Turma.values()) {
            if (turma.getDisplayName().equals(displayName)) {
                return turma;
            }
        }
        throw new IllegalArgumentException("Turma não encontrada: " + displayName);
    }

    public static boolean isParticipatingTurma(Turma turma) {
        return turma == PRIMEIRO_D || 
               turma == SEGUNDO_A || 
               turma == SEGUNDO_D || 
               turma == SEGUNDO_D_MARTA;
    }
}
