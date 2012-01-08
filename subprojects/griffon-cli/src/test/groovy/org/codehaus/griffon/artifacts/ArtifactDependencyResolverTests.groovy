package org.codehaus.griffon.artifacts

import griffon.util.GriffonUtil
import org.codehaus.griffon.artifacts.model.Artifact
import org.codehaus.griffon.artifacts.model.Plugin
import org.codehaus.griffon.artifacts.model.Release

class ArtifactDependencyResolverTests extends GroovyTestCase {
    private MockArtifactRepository repository = new MockArtifactRepository()
    private Map<String, Artifact> artifacts = [:]

    protected void setUp() {
        super.setUp()

        artifacts.one = makePlugin('one')
        artifacts.two = makePlugin('two')
        artifacts.three = makePlugin('three')

        repository.artifacts.plugin = artifacts
        ArtifactRepositoryRegistry.instance.registerRepository(repository)
    }

    void testResolveReleaseNotFound() {
        ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
        ArtifactDependency dependency = resolver.resolveDependencyTree(Plugin.TYPE, 'four')
        assert dependency
        assert !dependency.resolved
    }

    void testResolveReleaseWithOneDependencyNotFound() {
        artifacts.one.releases[0].dependencies = [
                [name: 'four', version: '1.0.0']
        ]
        ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
        ArtifactDependency dependency = resolver.resolveDependencyTree(Plugin.TYPE, 'one')
        assert dependency
        assert !dependency.resolved
        assert dependency.version == '2.2.2'
        assert dependency.dependencies
        assert dependency.dependencies[0].name == 'four'
        assert !dependency.dependencies[0].resolved
    }

    void testResolveReleaseWithNoDependencies() {
        ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
        ArtifactDependency dependency = resolver.resolveDependencyTree(Plugin.TYPE, 'one')
        assert dependency
        assert dependency.resolved
        assert dependency.version == '2.2.2'
        assert !dependency.dependencies
    }

    void testResolveReleaseWithOneDependency() {
        artifacts.one.releases[0].dependencies = [
                [name: 'two', version: '1.0.0']
        ]
        ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
        ArtifactDependency dependency = resolver.resolveDependencyTree(Plugin.TYPE, 'one')
        assert dependency
        assert dependency.resolved
        assert dependency.version == '2.2.2'
        assert dependency.dependencies
        assert dependency.dependencies[0].name == 'two'
        assert dependency.dependencies[0].version == '1.0.0'
    }

    void testResolveSetWithSingleReleaseWithNoDependencies() {
        ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
        Map<String, String> target = [one: '2.2.2']
        List<ArtifactDependency> dependencies = resolver.resolveDependencyTree(Plugin.TYPE, target)
        assert dependencies
        assert dependencies.size() == 1
        assert dependencies.grep {it.resolved}
        assert !dependencies[0].dependencies
    }

    void testResolveSetWithTwoReleasesWithNoDependencies() {
        ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
        Map<String, String> target = [one: '2.2.2', three: '0.0.1']
        List<ArtifactDependency> dependencies = resolver.resolveDependencyTree(Plugin.TYPE, target)
        assert dependencies
        assert dependencies.size() == 2
        assert dependencies.grep {it.resolved}
        assert !dependencies[0].dependencies
        assert !dependencies[1].dependencies
    }

    void testResolveEvictionsWithNoInstalledReleases_withOneTarget() {
        ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
        ArtifactDependency dependency = resolver.resolveDependencyTree(Plugin.TYPE, 'one')
        List<ArtifactDependency> installPlan = resolver.resolveEvictions([], [dependency])
        assert installPlan
        assert installPlan.size() == 1
        assert installPlan[0] == dependency
        assert installPlan[0].resolved
    }

    void testResolveEvictionsWithNoInstalledReleases_withTwoTargets() {
        ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
        Map<String, String> target = [one: '2.2.2', three: '0.0.1']
        List<ArtifactDependency> dependencies = resolver.resolveDependencyTree(Plugin.TYPE, target)
        List<ArtifactDependency> installPlan = resolver.resolveEvictions([], dependencies)
        assert installPlan
        assert installPlan.size() == 2
        assert installPlan[0] == dependencies[0]
        assert installPlan[0].resolved
        assert installPlan[1] == dependencies[1]
        assert installPlan[1].resolved
    }

    void testResolveEvictionsWithInstalledReleases_withOneTarget_noInstallPlan_1() {
        List<ArtifactDependency> installed = [new ArtifactDependency('one')]
        installed[0].version = '2.2.2'
        installed[0].installed = true
        installed[0].resolved = true
        ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
        ArtifactDependency dependency = resolver.resolveDependencyTree(Plugin.TYPE, 'one')
        assert dependency.resolved
        List<ArtifactDependency> installPlan = resolver.resolveEvictions(installed, [dependency])
        assert !installPlan
    }

    void testResolveEvictionsWithInstalledReleases_withOneTarget_noInstallPlan_2() {
        List<ArtifactDependency> installed = [new ArtifactDependency('one')]
        installed[0].version = '2.2.2'
        installed[0].installed = true
        installed[0].resolved = true
        ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
        ArtifactDependency dependency = resolver.resolveDependencyTree(Plugin.TYPE, 'one', '2.0.0')
        assert dependency.resolved
        List<ArtifactDependency> installPlan = resolver.resolveEvictions(installed, [dependency])
        assert !installPlan
    }

    void testResolveEvictionsWithInstalledReleases_withOneTarget_installedEvicted() {
        List<ArtifactDependency> installed = [new ArtifactDependency('one')]
        installed[0].version = '2.0.0'
        installed[0].installed = true
        installed[0].resolved = true
        ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
        ArtifactDependency dependency = resolver.resolveDependencyTree(Plugin.TYPE, 'one', '2.1.0')
        assert dependency.resolved
        List<ArtifactDependency> installPlan = resolver.resolveEvictions(installed, [dependency])
        assert installPlan
        assert installPlan[0] == installed[0]
        assert installPlan[0].installed && installPlan[0].evicted
        assert installPlan[1] == dependency
    }

