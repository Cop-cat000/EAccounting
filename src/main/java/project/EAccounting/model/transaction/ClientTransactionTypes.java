package project.EAccounting.model.transaction;

import project.EAccounting.persistence.entities.datatypes.TransactionTypes;

public enum ClientTransactionTypes implements ConverterToTransactionTypes {
    PAYMENT {
        public TransactionTypes convert() {
            return TransactionTypes.PAYMENT;
        }
    }, TRANSFER {
        public TransactionTypes convert() {
            return TransactionTypes.TRANSFER;
        }
    }, UP {
        public TransactionTypes convert() {
            return TransactionTypes.UP;
        }
    }, BETWEEN {
        public TransactionTypes convert() {
            return TransactionTypes.BETWEEN;
        }
    }, CASH_WITHDRAWAL {
        public TransactionTypes convert() {
            return TransactionTypes.CASH_WITHDRAWAL;
        }
    };
}
