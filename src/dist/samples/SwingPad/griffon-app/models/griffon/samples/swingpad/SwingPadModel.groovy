/*
 * Copyright 2007-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */

package griffon.samples.swingpad

import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.EventList
import ca.odell.glazedlists.SortedList
import org.fife.ui.autocomplete.BasicCompletion
import org.fife.ui.autocomplete.CompletionProvider
import org.fife.ui.autocomplete.DefaultCompletionProvider

import java.awt.Font

/**
 * @author Andres Almiray
 */
class SwingPadModel {
    @Bindable String status = ''
    @Bindable String rowAndCol = '1:1'
    @Bindable String currentSampleId = ''
    @Bindable boolean layout = true
    Map samples = [:]

    @Bindable boolean dirty = false
    @Bindable String code = ''
    @Bindable File scriptFile
    @Bindable String stylesheet = ''
    @Bindable String errors
    @Bindable Font font
    @Bindable boolean success

    final List recentScripts = [] as LinkedList
    final CompletionProvider codeCompletionProvider = createCompletionProvider()
    EventList nodes = new SortedList(new BasicEventList(),
            {a, b -> a.node <=> b.node} as Comparator)

    void mvcGroupInit(Map args) {
        status = "Welcome to ${GriffonNameUtils.capitalize(app.getMessage('application.title'))}"
        gatherNodeNames()
    }

    synchronized void addRecentScript(script, prefs) {
        def scriptCopy = recentScripts.find { it.absolutePath == script.absolutePath }
        if (scriptCopy) recentScripts.remove(scriptCopy)
        recentScripts.addFirst(script)
        if (recentScripts.size() > 10) recentScripts.removeLast()

        prefs.put('recentScripts.list.size', recentScripts.size().toString())
        recentScripts.eachWithIndex { file, i ->
            prefs.put("recentScripts.${i}.file", file.absolutePath)
        }
    }

    private void gatherNodeNames() {
        def ub = app.builders.SwingPad
        def groups = []

        ub.builderRegistration.each { ubr ->
            def builder = ubr.builder
            def oldProxy = builder.proxyBuilder
            try {
                builder.proxyBuilder = builder
                def builderName = builder.getClass().name
                builderName = builderName.substring(builderName.lastIndexOf('.') + 1)
                builder.getRegistrationGroups().each { group ->
                    def groupSet = builder.getRegistrationGroupItems(group)
                    if (group && groupSet) {
                        try {
                            builder.getClass().getDeclaredMethod("register$group", [] as Class[])
                            groupSet.each { node ->
                                groups << [
                                        builder: builderName,
                                        group: group,
                                        node: node
                                ]
                                method(codeCompletionProvider, node)
                            }
                        } catch (NoSuchMethodException nsme) {
                            // ignore
                        }
                    } else if (!group && groupSet) {
                        groupSet.each { node ->
                            if (node.startsWith('classicSwing:') || groups.find {it.node == node}) return
                            groups << [
                                    builder: builderName,
                                    group: builderName,
                                    node: ubr.prefixString + node
                            ]
                            method(codeCompletionProvider, ubr.prefixString + node)
                        }
                    }
                }
            } finally {
                builder.proxyBuilder = oldProxy
            }
        }

        app.addonManager.addons.each { addonName, addon ->
            try {
                addon.factories?.each { node, factory ->
                    addonName -= 'GriffonAddon'
                    groups << [
                            builder: addonName,
                            group: 'Addon',
                            node: node
                    ]
                    method(codeCompletionProvider, node)
                }
            } catch (MissingPropertyException mpe) {
                // ignore
            }
        }

        synchronized (nodes) {
            nodes.clear()
            nodes.addAll(groups)
        }
    }

    private CompletionProvider createCompletionProvider() {
        // A DefaultCompletionProvider is the simplest concrete implementation
        // of CompletionProvider.  This provider has no understanding of
        // language semantics. It simply checks the text entered up to the
        // caret position for a match against known completions. This is all
        // that is needed in the majority of cases.
        DefaultCompletionProvider provider = new DefaultCompletionProvider()

        // Add completions for all Java keywords.  A BasicCompletion is just
        // a straightforward word completion.
        keyword(provider, 'abstract')
        keyword(provider, 'assert')
        keyword(provider, 'as')
        keyword(provider, 'break')
        keyword(provider, 'case')
        keyword(provider, 'catch')
        keyword(provider, 'class')
        keyword(provider, 'const')
        keyword(provider, 'continue')
        keyword(provider, 'default')
        keyword(provider, 'do')
        keyword(provider, 'else')
        keyword(provider, 'enum')
        keyword(provider, 'extends')
        keyword(provider, 'final')
        keyword(provider, 'finally')
        keyword(provider, 'for')
        keyword(provider, 'goto')
        keyword(provider, 'if')
        keyword(provider, 'implements')
        keyword(provider, 'import')
        keyword(provider, 'in')
        keyword(provider, 'instanceof')
        keyword(provider, 'interface')
        keyword(provider, 'native')
        keyword(provider, 'new')
        keyword(provider, 'package')
        keyword(provider, 'private')
        keyword(provider, 'protected')
        keyword(provider, 'public')
        keyword(provider, 'return')
        keyword(provider, 'static')
        keyword(provider, 'strictfp')
        keyword(provider, 'super')
        keyword(provider, 'switch')
        keyword(provider, 'synchronized')
        keyword(provider, 'this')
        keyword(provider, 'throw')
        keyword(provider, 'throws')
        keyword(provider, 'transient')
        keyword(provider, 'try')
        keyword(provider, 'void')
        keyword(provider, 'volatile')
        keyword(provider, 'while')

        provider
    }

    private static void keyword(CompletionProvider provider, String text) {
        provider.addCompletion(new KeywordCompletion(provider, text))
    }

    private static void method(CompletionProvider provider, String text) {
        provider.addCompletion(new NodeCompletion(provider, text))
    }

    static class KeywordCompletion extends BasicCompletion {
        KeywordCompletion(CompletionProvider provider, String text) {
            super(provider, text)
        }

        String getReplacementText() {
            super.getReplacementText() + ' '
        }

        String getInputText() {
            getReplacementText()[0..-2]
        }
    }

    static class NodeCompletion extends BasicCompletion {
        NodeCompletion(CompletionProvider provider, String text) {
            super(provider, text)
        }

        String getReplacementText() {
            super.getReplacementText() + '()'
        }

        String getInputText() {
            getReplacementText()[0..-3]
        }
    }
}
