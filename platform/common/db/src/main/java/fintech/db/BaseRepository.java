package fintech.db;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.LockModeType;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;


@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, QueryDslPredicateExecutor<T> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    T lock(ID id);

    T getRequired(ID id);

    Optional<T> getOptional(ID id);

    Optional<T> getOptional(Predicate predicate);

    Optional<ID> getOptionalId(Predicate predicate);

    Optional<T> findFirst(Predicate predicate, OrderSpecifier<?>... orders);

    List<T> findAll(Predicate predicate);

    List<T> findAll(Predicate predicate, Sort sort);

    List<T> findAll(Predicate predicate, OrderSpecifier<?>... orders);

    List<T> findAll(OrderSpecifier<?>... orders);

    T findOneOrNull(Predicate predicate);

}
