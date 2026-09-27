package com.fluxbank.commons_core.utils;


import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;

public final class DocumentUtils {

    private static final CPFValidator cpfValidator = new CPFValidator();
    private static  final CNPJValidator cnpjValidator = new CNPJValidator();

    public static Boolean isCpf(String document) {
        return cpfValidator.isEligible(document);
    }

    public static Boolean isCnpj(String document) {
        return cnpjValidator.isEligible(document);
    }

    public static Boolean isValid(String document) {
        if (document == null) {
            return false;
        }
        try {

            if (isCnpj(document)) {
                cnpjValidator.assertValid(document);
            }

            if (isCpf(document)) {
                cpfValidator.assertValid(document);
            }

            return true;
        } catch (InvalidStateException e) {
            return false;
        }
    }

}
