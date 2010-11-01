package org.codehaus.griffon.resolve

import org.apache.ivy.core.module.id.ModuleRevisionId

/**
 * @author Graeme Rocher
 * @since 1.3
 */
class GriffonRepoResolverTests extends GroovyTestCase {

    void testTransformGriffonRepositoryPattern() {
        def repoResolver = new GriffonRepoResolver("test", new URL("http://localhost"))

        def url = "http://localhost/griffon-[artifact]/tags/RELEASE_*/griffon-[artifact]-[revision].[ext]"
        ModuleRevisionId mrid = ModuleRevisionId.newInstance("org.griffon.plugins","feeds", "latest.integration")
        assertEquals "http://localhost/griffon-[artifact]/tags/LATEST_RELEASE/griffon-[artifact]-[revision].[ext]",repoResolver.transformGriffonRepositoryPattern(mrid, url)

        mrid = ModuleRevisionId.newInstance("org.griffon.plugins","feeds", "1.1")
        assertEquals "http://localhost/griffon-[artifact]/tags/RELEASE_1_1/griffon-[artifact]-[revision].[ext]",repoResolver.transformGriffonRepositoryPattern(mrid, url)
    }
}
