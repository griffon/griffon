package org.example.calculator;

import griffon.util.CollectionUtils;
import org.codehaus.griffon.runtime.core.addon.AbstractGriffonAddon;

import javax.annotation.Nonnull;
import javax.inject.Named;
import java.util.Map;

@Named("calculator")
public class CalculatorAddon extends AbstractGriffonAddon {
    @Nonnull
    @Override
    public Map<String, Map<String, Object>> getMvcGroups() {
        return CollectionUtils.<String, Map<String, Object>>map()
            .e("calculator", CollectionUtils.<String, Object>map()
                    .e("view", "org.example.calculator.CalculatorView")
                    .e("controller", "org.example.calculator.CalculatorController")
            );
    }
}
