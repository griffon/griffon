package org.codehaus.griffon.runtime.core.resources;

import griffon.core.resources.InjectedResource;

public class Bean {
    @InjectedResource
    private String privateField;

    private String fieldBySetter;

    @InjectedResource
    public void setValue(String value) {
        this.fieldBySetter = value;
    }

    @InjectedResource(key = "sample.key.no_args")
    private String fieldWithKey;

    @InjectedResource(key = "sample.key.with_args", args = {"1", "2"})
    private String fieldWithKeyAndArgs;

    @InjectedResource(key = "sample.key.no_args.with_default", defaultValue = "DEFAULT_NO_ARGS")
    private String fieldWithKeyNoArgsWithDefault;

    @InjectedResource(key = "sample.key.no_args.with_default", args = {"1", "2"}, defaultValue = "DEFAULT_WITH_ARGS")
    private String fieldWithKeyWithArgsWithDefault;
}
