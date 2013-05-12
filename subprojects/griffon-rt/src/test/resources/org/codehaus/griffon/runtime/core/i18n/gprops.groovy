package org.codehaus.griffon.runtime.core.i18n

healthy {
    proverb {
        index = 'An {0} a day keeps the {1} away'
        map = 'An {:fruit} a day keeps the {:occupation} away'
        closure = { arg0, arg1 -> "An ${arg0} a day keeps the ${arg1} away" }
    }
}
famous{
    quote {
        index = 'This is {0}!'
        map = 'This is {:location}!'
        closure = { arg0 -> "This is ${arg0}!" }
    }
}
