alter table workflow.activity
    add column ui_state text;

alter table workflow.activity_audit
    add column ui_state text;
