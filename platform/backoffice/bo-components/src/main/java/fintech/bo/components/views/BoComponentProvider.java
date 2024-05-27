package fintech.bo.components.views;

public interface BoComponentProvider {

    BoComponent build(BoComponentContext context);

    BoComponentMetadata metadata();
}