    void testResolveEvictionsWithInstalledReleases_withOneTarget_conflict() {
        List<ArtifactDependency> installed = [new ArtifactDependency('one')]
        installed[0].version = '1.0.0'
        installed[0].installed = true
        installed[0].resolved = true
        ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
        ArtifactDependency dependency = resolver.resolveDependencyTree(Plugin.TYPE, 'one', '2.0.0')
        assert dependency.resolved
        List<ArtifactDependency> installPlan = resolver.resolveEvictions(installed, [dependency])
        assert !installPlan
        assert installed[0].conflicted
        assert dependency.conflicted
    }

    void testResolveEvictionsWithInstalledReleases_withOneTarget_forcedUpgradeOnMajor() {
        List<ArtifactDependency> installed = [new ArtifactDependency('one')]
        installed[0].version = '1.0.0'
        installed[0].installed = true
        installed[0].resolved = true
        System.setProperty(ArtifactDependencyResolver.KEY_FORCE_UPGRADE, 'true')
        try {
            ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
            ArtifactDependency dependency = resolver.resolveDependencyTree(Plugin.TYPE, 'one', '2.0.0')
            assert dependency.resolved
            List<ArtifactDependency> installPlan = resolver.resolveEvictions(installed, [dependency])
            assert installPlan
            assert installPlan.size() == 2
            assert installed[0].evicted
            assert installPlan[1] == dependency
        } finally {
            System.setProperty(ArtifactDependencyResolver.KEY_FORCE_UPGRADE, 'false')
        }
    }

    void testResolveEvictionsWithInstalledReleases_withOneTarget_noconflict() {
        List<ArtifactDependency> installed = [new ArtifactDependency('one')]
        installed[0].version = '2.0.0'
        installed[0].installed = true
        installed[0].resolved = true
        ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
        ArtifactDependency dependency = resolver.resolveDependencyTree(Plugin.TYPE, 'one', '1.0.0')
        assert dependency.resolved
        List<ArtifactDependency> installPlan = resolver.resolveEvictions(installed, [dependency])
        assert !installPlan
        assert !installed[0].conflicted
        assert dependency.evicted
    }

    void testResolveEvictionsWithInstalledReleases_withTwoTargets_noInstallPlan_1() {
        List<ArtifactDependency> installed = [new ArtifactDependency('one'), new ArtifactDependency('two')]
        installed[0].version = '2.2.2'
        installed[0].installed = true
        installed[0].resolved = true
        installed[1].version = '2.0.0'
        installed[1].installed = true
        installed[1].resolved = true
        ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
        Map<String, String> target = [one: '2.2.2', two: '2.0.0']
        List<ArtifactDependency> dependencies = resolver.resolveDependencyTree(Plugin.TYPE, target)
        assert dependencies
        assert dependencies.size() == 2
        assert dependencies.grep {it.resolved}
        List<ArtifactDependency> installPlan = resolver.resolveEvictions(installed, dependencies)
        assert !installPlan
    }

    void testResolveEvictionsWithInstalledReleases_withTwoTargets_noInstallPlan_2() {
        List<ArtifactDependency> installed = [new ArtifactDependency('one'), new ArtifactDependency('two')]
        installed[0].version = '2.2.2'
        installed[0].installed = true
        installed[0].resolved = true
        installed[1].version = '2.1.0'
        installed[1].installed = true
        installed[1].resolved = true
        ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
        Map<String, String> target = [one: '2.1.0', two: '2.1.0']
        List<ArtifactDependency> dependencies = resolver.resolveDependencyTree(Plugin.TYPE, target)
        assert dependencies
        assert dependencies.size() == 2
        assert dependencies.grep {it.resolved}
        List<ArtifactDependency> installPlan = resolver.resolveEvictions(installed, dependencies)
        assert !installPlan
    }

    void testResolveEvictionsWithNoInstalledReleases_withTwoTargets_withDependencies() {
        artifacts.one.releases[0].dependencies = [
                [name: 'two', version: '1.0.0']
        ]

        ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
        Map<String, String> target = [one: '2.2.2', two: '1.0.0']
        List<ArtifactDependency> dependencies = resolver.resolveDependencyTree(Plugin.TYPE, target)
        List<ArtifactDependency> installPlan = resolver.resolveEvictions([], dependencies)
        assert installPlan
        assert installPlan.size() == 2
        assert installPlan[0] == dependencies[0].dependencies[0]
        assert installPlan[1] == dependencies[0]

        // change the order of the installed deps, same result should be obtained

        target = [two: '1.0.0', one: '2.2.2']
        dependencies = resolver.resolveDependencyTree(Plugin.TYPE, target)
        installPlan = resolver.resolveEvictions([], dependencies)
        assert installPlan
        assert installPlan.size() == 2
        assert installPlan[0] == dependencies[0]
        assert installPlan[1] == dependencies[1]
    }

    Artifact makePlugin(String name) {
        Plugin plugin = new Plugin(
                name: name,
                title: name,
                description: name,
                license: name,
        )

        def n = [0, 1, 2]
        [n, n, n].combinations()
        plugin.releases = [n, n, n].combinations().collect([]) { version ->
            new Release(
                    version: version.join('.'),
                    griffonVersion: "${GriffonUtil.griffonVersion} > *",
                    date: new Date(),
                    artifact: plugin
            )
        }

        plugin.releases.sort {a, b -> b.version <=> a.version}

        plugin
    }
}
