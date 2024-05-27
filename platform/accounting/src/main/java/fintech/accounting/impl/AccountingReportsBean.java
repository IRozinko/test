package fintech.accounting.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.BigDecimalUtils;
import fintech.PojoUtils;
import fintech.Validate;
import fintech.accounting.AccountTrialBalance;
import fintech.accounting.AccountTurnover;
import fintech.accounting.AccountingReports;
import fintech.accounting.ReportQuery;
import fintech.accounting.db.AccountEntity;
import fintech.accounting.db.AccountRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fintech.BigDecimalUtils.amount;
import static fintech.accounting.db.Entities.account;
import static fintech.accounting.db.Entities.entry;

@Component
public class AccountingReportsBean implements AccountingReports {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    @Override
    public List<AccountTrialBalance> getTrialBalance(ReportQuery query) {
        Validate.notNull(query.getBookingDateFrom(), "Booking date from is required");
        Validate.notNull(query.getBookingDateTo(), "Booking date to is required");

        ReportQuery openingQuery = PojoUtils.cloneBean(query);
        openingQuery.setBookingDateFrom(null);
        openingQuery.setBookingDateTo(query.getBookingDateFrom().minusDays(1));
        List<AccountSums> openingSums = sum(openingQuery);
        List<AccountSums> turnoverSums = sum(query);

        List<AccountEntity> accounts = accountRepository.findAll(account.code.asc());
        List<AccountTrialBalance> balances = accounts.stream().map((account) -> {
            AccountSums opening = openingSums.stream().filter((it) -> it.accountId.equals(account.getId()))
                .findFirst().orElseGet(AccountSums::new);
            AccountSums turnover = turnoverSums.stream().filter((it) -> it.accountId.equals(account.getId()))
                .findFirst().orElseGet(AccountSums::new);
            return map(account, opening, turnover);
        })
            .filter((b) -> (!b.isEmpty() || b.getAccountCode().equals(query.getAccountCode())))
            .collect(Collectors.toList());
        return balances;
    }

    @Transactional
    @Override
    public Map<String, AccountTurnover> getTurnover(ReportQuery query) {
        Predicate[] predicates = queryToPredicates(query);
        List<Tuple> tuples = queryFactory
            .select(new Expression[]{
                entry.account.code,
                entry.debit.sum(),
                entry.credit.sum(),
            })
            .from(entry)
            .groupBy(entry.account.code)
            .where(predicates)
            .fetch();
        Map<String, AccountTurnover> turnovers = tuples.stream().map((t) -> {
            AccountTurnover turnover = new AccountTurnover();
            turnover.setCode(t.get(entry.account.code));
            turnover.setDebit(t.get(entry.debit.sum()));
            turnover.setCredit(t.get(entry.credit.sum()));
            return turnover;
        }).collect(Collectors.toMap(AccountTurnover::getCode, b -> b));
        return turnovers;
    }

    private AccountTrialBalance map(AccountEntity account, AccountSums opening, AccountSums turnover) {
        AccountTrialBalance balance = new AccountTrialBalance();
        balance.setAccountCode(account.getCode());
        balance.setAccountName(account.getName());

        BigDecimal openingSum = opening.getDebit().subtract(opening.getCredit());
        balance.setOpeningDebit(BigDecimalUtils.isPositive(openingSum) ? openingSum : amount(0));
        balance.setOpeningCredit(BigDecimalUtils.isNegative(openingSum) ? openingSum.abs() : amount(0));

        balance.setTurnoverDebit(turnover.getDebit());
        balance.setTurnoverCredit(turnover.getCredit());

        BigDecimal closingSum = opening.getDebit()
            .add(turnover.getDebit()).subtract(opening.getCredit()).subtract(turnover.getCredit());
        balance.setClosingDebit(BigDecimalUtils.isPositive(closingSum) ? closingSum : amount(0));
        balance.setClosingCredit(BigDecimalUtils.isNegative(closingSum) ? closingSum.abs() : amount(0));
        return balance;
    }

    private List<AccountSums> sum(ReportQuery query) {
        return queryFactory
            .select(
                Projections.bean(AccountSums.class,
                    entry.account.id.as("accountId"),
                    entry.debit.sum().as("debit"),
                    entry.credit.sum().as("credit"))
            )
            .from(entry)
            .where(queryToPredicates(query))
            .groupBy(entry.account.id).fetch();


    }

    private Predicate[] queryToPredicates(ReportQuery query) {
        List<Predicate> predicates = new ArrayList<>();
        if (query.getAccountCode() != null) {
            predicates.add(entry.account.code.eq(query.getAccountCode()));
        }
        if (query.getPostDateTo() != null) {
            predicates.add(entry.postDate.loe(query.getPostDateTo()));
        }
        if (query.getBookingDateFrom() != null) {
            predicates.add(entry.bookingDate.goe(query.getBookingDateFrom()));
        }
        if (query.getBookingDateTo() != null) {
            predicates.add(entry.bookingDate.loe(query.getBookingDateTo()));
        }
        if (query.getLoanId() != null) {
            predicates.add(entry.loanId.eq(query.getLoanId()));
        }
        if (query.getClientId() != null) {
            predicates.add(entry.clientId.eq(query.getClientId()));
        }
        if (query.getPaymentId() != null) {
            predicates.add(entry.paymentId.eq(query.getPaymentId()));
        }
        return predicates.stream().toArray(Predicate[]::new);
    }

    @Data
    public static class AccountSums {
        private Long accountId;
        private BigDecimal debit = amount(0);
        private BigDecimal credit = amount(0);
    }

}
