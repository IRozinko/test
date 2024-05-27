package fintech.nordigen.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import fintech.DateUtils;
import fintech.PojoUtils;
import fintech.Validate;
import fintech.nordigen.json.AccountList;
import fintech.nordigen.json.NordigenJson;
import fintech.nordigen.json.TransactionList;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NordigenParser {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final NordigenJson json;

    public NordigenParser(NordigenJson json) {
        this.json = Validate.notNull(json, "Null json");
    }

    public double sum(String accountNumber, Set<Integer> categories, LocalDate periodFrom, LocalDate periodTo, boolean onlyNegative, boolean onlyPositive) {
        List<TransactionList> transactions = findTransactions(accountNumber, categories, periodFrom, periodTo, onlyNegative, onlyPositive);
        double sum = transactions.stream().map(TransactionList::getSum).mapToDouble(Double::doubleValue).sum();
        return sum;
    }

    public long countTransactions(String accountNumber, LocalDate periodFrom, LocalDate periodTo) {
        List<AccountList> accountList = PojoUtils.npeSafe(json::getAccountList).orElse(ImmutableList.of());
        return accountList
            .stream()
            .filter(accountNumberEquals(accountNumber))
            .flatMap(a -> a.getTransactionList().stream())
            .filter(transactionMatches(periodFrom, periodTo))
            .count();
    }

    public long countAllTransactions(String accountNumber) {
        List<AccountList> accountList = PojoUtils.npeSafe(json::getAccountList).orElse(ImmutableList.of());
        return accountList
            .stream()
            .filter(accountNumberEquals(accountNumber))
            .mapToLong(a -> a.getTransactionList().size())
            .sum();
    }

    public List<TransactionList> findTransactions(String accountNumber, Set<Integer> categories, LocalDate periodFrom, LocalDate periodTo, boolean onlyNegative, boolean onlyPositive) {
        List<AccountList> accountList = PojoUtils.npeSafe(json::getAccountList).orElse(ImmutableList.of());
        return accountList
            .stream()
            .filter(accountNumberEquals(accountNumber))
            .flatMap(a -> a.getTransactionList().stream())
            .filter(transactionMatches(categories, periodFrom, periodTo, onlyNegative, onlyPositive))
            .collect(Collectors.toList());
    }

    private Predicate<TransactionList> transactionMatches(Set<Integer> categories, LocalDate periodFrom, LocalDate periodTo, boolean onlyNegative, boolean onlyPositive) {
        return t -> {
            LocalDate date = LocalDate.parse(t.getDate(), DATE_FORMAT);
            boolean inPeriod = DateUtils.goe(date, periodFrom) && DateUtils.loe(date, periodTo);

            Integer mainCategory = PojoUtils.npeSafe(() -> t.getCategory().getId()).orElse(-1);
            Set<Integer> otherCategories = PojoUtils.npeSafe(() -> t.getCategory().getHierarchy()).orElse(ImmutableList.of()).stream().map(h -> Integer.valueOf(h.getId())).collect(Collectors.toSet());
            boolean inCategory = categories.contains(mainCategory) || !Sets.intersection(otherCategories, categories).isEmpty();

            boolean matchesNegative = !onlyNegative || t.getSum() < 0;
            boolean matchesPositive = !onlyPositive || t.getSum() > 0;

            return inPeriod && inCategory && matchesNegative && matchesPositive;
        };
    }

    private Predicate<TransactionList> transactionMatches(LocalDate periodFrom, LocalDate periodTo) {
        return t -> {
            LocalDate date = LocalDate.parse(t.getDate(), DATE_FORMAT);
            boolean inPeriod = DateUtils.goe(date, periodFrom) && DateUtils.loe(date, periodTo);
            return inPeriod;
        };
    }

    private Predicate<AccountList> accountNumberEquals(String accountNumber) {
        return a -> {
            String numberA = StringUtils.replace(a.getAccountNumber(), " ", "");
            String numberB = StringUtils.replace(accountNumber, " ", "");
            return StringUtils.equalsIgnoreCase(numberA, numberB);
        };
    }
}
