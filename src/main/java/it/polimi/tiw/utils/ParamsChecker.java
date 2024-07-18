package it.polimi.tiw.utils;

public final class ParamsChecker {
    public static boolean checkParams(String param) {    //Ritorna true se il parametro è okay
        return (param!=null && !param.isEmpty());
    }
}
