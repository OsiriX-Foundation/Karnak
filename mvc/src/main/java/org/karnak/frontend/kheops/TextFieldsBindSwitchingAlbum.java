package org.karnak.frontend.kheops;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.dcm4che6.data.DicomObject;
import org.json.JSONObject;
import org.karnak.backend.api.KheopsApi;
import org.karnak.backend.data.entity.KheopsAlbums;
import org.karnak.backend.model.expression.ExprConditionKheops;
import org.karnak.backend.model.expression.ExpressionError;
import org.karnak.backend.model.expression.ExpressionResult;
import org.karnak.backend.service.kheops.SwitchingAlbum;

public class TextFieldsBindSwitchingAlbum {

  private final KheopsApi kheopsApi;
  private final Binder<KheopsAlbums> binder;

  private final TextField textUrlAPI;
  private final TextField textAuthorizationDestination;
  private final TextField textAuthorizationSource;
  private final TextField textCondition;
  private ExpressionError expressionError;
  private final Span textErrorConditionMsg;

  public TextFieldsBindSwitchingAlbum() {
    kheopsApi = new KheopsApi();
    textUrlAPI = new TextField();
    textAuthorizationDestination = new TextField();
    textAuthorizationSource = new TextField();
    textCondition = new TextField();
    textErrorConditionMsg = new Span();
        expressionError = new ExpressionError(true, "");
        binder = buildBinder();

    }

    private Binder<KheopsAlbums> buildBinder() {
        Binder<KheopsAlbums> b = new BeanValidationBinder<>(KheopsAlbums.class);
        b.forField(textAuthorizationDestination)
                .withValidator(StringUtils::isNotBlank,"Token destination is mandatory")
                .withValidator(value -> {
                    if (!textUrlAPI.getValue().isBlank()) {
                        return validateToken(value, textUrlAPI.getValue(), SwitchingAlbum.MIN_SCOPE_DESTINATION);
                    }
                    return true;
                }, "Token can't be validate, minimum permissions: [write]")
                .bind(KheopsAlbums::getAuthorizationDestination, KheopsAlbums::setAuthorizationDestination);
        b.forField(textAuthorizationSource)
                .withValidator(StringUtils::isNotBlank,"Token source is mandatory")
                .withValidator(value -> {
                    if (!textUrlAPI.getValue().isBlank()) {
                        return validateToken(value, textUrlAPI.getValue(), SwitchingAlbum.MIN_SCOPE_SOURCE);
                    }
                    return true;
                }, "Token can't be validate, minimum permissions: [read, send]")
                .bind(KheopsAlbums::getAuthorizationSource, KheopsAlbums::setAuthorizationSource);
        b.forField(textUrlAPI)
                .withValidator(StringUtils::isNotBlank,"Url API is mandatory")
                .bind(KheopsAlbums::getUrlAPI, KheopsAlbums::setUrlAPI);
        b.forField(textCondition)
                .withValidator(value -> {
                    if (!textCondition.getValue().equals("")) {
                         expressionError = ExpressionResult.isValid(textCondition.getValue(),
                                new ExprConditionKheops(DicomObject.newDicomObject()),
                                Boolean.class);
                         textErrorConditionMsg.setText(expressionError.getMsg());
                         return expressionError.isValid();
                    }
                    textErrorConditionMsg.setText("");
                    return true;
                }, "Condition is not valid")
                .bind(KheopsAlbums::getCondition, KheopsAlbums::setCondition);
        return b;
    }

    private boolean validateToken(String token, String urlAPI, List<String> validMinScope) {
        try {
            JSONObject responseIntrospect = kheopsApi.tokenIntrospect(urlAPI, token, token);
            return SwitchingAlbum.validateIntrospectedToken(responseIntrospect, validMinScope);
        } catch (Exception e) {
            return false;
        }
    }

    public Binder<KheopsAlbums> getBinder() {
        return binder;
    }

    public TextField getTextUrlAPI() {
        return textUrlAPI;
    }

    public TextField getTextAuthorizationDestination() {
        return textAuthorizationDestination;
    }

    public TextField getTextAuthorizationSource() {
        return textAuthorizationSource;
    }

    public TextField getTextCondition() {
        return textCondition;
    }

    public Span getTextErrorConditionMsg() {
        return textErrorConditionMsg;
    }
}
