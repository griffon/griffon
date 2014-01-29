package com.acme

import griffon.core.artifact.GriffonController
import griffon.metadata.ArtifactProviderFor
import org.apache.shiro.authz.annotation.*

@ArtifactProviderFor(GriffonController)
class ShiroController {
    ShiroModel model

    @RequiresGuest
    void guest() {
        model.value = 'guest'
    }

    @RequiresUser
    void user() {
        model.value = 'user'
    }

    @RequiresAuthentication
    void authenticated() {
        model.value = 'authenticated'
    }

    @RequiresRoles('manager')
    void roles() {
        model.value = 'roles'
    }

    @RequiresPermissions('user:write')
    void permissions() {
        model.value = 'permissions'
    }
}
