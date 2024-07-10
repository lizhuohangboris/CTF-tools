package org.hibernate.validator.internal.constraintvalidators.bv.money;

import java.util.ArrayList;
import java.util.List;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.Currency;
import org.hibernate.validator.internal.util.CollectionHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/money/CurrencyValidatorForMonetaryAmount.class */
public class CurrencyValidatorForMonetaryAmount implements ConstraintValidator<Currency, MonetaryAmount> {
    private List<CurrencyUnit> acceptedCurrencies;

    @Override // javax.validation.ConstraintValidator
    public void initialize(Currency currency) {
        String[] value;
        List<CurrencyUnit> acceptedCurrencies = new ArrayList<>();
        for (String currencyCode : currency.value()) {
            acceptedCurrencies.add(Monetary.getCurrency(currencyCode, new String[0]));
        }
        this.acceptedCurrencies = CollectionHelper.toImmutableList(acceptedCurrencies);
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(MonetaryAmount value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return this.acceptedCurrencies.contains(value.getCurrency());
    }
}