/*
 * Copyright 2010-2011 the original author or authors.
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
 * limitations under the License.
 */

ruleset {    
    ruleset('rulesets/imports.xml')
    ruleset('rulesets/basic.xml') {
        exclude 'EmptyCatchBlock'
		exclude 'EmptyMethod'
    }
    /*
	ruleset('rulesets/naming.xml') {
        exclude 'PropertyName'
        'ClassName' {
            regex = '^[A-Z][a-zA-Z0-9]*$'
        }
        'FieldName' {
            finalRegex = '^_?[a-z][a-zA-Z0-9]*$'
            staticFinalRegex = '^[A-Z][A-Z_0-9]*$'
        }
        'MethodName' {
            regex = '^[a-z][a-zA-Z0-9_]*$'
        }
        'VariableName' {
            finalRegex = '^_?[a-z][a-zA-Z0-9]*$'
        }
    }
    // ruleset('rulesets/unused.xml')
    ruleset('rulesets/exceptions.xml') {
        exclude 'CatchException'
        exclude 'CatchThrowable'
        exclude 'ReturnNullFromCatchBlock'
    }
    ruleset('rulesets/generic.xml') {
        exclude 'StatelessClass'
    }
    ruleset('rulesets/concurrency.xml')

    ruleset('rulesets/logging.xml') {
        exclude 'Println'       //TODO: Fix the code and enable rule
        exclude 'PrintStackTrace'    //TODO: Fix the code and enable rule
        exclude 'SystemErrPrint'    //TODO: Fix the code and enable rule
    }

    ruleset('rulesets/braces.xml') {
       exclude 'IfStatementBraces'    // TODO: analyze usage and verify correct code 
        exclude 'WhileStatementBraces'    //TODO: analyze usage and verify correct code 
        exclude 'ElseBlockBraces'    //TODO: analyze usage and verify correct code 
        exclude 'ForStatementBraces'    //TODO: analyze usage and verify correct code 
    }

    ruleset('rulesets/size.xml') {
        exclude 'AbcComplexity'    //TODO: Fix the code and enable rule
        exclude 'MethodCount'    //TODO: Fix the code and enable rule
        exclude 'MethodSize'    //TODO: Fix the code and enable rule
        exclude 'CyclomaticComplexity'    //TODO: Fix the code and enable rule
        exclude 'NestedBlockDepth'    //TODO: Fix the code and enable rule
    }

    ruleset('rulesets/junit.xml') {
        exclude 'JUnitStyleAssertions'       //TODO: Fix the code and enable rule
        exclude 'JUnitSetUpCallsSuper'       //TODO: Fix the code and enable rule
        exclude 'JUnitTearDownCallsSuper'       //TODO: Fix the code and enable rule
        exclude 'UseAssertTrueInsteadOfAssertEquals' //TODO: Fix the code and enable rule
        exclude 'JUnitPublicNonTestMethod' //TODO: Fix the code and enable rule
    }

    ruleset('rulesets/concurrency.xml') {
        exclude 'SynchronizedMethod'         //TODO: Fix the code and enable rule
    }

    ruleset('rulesets/unnecessary.xml') {
        exclude 'UnnecessaryConstructor'
        exclude 'UnnecessaryReturnKeyword'   //TODO: Fix the code and enable rule
    }

    ruleset('rulesets/dry.xml') {
        exclude 'DuplicateStringLiteral' // TODO: analyze usage and verify correct code
        exclude 'DuplicateNumberLiteral'    //TODO: Fix the code and enable rule
    }

    ruleset('rulesets/design.xml')

    ruleset('rulesets/unused.xml') {
        exclude 'UnusedPrivateField'    //TODO: Fix the code and enable rule
        exclude 'UnusedPrivateMethod'   //TODO: Fix the code and enable rule
        exclude 'UnusedVariable'        //TODO: Fix the code and enable rule
    }*/
}
