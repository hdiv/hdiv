package org.hdiv.services;

public interface TrustParameterDefinitionBuilder {
	TrustParameterDefinitionBuilder readOnly(boolean readOnly);

	TrustParameterDefinitionBuilder required(boolean required);

	TrustParameterDefinitionBuilder pattern(String pattern);

	TrustParameterDefinitionBuilder max(int max);

	TrustParameterDefinitionBuilder min(int min);

	TrustParameterDefinitionBuilder maxLength(int max);

	TrustParameterDefinitionBuilder minLength(int min);

}
