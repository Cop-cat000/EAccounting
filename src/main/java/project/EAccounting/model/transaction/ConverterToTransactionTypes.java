package project.EAccounting.model.transaction;

import project.EAccounting.persistence.entities.datatypes.TransactionTypes;

public interface ConverterToTransactionTypes {
    public TransactionTypes convert();
}
