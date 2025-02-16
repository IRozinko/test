/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.task;


import fintech.bo.db.jooq.task.tables.Agent;
import fintech.bo.db.jooq.task.tables.Log;
import fintech.bo.db.jooq.task.tables.TaskAttribute;
import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Task extends SchemaImpl {

    private static final long serialVersionUID = 1336973354;

    /**
     * The reference instance of <code>task</code>
     */
    public static final Task TASK = new Task();

    /**
     * The table <code>task.agent</code>.
     */
    public final Agent AGENT = fintech.bo.db.jooq.task.tables.Agent.AGENT;

    /**
     * The table <code>task.log</code>.
     */
    public final Log LOG = fintech.bo.db.jooq.task.tables.Log.LOG;

    /**
     * The table <code>task.task</code>.
     */
    public final fintech.bo.db.jooq.task.tables.Task TASK_ = fintech.bo.db.jooq.task.tables.Task.TASK_;

    /**
     * The table <code>task.task_attribute</code>.
     */
    public final TaskAttribute TASK_ATTRIBUTE = fintech.bo.db.jooq.task.tables.TaskAttribute.TASK_ATTRIBUTE;

    /**
     * No further instances allowed
     */
    private Task() {
        super("task", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            Agent.AGENT,
            Log.LOG,
            fintech.bo.db.jooq.task.tables.Task.TASK_,
            TaskAttribute.TASK_ATTRIBUTE);
    }
}
