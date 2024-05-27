package fintech.admintools.impl;

import fintech.admintools.AdminActionContext;

public class AdminActionContextImpl implements AdminActionContext {

    private final Long id;
    private final AdminActionsHelper helper;
    private final String params;

    public AdminActionContextImpl(Long id, AdminActionsHelper helper, String params) {
        this.id = id;
        this.helper = helper;
        this.params = params;
    }

    @Override
    public String getParams() {
        return this.params;
    }

    @Override
    public void failed(String message) {
        helper.failed(id, message);
    }

    @Override
    public void updateProgress(String message) {
        helper.updateProgress(id, message);
    }

    @Override
    public boolean isRunning() {
        return helper.isRunning(id);
    }
}
