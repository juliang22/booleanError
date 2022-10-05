package com.example.helloappian.templates;

import static com.example.helloappian.templates.HelloAppianCSP.CS_PROP_KEY;

import java.util.HashMap;
import java.util.Map;

import com.appian.connectedsystems.simplified.sdk.SimpleIntegrationTemplate;
import com.appian.connectedsystems.simplified.sdk.configuration.SimpleConfiguration;
import com.appian.connectedsystems.templateframework.sdk.ExecutionContext;
import com.appian.connectedsystems.templateframework.sdk.IntegrationResponse;
import com.appian.connectedsystems.templateframework.sdk.TemplateId;
import com.appian.connectedsystems.templateframework.sdk.configuration.PropertyPath;
import com.appian.connectedsystems.templateframework.sdk.configuration.RefreshPolicy;
import com.appian.connectedsystems.templateframework.sdk.diagnostics.IntegrationDesignerDiagnostic;
import com.appian.connectedsystems.templateframework.sdk.metadata.IntegrationTemplateRequestPolicy;
import com.appian.connectedsystems.templateframework.sdk.metadata.IntegrationTemplateType;

// Must provide an integration id. This value need only be unique for this connected system
@TemplateId(name="HelloAppianIntegrationTemplate")
// Set template type to READ since this integration does not have side effects
@IntegrationTemplateType(IntegrationTemplateRequestPolicy.READ)
public class HelloAppianIntegrationTemplate extends SimpleIntegrationTemplate {

  public static final String INTEGRATION_PROP_KEY = "intProp";

  @Override
  protected SimpleConfiguration getConfiguration(
    SimpleConfiguration integrationConfiguration,
    SimpleConfiguration connectedSystemConfiguration,
    PropertyPath propertyPath,
    ExecutionContext executionContext) {

    Boolean checked = integrationConfiguration.getValue(INTEGRATION_PROP_KEY);
    return integrationConfiguration.setProperties(
        booleanProperty(INTEGRATION_PROP_KEY).label("Conditional property to control integration " +
                "properties.")
            .isExpressionable(true)
            .description("For some reason, using the expression editor to " +
                "check this property does not work.")
            .refresh(RefreshPolicy.ALWAYS)
            .build(),
        documentProperty("document")
            .isExpressionable(true)
            .label("File")
            .instructionText("File to be uploaded")
            .isHidden(checked != null && checked ? false : true)
            .isRequired(checked != null && checked ? true : false)
            .build(),
        textProperty("folder")
            .isExpressionable(true)
            .label("Folder Name")
            .instructionText("Folder to upload file into")
            .isHidden(checked != null && checked ? false : true)
            .isRequired(checked != null && checked ? true : false)
            .build()
    );

  }

  @Override
  protected IntegrationResponse execute(
      SimpleConfiguration integrationConfiguration,
      SimpleConfiguration connectedSystemConfiguration,
      ExecutionContext executionContext) {
    Map<String,Object> requestDiagnostic = new HashMap<>();
    String csValue = connectedSystemConfiguration.getValue(CS_PROP_KEY);
    requestDiagnostic.put("csValue", csValue);
    String integrationValue = integrationConfiguration.getValue(INTEGRATION_PROP_KEY);
    requestDiagnostic.put("integrationValue", integrationValue);
    Map<String,Object> result = new HashMap<>();

    // Important for debugging to capture the amount of time it takes to interact
    // with the external system. Since this integration doesn't interact
    // with an external system, we'll just log the calculation time of concatenating the strings
    final long start = System.currentTimeMillis();
    result.put("hello", "world");
    result.put("concat", csValue + integrationValue);
    final long end = System.currentTimeMillis();

    final long executionTime = end - start;
    final IntegrationDesignerDiagnostic diagnostic = IntegrationDesignerDiagnostic.builder()
        .addExecutionTimeDiagnostic(executionTime)
        .addRequestDiagnostic(requestDiagnostic)
        .build();

    return IntegrationResponse
        .forSuccess(result)
        .withDiagnostic(diagnostic)
        .build();
  }
}
